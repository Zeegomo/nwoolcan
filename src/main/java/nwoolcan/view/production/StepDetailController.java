package nwoolcan.view.production;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import nwoolcan.controller.Controller;
import nwoolcan.model.brewery.batch.step.parameter.ParameterType;
import nwoolcan.view.InitializableController;
import nwoolcan.view.utils.ViewManager;
import nwoolcan.view.subview.SubView;
import nwoolcan.view.subview.SubViewController;
import nwoolcan.view.utils.ViewModelCallback;
import nwoolcan.viewmodel.brewery.production.step.DetailStepViewModel;
import nwoolcan.viewmodel.brewery.production.step.ParameterViewModel;
import nwoolcan.viewmodel.brewery.production.step.RegisterParameterDTO;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * View controller for step detail view.
 */
@SuppressWarnings("NullAway")
public final class StepDetailController
    extends SubViewController
    implements InitializableController<ViewModelCallback<DetailStepViewModel>> {

    private static final String PARAMETER_VALUE_MUST_BE_NUMBER_MESSAGE = "Parameter value must be a number!";
    private static final String ERROR_REGISTERING_PARAMETER_MESSAGE = "An error occurred while registering the parameter.";
    private static final String DAYS = " Days ";
    private static final String HOURS = " Hours ";
    private static final String MINUTES = " Minutes ";
    private static final String SECONDS = " Seconds";

    private Runnable updateFather = () -> { };

    @FXML
    private VBox notesVBox;
    @FXML
    private TextArea notesTextArea;
    @FXML
    private Label unitOfMeasureSymbolLabel;
    @FXML
    private ComboBox<ParameterType> parameterTypesComboBox;
    @FXML
    private ComboBox<ParameterType> graphicsComboBox;
    @FXML
    private TextField newParameterValueTextField;
    @FXML
    private VBox parametersGraphicsVBox;
    @FXML
    private Label durationLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private Label initialDateLabel;
    @FXML
    private Label finalizedLabel;
    @FXML
    private Label stepTypeNameLabel;
    @FXML
    private Button registerParameterButton;
    @FXML
    private SubView stepDetailSubView;

    private DetailStepViewModel data;
    private Map<ParameterType, BorderPane> graphics = new HashMap<>();
    /**
     * Creates itself and gets injected.
     *
     * @param controller  injected controller.
     * @param viewManager injected view manager.
     */
    public StepDetailController(final Controller controller, final ViewManager viewManager) {
        super(controller, viewManager);
    }

    private void setData(final DetailStepViewModel data) {
        this.registerParameterButton.setDisable(data.isFinalized() || data.getPossibleParametersToRegister().size() == 0);

        this.stepTypeNameLabel.setText(data.getTypeName());
        this.initialDateLabel.setText(data.getStartDate().toString());
        this.endDateLabel.setText(data.getEndDate() == null ? "" : data.getEndDate().toString());

        final long durationMillis = data.getEndDate() == null ? 0 : data.getEndDate().getTime() - data.getStartDate().getTime();
        this.durationLabel.setText(durationMillis == 0 ? "" : this.getDurationBreakdown(durationMillis));
        this.finalizedLabel.setText(data.isFinalized() ? "Yes" : "No");

        this.notesTextArea.setText(data.getNotes());
        this.notesTextArea.maxWidthProperty().bind(
            this.notesVBox.widthProperty().divide(3)
        );

        ParameterType prevParameter = this.parameterTypesComboBox.getValue();
        this.parameterTypesComboBox.setItems(FXCollections.observableList(data.getPossibleParametersToRegister()));
        this.parameterTypesComboBox.setConverter(new StringConverter<ParameterType>() {
            @Override
            public String toString(final ParameterType object) {
                return object.getName();
            }

            @Override
            public ParameterType fromString(final String string) {
                return null;
            }
        });
        if (prevParameter != null) {
            this.parameterTypesComboBox.getSelectionModel().select(prevParameter);
        } else {
            this.parameterTypesComboBox.getSelectionModel().selectFirst();
        }

        ParameterType prev = this.graphicsComboBox.getValue();
        this.graphics.clear();
        this.graphicsComboBox.getItems().clear();
        data.getMapTypeToRegistrations().forEach((paramType, params) -> {
            final BorderPane pane = new BorderPane();
            //this.parametersGraphicsVBox.getChildren().add(pane);
            this.graphics.put(paramType, pane);
            this.graphicsComboBox.getItems().add(paramType);

            final Label title = new Label(paramType.getName());
            BorderPane.setAlignment(title, Pos.CENTER);
            pane.setTop(title);

            final VBox generalInfoVBox = new VBox();

            ParameterViewModel initial = params.get(params.size() - 1);
            ParameterViewModel current = params.get(0);

            final VBox initialValueVBox = new VBox();
            initialValueVBox.getChildren().add(new Label("Initial value: "));
            initialValueVBox.getChildren().add(new Label(initial.getValueRepresentation()));

            final VBox currentValueVBox = new VBox();
            currentValueVBox.getChildren().add(new Label("Current value : "));
            currentValueVBox.getChildren().add(new Label(current.getValueRepresentation()));

            double medium = params.stream()
                                  .mapToDouble(p -> p.getValue()
                                                     .doubleValue())
                                  .average().orElse(0);


            final VBox mediumValueVBox = new VBox();
            mediumValueVBox.getChildren().add(new Label("Medium value: "));
            mediumValueVBox.getChildren().add(new Label(String.format("%.2f %s", medium, initial.getUnitOfMeasure().getSymbol())));

            generalInfoVBox.getChildren().add(initialValueVBox);
            generalInfoVBox.getChildren().add(currentValueVBox);
            generalInfoVBox.getChildren().add(mediumValueVBox);

            pane.setLeft(generalInfoVBox);

            XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>(paramType.getName(),
                FXCollections.observableList(params.stream()
                                                   .map(p -> new XYChart.Data<Number, Number>(p.getRegistrationDate().getTime(), p.getValue()))
                                                   .collect(Collectors.toList())
            ));

            NumberAxis dateAxis = new NumberAxis(
                initial.getRegistrationDate().getTime() - (current.getRegistrationDate().getTime() - initial.getRegistrationDate().getTime()) / 16.0,
                current.getRegistrationDate().getTime() + (current.getRegistrationDate().getTime() - initial.getRegistrationDate().getTime()) / 16.0,
                 (current.getRegistrationDate().getTime() - initial.getRegistrationDate().getTime()) / 8.0);
            dateAxis.setTickLabelRotation(90);
            dateAxis.setTickLabelFont(new Font(10));
            dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                @Override
                public String toString(final Number object) {
                    return new Date(object.longValue()).toString();
                }

                @Override
                public Number fromString(final String string) {
                    return null;
                }
            });

            LineChart<Number, Number> chart = new LineChart<Number, Number>(dateAxis, new NumberAxis(),
                FXCollections.observableList(Collections.singletonList(series))
            );
            chart.setAnimated(false);
            pane.setCenter(chart);
        });
        if (prev != null) {
            this.graphicsComboBox.getSelectionModel().select(prev);
            this.graphicsComboBoxClicked(null);
        }
        if (this.graphics.size() > 0) {
            this.graphicsComboBox.setDisable(false);
        } else {
            this.graphicsComboBox.setDisable(true);
        }
    }

    @Override
    public void initData(final ViewModelCallback<DetailStepViewModel> dataCallback) {
        this.data = dataCallback.getViewModel();
        this.updateFather = dataCallback.getCallback();

        this.parameterTypesComboBox.getSelectionModel().selectedItemProperty().addListener((opt, oldV, newV) -> {
                if (newV != null) {
                    this.unitOfMeasureSymbolLabel.setText(newV.getUnitOfMeasure().getSymbol());
                }
            }
        );

        this.setData(this.data);
    }

    @Override
    protected SubView getSubView() {
        return this.stepDetailSubView;
    }

    /**
     * Goes back to batch detail.
     * @param event the occurred event.
     */
    public void goBackButtonClicked(final ActionEvent event) {
        this.updateFather.run();
        this.previousView();
    }

    /**
     * Registers a parameter in this step.
     * @param event the occurred event.
     */
    public void registerParameterButtonClicked(final ActionEvent event) {
        double value;
        try {
            value = Double.parseDouble(this.newParameterValueTextField.getText().trim());
        } catch (NumberFormatException ex) {
            this.showAlertAndWait(PARAMETER_VALUE_MUST_BE_NUMBER_MESSAGE);
            return;
        }

        this.getController().getBatchController().getStepController().registerParameter(
            new RegisterParameterDTO(
                data.getBatchId(),
                value,
                this.parameterTypesComboBox.getSelectionModel().getSelectedItem(),
                new Date())
        ).peek(e -> {
            this.getController().getBatchController().getStepController().getDetailStepViewModel(
                data.getBatchId(),
                data.getTypeName()
            ).peek(this::setData);
        }).peekError(e -> {
            this.showAlertAndWait(e.getMessage());
        });

    }
    /**
     * Update graphics based on selected item.
     * @param event the recorded event.
     */
    public void graphicsComboBoxClicked(final ActionEvent event) {
        if (this.graphicsComboBox.getItems().size() > 0) {
            this.parametersGraphicsVBox.getChildren().clear();
            this.parametersGraphicsVBox.getChildren()
                                       .add(this.graphics.get(this.graphicsComboBox.getSelectionModel().getSelectedItem()));
        }
    }
    /* Utils function from stack overflow.
       https://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java
     */
    private String getDurationBreakdown(final long millis) {
        long cMillis = millis;
        long days = TimeUnit.MILLISECONDS.toDays(cMillis);
        cMillis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(cMillis);
        cMillis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(cMillis);
        cMillis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(cMillis);

        final StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(DAYS);
        sb.append(hours);
        sb.append(HOURS);
        sb.append(minutes);
        sb.append(MINUTES);
        sb.append(seconds);
        sb.append(SECONDS);

        return sb.toString();
    }

    private void showAlertAndWait(final String message) {
        this.showErrorAndWait(ERROR_REGISTERING_PARAMETER_MESSAGE + "\n" + message,
            this.endDateLabel.getScene().getWindow());
    }
}
