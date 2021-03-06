package nwoolcan.view.production;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import nwoolcan.controller.Controller;
import nwoolcan.model.brewery.batch.BatchMethod;
import nwoolcan.model.brewery.batch.misc.WaterMeasurement;
import nwoolcan.model.brewery.batch.step.parameter.ParameterTypeEnum;
import nwoolcan.model.utils.Quantity;
import nwoolcan.model.utils.UnitOfMeasure;
import nwoolcan.utils.Result;
import nwoolcan.view.AbstractViewController;
import nwoolcan.view.InitializableController;
import nwoolcan.view.utils.ViewManager;
import nwoolcan.viewmodel.brewery.production.batch.CreateBatchDTO;
import nwoolcan.viewmodel.brewery.production.batch.NewBatchViewModel;
import nwoolcan.viewmodel.brewery.warehouse.article.IngredientArticleViewModel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * View controller for new batch modal.
 */
@SuppressWarnings("NullAway")
public final class NewBatchModalController
    extends AbstractViewController implements InitializableController<NewBatchViewModel> {

    private static final String MUST_SELECT_ELEMENT_MESSAGE = "Must select a element!";
    private static final String REGISTRATION_VALUE_MUST_BE_NUMBER_MESSAGE = "Registration value must be a number!";
    private static final String MUST_SELECT_INGREDIENT_MESSAGE = "Must select an ingredient!";
    private static final String INGREDIENT_QUANTITY_MUST_BE_NUMBER_MESSAGE = "Ingredient quantity must be a number!";
    private static final String THERE_MUST_BE_A_BEER_NAME_MESSAGE = "There must be a beer name!";
    private static final String THERE_MUST_BE_A_BEER_STYLE_MESSAGE = "There must be a beer style!";
    private static final String MUST_SELECT_BATCH_METHOD_MESSAGE = "Must select a batch method!";
    private static final String THERE_MUST_BE_AN_INITIAL_SIZE_MESSAGE = "There must be an initial size!";
    private static final String INITIAL_SIZE_MUST_BE_NUMBER_MESSAGE = "Initial size must be a number!";
    private static final String INITIAL_SIZE_ERROR_MESSAGE = "Initial size : ";
    private static final String ERROR_CREATING_BATCH_MESSAGE = "An error occurred while creating the batch.";

    @FXML
    private Button addIngredientButton;
    @FXML
    private Label elementUnitOfMeasureSymbolLabel;
    @FXML
    private TextField beerNameTextField;
    @FXML
    private TextField beerStyleTextField;
    @FXML
    private TextField beerCategoryTextField;
    @FXML
    private Label initialSizeUnitOfMeasureLabel;
    @FXML
    private ComboBox<BatchMethodProperty> batchMethodsComboBox;
    @FXML
    private TextField initialSizeTextField;

    @FXML
    private TextField registrationValueTextField;
    @FXML
    private TableView<Pair<NumberUnitOfMeasureProperty, WaterMeasurement.Element>> elementsTableView;
    @FXML
    private ComboBox<WaterMeasurement.Element> elementsComboBox;

    @FXML
    private Label ingredientUnitOfMeasureLabel;
    @FXML
    private TextField quantityIngredientTextField;
    @FXML
    private TableView<Pair<NumberUnitOfMeasureProperty, IngredientArticleViewModel>> ingredientsTableView;
    @FXML
    private ComboBox<IngredientArticleViewModel> ingredientsComboBox;

    private static final class BatchMethodProperty {
        private final BatchMethod method;

        private BatchMethodProperty(final BatchMethod method) {
            this.method = method;
        }

        private BatchMethod getMethod() {
            return this.method;
        }

        @Override
        public String toString() {
            return this.method.getName();
        }
    }

    private static final class NumberUnitOfMeasureProperty {
        private final Number value;
        private final UnitOfMeasure um;

        private NumberUnitOfMeasureProperty(final Number value, final UnitOfMeasure um) {
            this.value = value;
            this.um = um;
        }

        private Number getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.format("%.2f %s", this.value.doubleValue(), this.um.getSymbol());
        }
    }

    /**
     * Creates itself and inject the controller and the view manager.
     *
     * @param controller  injected controller.
     * @param viewManager injected view manager.
     */
    public NewBatchModalController(final Controller controller, final ViewManager viewManager) {
        super(controller, viewManager);
    }

    @Override
    public void initData(final NewBatchViewModel data) {
        this.batchMethodsComboBox.setItems(
            FXCollections.observableList(data.getBatchMethods()
                                             .stream()
                                             .map(BatchMethodProperty::new)
                                             .collect(Collectors.toList())));

        this.initialSizeUnitOfMeasureLabel.setText(UnitOfMeasure.LITER.getSymbol());

        this.ingredientUnitOfMeasureLabel.setMinWidth(Region.USE_PREF_SIZE);
        this.addIngredientButton.setMinWidth(Region.USE_PREF_SIZE);

        final TableColumn<Pair<NumberUnitOfMeasureProperty, WaterMeasurement.Element>, Button> removeElementColumn = new TableColumn<>();
        removeElementColumn.setStyle("-fx-alignment: CENTER");
        removeElementColumn.setCellValueFactory(obj -> {
            final Button btn = new Button("Remove");
            btn.setOnAction(event -> this.elementsTableView.getItems().removeIf(p ->
                p.getRight().equals(obj.getValue().getRight())));
            return new SimpleObjectProperty<>(btn);
        });
        this.elementsTableView.getColumns().add(removeElementColumn);

        final TableColumn<Pair<NumberUnitOfMeasureProperty, IngredientArticleViewModel>, Button> removeIngredientColumn = new TableColumn<>();
        removeIngredientColumn.setStyle("-fx-alignment: CENTER");
        removeIngredientColumn.setCellValueFactory(obj -> {
            final Button btn = new Button("Remove");
            btn.setOnAction(event -> this.ingredientsTableView.getItems().removeIf(p ->
                p.getRight().getId() == obj.getValue().getRight().getId()));
            return new SimpleObjectProperty<>(btn);
        });
        this.ingredientsTableView.getColumns().add(removeIngredientColumn);

        this.elementsComboBox.setItems(FXCollections.observableList(
            new ArrayList<>(data.getWaterMeasurementElements())
        ));

        this.ingredientsComboBox.getSelectionModel().selectedItemProperty().addListener((opt, oldV, newV) -> {
            if (newV != null) {
                this.ingredientUnitOfMeasureLabel.setText(newV.getUnitOfMeasure().getSymbol());
            }
        });

        this.ingredientsComboBox.setItems(FXCollections.observableList(
            new ArrayList<>(data.getIngredients()))
        );

        this.elementUnitOfMeasureSymbolLabel.setText(ParameterTypeEnum.WATER_MEASUREMENT.getUnitOfMeasure().getSymbol());
    }

    /**
     * Adds a new water measurement registration element to the water measurement table view.
     * @param event the occurred event.
     */
    public void addElementRegistrationClick(final ActionEvent event) {
        final WaterMeasurement.Element selectedElement = this.elementsComboBox.getSelectionModel().getSelectedItem();
        if (selectedElement == null) {
            this.showAlertAndWait(MUST_SELECT_ELEMENT_MESSAGE);
            return;
        }

        Number registrationValue;
        try {
            registrationValue = Double.parseDouble(this.registrationValueTextField.getText().trim());
        } catch (NumberFormatException ex) {
            this.showAlertAndWait(REGISTRATION_VALUE_MUST_BE_NUMBER_MESSAGE);
            return;
        }

        this.elementsTableView.getItems().removeIf(p -> p.getRight().equals(selectedElement));
        this.elementsTableView.getItems().add(Pair.of(new NumberUnitOfMeasureProperty(
            registrationValue, ParameterTypeEnum.WATER_MEASUREMENT.getUnitOfMeasure()
        ), selectedElement));
    }

    /**
     * Adds an ingredient to the ingredient table view.
     * @param event the occurred event.
     */
    public void addIngredientClick(final ActionEvent event) {
        final IngredientArticleViewModel selectedElement = this.ingredientsComboBox.getSelectionModel().getSelectedItem();
        if (selectedElement == null) {
            this.showAlertAndWait(MUST_SELECT_INGREDIENT_MESSAGE);
            return;
        }

        Number quantity;
        try {
            quantity = Double.parseDouble(this.quantityIngredientTextField.getText().trim());
        } catch (NumberFormatException ex) {
            this.showAlertAndWait(INGREDIENT_QUANTITY_MUST_BE_NUMBER_MESSAGE);
            return;
        }

        this.ingredientsTableView.getItems().removeIf(p -> p.getRight().getId() == selectedElement.getId());
        this.ingredientsTableView.getItems().add(Pair.of(new NumberUnitOfMeasureProperty(
            quantity, selectedElement.getUnitOfMeasure()
        ), selectedElement));
    }

    /**
     * Creates the actual batch.
     * @param event the occurred event.
     */
    public void createBatchClick(final ActionEvent event) {
        //all possible checks before calling controller
        if (this.beerNameTextField.getText().trim().isEmpty()) {
            this.showAlertAndWait(THERE_MUST_BE_A_BEER_NAME_MESSAGE);
            return;
        }

        if (this.beerStyleTextField.getText().trim().isEmpty()) {
            this.showAlertAndWait(THERE_MUST_BE_A_BEER_STYLE_MESSAGE);
            return;
        }

        if (this.batchMethodsComboBox.getSelectionModel().getSelectedItem() == null) {
            this.showAlertAndWait(MUST_SELECT_BATCH_METHOD_MESSAGE);
            return;
        }

        if (this.initialSizeTextField.getText().trim().isEmpty()) {
            this.showAlertAndWait(THERE_MUST_BE_AN_INITIAL_SIZE_MESSAGE);
            return;
        }

        final double size;
        try {
            size = Double.parseDouble(this.initialSizeTextField.getText().trim());
        } catch (NumberFormatException ex) {
            this.showAlertAndWait(INITIAL_SIZE_MUST_BE_NUMBER_MESSAGE);
            return;
        }

        //refactor maybe with a DTO
        final Result<Quantity> res = Quantity.of(size, UnitOfMeasure.LITER)
                                             .peekError(e -> this.showAlertAndWait(INITIAL_SIZE_ERROR_MESSAGE + e.getMessage()));

        final Quantity initialSize;
        if (res.isPresent()) {
            initialSize = res.getValue();
        } else {
            return;
        }

        this.getController().createNewBatch(new CreateBatchDTO(
            this.beerNameTextField.getText().trim(),
            this.beerStyleTextField.getText().trim(),
            this.beerCategoryTextField.getText().trim().isEmpty() ? null : this.beerCategoryTextField.getText().trim(),
            this.batchMethodsComboBox.getSelectionModel().getSelectedItem().getMethod(),
            initialSize,
            this.ingredientsTableView.getItems()
                                     .stream()
                                     .map(p -> Pair.of(p.getRight().getId(), p.getLeft().getValue().doubleValue()))
                                     .collect(Collectors.toList()),
            this.elementsTableView.getItems()
                                  .stream()
                                  .map(p -> Triple.of(p.getRight(), p.getLeft().getValue().doubleValue(), new Date()))
                                  .collect(Collectors.toList())))
            .peekError(e -> this.showAlertAndWait(e.getMessage()))
            .peek(e -> {
                final Stage stage = ((Stage) this.elementsTableView.getScene().getWindow());
                //just for saying that i added batch to the caller
                stage.setUserData(new Object());
                stage.close();
            });
    }

    private void showAlertAndWait(final String message) {
        this.showErrorAndWait(ERROR_CREATING_BATCH_MESSAGE + "\n" + message,
            this.addIngredientButton.getScene().getWindow()); // You can use any other control
    }
}
