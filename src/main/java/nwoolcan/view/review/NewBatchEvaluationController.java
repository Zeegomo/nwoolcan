package nwoolcan.view.review;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nwoolcan.controller.Controller;
import nwoolcan.model.brewery.batch.review.BatchEvaluationType;
import nwoolcan.model.brewery.batch.review.EvaluationType;
import nwoolcan.utils.Result;
import nwoolcan.utils.Results;
import nwoolcan.view.InitializableController;
import nwoolcan.view.subview.SubViewController;
import nwoolcan.view.utils.ViewManager;
import nwoolcan.view.subview.SubView;
import nwoolcan.viewmodel.brewery.production.batch.review.BatchEvaluationDTO;
import nwoolcan.viewmodel.brewery.production.batch.review.NewBatchEvaluationViewModel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for new Batch Evaluation modal.
 */
@SuppressWarnings("NullAway")
public final class NewBatchEvaluationController extends SubViewController
    implements InitializableController<NewBatchEvaluationViewModel> {
    private static final String SCORE_PARSE_FAILED = "Invalid score";
    private static final String REVIEW_CREATION_FAILED = "An error occurred while creating the review.\n";
    @FXML
    private SubView modalSubView;
    @FXML
    private ComboBox<BatchEvaluationTypeProperty> batchTypeComboBox;
    @FXML
    private VBox categories;
    @FXML
    private TextArea notesTextArea;
    @FXML
    private TextField reviewerTextField;
    @FXML
    private Button createButton;

    //Map every evaluation type to its input properties (score and notes) and input validity property.
    private final Map<EvaluationType, Pair<Pair<Property<String>, Property<String>>, BooleanProperty>> evaluations = new HashMap<>();
    private int id;

    private static final class BatchEvaluationTypeProperty {
        private final BatchEvaluationType type;

        private BatchEvaluationTypeProperty(final BatchEvaluationType type) {
            this.type = type;
        }

        private BatchEvaluationType getType() {
            return this.type;
        }

        @Override
        public String toString() {
            return this.type.getName();
        }
    }

    /**
     * Creates itself and gets injected.
     *
     * @param controller  injected controller.
     * @param viewManager injected view manager.
     */
    public NewBatchEvaluationController(final Controller controller, final ViewManager viewManager) {
        super(controller, viewManager);
    }

    @Override
    protected SubView getSubView() {
        return this.modalSubView;
    }

    @Override
    public void initData(final NewBatchEvaluationViewModel data) {
        batchTypeComboBox.setItems(FXCollections.observableList(data.getTypes().stream()
                                                                    .map(BatchEvaluationTypeProperty::new)
                                                                    .collect(Collectors.toList())));
        this.id = data.getID();
        batchTypeComboBox.getSelectionModel().selectFirst();
        changeBatchTypeClick();
    }

    @FXML
    private void changeBatchTypeClick() {
        BatchEvaluationType type = this.batchTypeComboBox.getSelectionModel().getSelectedItem().getType();

        this.evaluations.clear();
        type.getCategories().forEach(cat ->
            this.evaluations.put(cat, Pair.of(
                Pair.of(new SimpleStringProperty(), new SimpleStringProperty()),
                new SimpleBooleanProperty()))
        );

        List<TitledPane> nodes = this.evaluations.entrySet()
                                           .stream()
                                           .map(entry -> new TitledPane(entry.getKey().getName(),
                                               EvaluationView.builder().bindWithInput(entry.getValue().getLeft())
                                                                          .bindWithInputValidity(entry.getValue().getRight())
                                                                          .enableInput(true)
                                                                          .checkInput(entry.getKey())
                                                                          .build(this.getViewManager()))
                                           )
                                           .collect(Collectors.toList());

        Accordion acc = new Accordion(nodes.stream().toArray(TitledPane[]::new));
        nodes.stream().findFirst().ifPresent(acc::setExpandedPane);
        this.categories.getChildren().clear();
        this.categories.getChildren().add(acc);

        Collection<ReadOnlyBooleanProperty> inputProperties = this.evaluations.values()
                                                                              .stream()
                                                                              .map(Pair::getRight)
                                                                              .collect(Collectors.toList());

        BooleanBinding inputValidityBinding = Bindings.createBooleanBinding(
            () -> inputProperties.stream().allMatch(ReadOnlyBooleanProperty::get),
            inputProperties.stream().toArray(Observable[]::new)
        );
        this.createButton.disableProperty().bind(inputValidityBinding.not());
    }

    @FXML
    private void createBatchReviewClick() {
        BatchEvaluationType type = this.batchTypeComboBox.getSelectionModel().getSelectedItem().getType();
        Result<Set<Triple<EvaluationType, Integer, Optional<String>>>> cat
            = this.evaluations.entrySet()
                              .stream()
                              .map(entry -> Triple.of(
                                  entry.getKey(),
                                  Results.ofChecked(() -> {
                                      //throw exception if there no value
                                      if (entry.getValue().getLeft().getLeft().getValue().isEmpty()) {
                                          throw new IllegalArgumentException("Empty value in " + entry.getKey()
                                                                                                      .getName());
                                      }
                                      return Integer.parseInt(entry.getValue().getLeft().getLeft().getValue());
                                  }).mapError(err ->
                                      //give more understandable error
                                      new IllegalArgumentException(SCORE_PARSE_FAILED + "\n" + err.getMessage())),
                                  Optional.ofNullable(entry.getValue().getLeft().getRight().getValue())))
                              .reduce(
                                  Result.of(new HashSet<>()),
                                  //propagate error in set
                                  (res, triple) -> res.flatMap(r -> triple.getMiddle()
                                                                          .map(rr -> {
                                                                              r.add(Triple.of(
                                                                                  triple.getLeft(),
                                                                                  rr,
                                                                                  triple.getRight()));
                                                                              return r;
                                                                          })),
                                  (res1, res2) -> res1.require(res2::isPresent, res2::getError)
                                                      .peek(col -> col.addAll(res2.getValue())));
        cat.map(eval -> new BatchEvaluationDTO(type, eval, this.notesTextArea.getText(), this.reviewerTextField.getText().trim()))
           .flatMap(dto -> this.getController().getBatchController().addBatchEvaluation(this.id, dto))
           .peekError(err -> this.showAlertAndWait(err.getMessage())).peek(res -> {
            final Stage stage = ((Stage) this.reviewerTextField.getScene().getWindow());
            //just for saying that I added review to the caller
            stage.setUserData(new Object());
            stage.close();
        });
    }

    private void showAlertAndWait(final String message) {
        this.showErrorAndWait(REVIEW_CREATION_FAILED + message,
            this.reviewerTextField.getScene().getWindow());
    }
}
