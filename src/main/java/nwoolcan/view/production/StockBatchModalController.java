package nwoolcan.view.production;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nwoolcan.controller.Controller;
import nwoolcan.utils.Empty;
import nwoolcan.utils.Result;
import nwoolcan.view.AbstractViewController;
import nwoolcan.view.InitializableController;
import nwoolcan.view.ViewManager;
import nwoolcan.viewmodel.brewery.production.batch.DetailBatchViewModel;
import nwoolcan.viewmodel.brewery.production.batch.StockBatchViewModel;
import nwoolcan.viewmodel.brewery.warehouse.article.BeerArticleViewModel;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * View controller of stock new batch modal view.
 */
@SuppressWarnings("NullAway")
public final class StockBatchModalController
    extends AbstractViewController
    implements InitializableController<StockBatchViewModel> {

    @FXML
    private TitledPane newArticleTitledPane;
    @FXML
    private VBox useExistentArticleVBox;
    @FXML
    private VBox beerArticlesVBox;
    @FXML
    private Button stockBatchButton;
    @FXML
    private DatePicker expirationDatePicker;
    @FXML
    private TextField newBeerArticleNameTextField;
    @FXML
    private CheckBox createNewBeerArticleCheckBox;
    @FXML
    private ComboBox<BeerArticleViewModel> possibileBeerArticlesComboBox;

    private StockBatchViewModel data;

    /**
     * Creates itself and inject the controller and the view manager.
     *
     * @param controller  injected controller.
     * @param viewManager injected view manager.
     */
    public StockBatchModalController(final Controller controller, final ViewManager viewManager) {
        super(controller, viewManager);
    }

    @Override
    public void initData(final StockBatchViewModel data) {
        this.data = data;
        final List<BeerArticleViewModel> articles = data.getPossibleArticles();

        if (articles.size() == 0) {
            this.useExistentArticleVBox.getChildren().clear();
            this.useExistentArticleVBox.getChildren().add(new Label("No matching beer articles for this batch"));
        } else {
            this.possibileBeerArticlesComboBox.setItems(FXCollections.observableList(articles));
        }

        this.newArticleTitledPane.visibleProperty().bind(
            this.createNewBeerArticleCheckBox.selectedProperty()
        );

        this.possibileBeerArticlesComboBox.disableProperty().bind(
            this.createNewBeerArticleCheckBox.selectedProperty()
        );
    }

    /**
     * Stock the batch with beer article and possible expiration date.
     * @param event the occurred event.
     */
    public void stockBatchButtonClicked(final ActionEvent event) {
        int articleId;

        if (this.createNewBeerArticleCheckBox.isSelected()) {
            if (this.newBeerArticleNameTextField.getText().isEmpty()) {
                this.showAlertAndWait("New beer article name cannot be empty!");
                return;
            }

            Result<DetailBatchViewModel> res = this.getController().getBatchController().getDetailBatchViewModelById(data.getBatchId());

            if (res.isError()) {
                this.showAlertAndWait(res.getError().getMessage());
                return;
            }

            articleId = this.getController().getWarehouseController().createBeerArticle(this.newBeerArticleNameTextField.getText(),
                data.getUnitOfMeasure()).getId();
        } else {
            if (this.possibileBeerArticlesComboBox.getSelectionModel().getSelectedItem() == null) {
                this.showAlertAndWait("Must select a beer article!");
                return;
            }

            articleId = this.possibileBeerArticlesComboBox.getSelectionModel().getSelectedItem().getId();
        }

        Result<Empty> res;

        if (this.expirationDatePicker.getValue() == null) {
            res = this.getController().stockBatch(data.getBatchId(), articleId);
        } else {
            //convert from LocalDate to Date
            final Date expDate = Date.from(this.expirationDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            res = this.getController().stockBatch(data.getBatchId(), articleId, expDate);
        }

        res.peekError(e -> this.showAlertAndWait(e.getMessage()))
           .peek(e -> {
               final Stage stage = ((Stage) this.newArticleTitledPane.getScene().getWindow());
               //just for saying that i stocked the batch
               stage.setUserData(new Object());
               stage.close();
           });
    }

    private void showAlertAndWait(final String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, "An error occurred while stocking the batch.\n" + message, ButtonType.CLOSE);
        a.showAndWait();
    }
}
