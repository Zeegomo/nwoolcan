package nwoolcan.view.review;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import nwoolcan.controller.Controller;
import nwoolcan.model.brewery.batch.review.EvaluationType;
import nwoolcan.utils.Results;
import nwoolcan.view.AbstractViewController;
import nwoolcan.view.InitializableController;
import nwoolcan.view.ViewManager;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;

/**
 * Controller for single evaluation input displayed in {@link NewBatchEvaluationController}.
 */
@SuppressWarnings("NullAway")
public final class EvaluationInputController extends AbstractViewController implements InitializableController<EvaluationType> {
    private static final String DEFAULT_TEXT = "-fx-text-fill: black;";
    private static final String ERROR_TEXT = "-fx-text-fill: red;";
    @FXML
    private TextField score;
    @FXML
    private Label maxScore;
    @FXML
    private TextArea notes;
    @FXML
    private TitledPane title;

    private final BooleanProperty valididtyProperty = new SimpleBooleanProperty(false);
    /**
     * Creates itself and inject the controller and the view manager.
     *
     * @param controller  injected controller.
     * @param viewManager injected view manager.
     */
    public EvaluationInputController(final Controller controller, final ViewManager viewManager) {
        super(controller, viewManager);
    }

    @Override
    public void initData(final EvaluationType data) {
        this.maxScore.setText(String.valueOf(data.getMaxScore()));
        this.score.focusedProperty().addListener((obv, unfocus, focus) -> {
            if (unfocus) {
                Results.ofChecked(() -> Integer.parseInt(this.score.getText()))
                       .flatMap(parsedScore ->
                           this.getController()
                               .getBatchController()
                               .checkEvaluation(Triple.of(data, parsedScore, Optional.of(this.notes.textProperty().get())))
                       )
                       .peek(good -> {
                           this.score.setStyle(DEFAULT_TEXT);
                           this.valididtyProperty.setValue(true);
                       })
                       .peekError(bad -> {
                           this.score.setStyle(ERROR_TEXT);
                           this.valididtyProperty.setValue(false);
                       });
            }
        });
        this.score.alignmentProperty().setValue(Pos.BASELINE_RIGHT);
        this.title.setText(data.getName());
        this.title.setExpanded(false);
    }

    /**
     * Returns properties of this controller.
     * @return properties of this controller.
     */
    public ReadOnlyObjectWrapper<Pair<ReadOnlyStringProperty, ReadOnlyStringProperty>> getInputProperty() {
        return new ReadOnlyObjectWrapper<>(Pair.of(this.score.textProperty(), this.notes.textProperty()));
    }
    public ReadOnlyBooleanProperty getInputValidityProperty() {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(valididtyProperty);
    }

}
