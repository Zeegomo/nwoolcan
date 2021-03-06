package nwoolcan.view.warehouse;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import nwoolcan.controller.Controller;
import nwoolcan.model.brewery.warehouse.article.ArticleType;
import nwoolcan.model.brewery.warehouse.stock.QueryStock;
import nwoolcan.model.brewery.warehouse.stock.QueryStockBuilder;
import nwoolcan.model.brewery.warehouse.stock.StockState;
import nwoolcan.model.utils.Quantity;
import nwoolcan.utils.Result;
import nwoolcan.view.filters.DateFilter;
import nwoolcan.view.filters.SelectFilter;
import nwoolcan.view.filters.TextFilter;
import nwoolcan.view.utils.ViewManager;
import nwoolcan.view.ViewType;
import nwoolcan.view.mastertable.ColumnDescriptor;
import nwoolcan.view.mastertable.MasterTableViewModel;
import nwoolcan.view.subview.SubView;
import nwoolcan.view.subview.SubViewContainer;
import nwoolcan.view.subview.SubViewController;
import nwoolcan.view.utils.ViewModelCallback;
import nwoolcan.viewmodel.brewery.warehouse.WarehouseViewModel;
import nwoolcan.viewmodel.brewery.warehouse.article.AbstractArticleViewModel;
import nwoolcan.viewmodel.brewery.warehouse.stock.MasterStockViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the Warehouse view.
 */
@SuppressWarnings("NullAway")
public final class WarehouseViewController extends SubViewController {

    private static final String USED = "Used";
    private static final String AVAILABLE = "Available";
    private static final String EXPIRED = "Expired";
    private static final String SUFFIX_QUANTITY_NOT_DOUBLE = " quantity must be a double number.";
    private static final String MIN_REM_QUANTITY_NOT_DOUBLE = "The minimum remaining" + SUFFIX_QUANTITY_NOT_DOUBLE;
    private static final String MAX_REM_QUANTITY_NOT_DOUBLE = "The maximum remaining" + SUFFIX_QUANTITY_NOT_DOUBLE;
    private static final String MIN_USED_QUANTITY_NOT_DOUBLE = "The minimum used" + SUFFIX_QUANTITY_NOT_DOUBLE;
    private static final String MAX_USED_QUANTITY_NOT_DOUBLE = "The maximum used" + SUFFIX_QUANTITY_NOT_DOUBLE;
    private static final String INTERNAL_ERROR = "Internal error: ";
    private static final String NO_ARTICLES_ALERT_MESSAGE = "There are no articles or only beer articles. Create a misc"
                                                          + "or ingredient article first";
    private static final String ID_COLUMN_NAME = "ID";
    private static final String ARTICLE_COLUMN_NAME = "Article";
    private static final String REM_QUANTITY_COLUMN_NAME = "Remaining Quantity";
    private static final String USED_QUANTITY_COLUMN_NAME = "Used Quantity";
    private static final String STOCK_STATE_COLUMN_NAME = "Stock State";
    private static final String EXPIRATION_DATE_COLUMN_NAME = "Expiration Date";
    private static final String ID_FIELD_NAME = "id";
    private static final String ARTICLE_FIELD_NAME = "article";
    private static final String REM_QUANTITY_FIELD_NAME = "remainingQuantity";
    private static final String USED_QUANTITY_FIELD_NAME = "usedQuantity";
    private static final String STOCK_STATE_FIELD_NAME = "stockState";
    private static final String EXPIRATION_DATE_FIELD_NAME = "expirationDate";

    @FXML
    private SelectFilter<AbstractArticleViewModel> articleFilter;
    @FXML
    private SelectFilter<ArticleType> articleTypeFilter;
    @FXML
    private DateFilter expiresBeforeFilter;
    @FXML
    private DateFilter expiresAfterFilter;
    @FXML
    private TextFilter minRemainingQuantity;
    @FXML
    private TextFilter maxRemainingQuantity;
    @FXML
    private TextFilter minUsedQuantity;
    @FXML
    private TextFilter maxUsedQuantity;
    @FXML
    private SelectFilter<StockState> stockStateInclude;
    @FXML
    private SelectFilter<StockState> stockStateExclude;
    @FXML
    private Label lblNumberBeerAvailable;
    @FXML
    private Label lblNumberBeerExpired;
    @FXML
    private Label lblNumberBeerUsedUp;
    @FXML
    private PieChart pieChartBeerStatus;
    @FXML
    private Label lblNumberIngredientAvailable;
    @FXML
    private Label lblNumberIngredientExpired;
    @FXML
    private Label lblNumberIngredientUsedUp;
    @FXML
    private PieChart pieChartIngredientStatus;
    @FXML
    private Label lblNumberMiscAvailable;
    @FXML
    private Label lblNumberMiscExpired;
    @FXML
    private Label lblNumberMiscUsedUp;
    @FXML
    private PieChart pieChartMiscStatus;
    @FXML
    private SubViewContainer masterTableContainer;
    @FXML
    private SubView warehouseSubView;

    /**
     * Creates itself and gets injected.
     * @param controller injected controller.
     * @param viewManager injected view manager.
     */
    public WarehouseViewController(final Controller controller, final ViewManager viewManager) {
        super(controller, viewManager);
    }

    @FXML
    private void initialize() {
        final WarehouseViewModel data = getController().getWarehouseController().getWarehouseViewModel();
        // Beer data load
        this.lblNumberBeerAvailable.setText(Integer.toString(data.getnBeerAvailable()));
        this.lblNumberBeerExpired.setText(Integer.toString(data.getnBeerExpired()));
        this.lblNumberBeerUsedUp.setText(Integer.toString(data.getnBeerUsed()));
        pieChartBeerStatus.setData(
            FXCollections.observableArrayList(
                new PieChart.Data(USED, data.getnBeerUsed()),
                new PieChart.Data(EXPIRED, data.getnBeerExpired()),
                new PieChart.Data(AVAILABLE, data.getnBeerAvailable())
            )
        );
        // Ingredient data load
        this.lblNumberIngredientAvailable.setText(Integer.toString(data.getnIngredientAvailable()));
        this.lblNumberIngredientExpired.setText(Integer.toString(data.getnIngredientExpired()));
        this.lblNumberIngredientUsedUp.setText(Integer.toString(data.getnIngredientUsed()));
        pieChartIngredientStatus.setData(
            FXCollections.observableArrayList(
                new PieChart.Data(USED, data.getnIngredientUsed()),
                new PieChart.Data(EXPIRED, data.getnIngredientExpired()),
                new PieChart.Data(AVAILABLE, data.getnIngredientAvailable())
            )
        );
        // Misc data load
        this.lblNumberMiscAvailable.setText(Integer.toString(data.getnMiscAvailable()));
        this.lblNumberMiscExpired.setText(Integer.toString(data.getnMiscExpired()));
        this.lblNumberMiscUsedUp.setText(Integer.toString(data.getnMiscUsed()));
        pieChartMiscStatus.setData(
            FXCollections.observableArrayList(
                new PieChart.Data(USED, data.getnMiscUsed()),
                new PieChart.Data(EXPIRED, data.getnMiscExpired()),
                new PieChart.Data(AVAILABLE, data.getnMiscAvailable())
            )
        );
        articleFilter.setItems(FXCollections.observableArrayList(getController().getWarehouseController().getArticlesViewModel().getArticles()));

        changeArticleSelection(new ActionEvent());
        setTable(data.getStocks());
    }



    private void setTable(final List<MasterStockViewModel> stocks) {
        final MasterTableViewModel<MasterStockViewModel, ViewModelCallback<Integer>> masterViewModel =
            new MasterTableViewModel<>(Arrays.asList(
                                           new ColumnDescriptor(ID_COLUMN_NAME, ID_FIELD_NAME),
                                           new ColumnDescriptor(ARTICLE_COLUMN_NAME, ARTICLE_FIELD_NAME),
                                           new ColumnDescriptor(REM_QUANTITY_COLUMN_NAME, REM_QUANTITY_FIELD_NAME),
                                           new ColumnDescriptor(USED_QUANTITY_COLUMN_NAME, USED_QUANTITY_FIELD_NAME),
                                           new ColumnDescriptor(STOCK_STATE_COLUMN_NAME, STOCK_STATE_FIELD_NAME),
                                           new ColumnDescriptor(EXPIRATION_DATE_COLUMN_NAME, EXPIRATION_DATE_FIELD_NAME)
                                       ),
                                       stocks,
                                       ViewType.STOCK_DETAIL,
                                       masterStockViewModel -> new ViewModelCallback<>(masterStockViewModel.getId(), this::initialize)
            );
        this.getViewManager().getView(ViewType.MASTER_TABLE, masterViewModel).peek(masterTableContainer::substitute);
    }

    @Override
    protected SubView getSubView() {
        return this.warehouseSubView;
    }

    @FXML
    private void gotoDashboardClick(final ActionEvent actionEvent) {
        this.substituteView(ViewType.DASHBOARD);
    }

    @FXML
    private void createNewStockButtonClick(final ActionEvent actionEvent) {
        if (getController().getWarehouseController().getArticlesViewModel().getnMiscArticles() <= 0
            && getController().getWarehouseController().getArticlesViewModel().getnIngredientArticles() <= 0) {
            this.showErrorAndWait(NO_ARTICLES_ALERT_MESSAGE);
            return;
        }

        final Stage modal =  new Stage();
        final Window window = this.getSubView().getScene().getWindow();

        modal.initOwner(window);
        modal.initModality(Modality.WINDOW_MODAL);

        final Scene scene = new Scene(this.getViewManager().getView(ViewType.NEW_STOCK_MODAL).orElse(new AnchorPane()));

        modal.setScene(scene);
        modal.setResizable(false);
        modal.showAndWait();

        this.substituteView(ViewType.WAREHOUSE);

    }

    private void updateStocksTable(final QueryStock queryStock) {
        final List<MasterStockViewModel> stocks = this.getController()
                                                      .getWarehouseController()
                                                      .getStocks(queryStock);
        setTable(stocks);
    }

    @FXML
    private void applyFiltersClicked(final ActionEvent event) {
        final QueryStockBuilder builder = new QueryStockBuilder();
        this.articleFilter
            .getValue()
            .ifPresent(article -> builder.setArticle(getController().getWarehouseController()
                                                                    .getViewArticleById(article.getId())
                                                                    .getValue()));
        this.articleTypeFilter.getValue().ifPresent(builder::setArticleType);
        this.expiresBeforeFilter.getValue().ifPresent(builder::setExpireBefore);
        this.expiresAfterFilter.getValue().ifPresent(builder::setExpireAfter);
        this.minRemainingQuantity.getValue().ifPresent(s -> {
            final double minRemValue;
            try {
                minRemValue = Double.parseDouble(s);
            } catch (final NumberFormatException ex) {
                this.showErrorAndWait(MIN_REM_QUANTITY_NOT_DOUBLE);
                return;
            }
            builder.setMinRemainingQuantity(Quantity.of(minRemValue, articleFilter.getValue().get().getUnitOfMeasure()).getValue());
        });
        this.maxRemainingQuantity.getValue().ifPresent(s -> {
            final double maxRemValue;
            try {
                maxRemValue = Double.parseDouble(s);
            } catch (final NumberFormatException ex) {
                this.showErrorAndWait(MAX_REM_QUANTITY_NOT_DOUBLE);
                return;
            }
            builder.setMaxRemainingQuantity(Quantity.of(maxRemValue, articleFilter.getValue().get().getUnitOfMeasure()).getValue());
        });
        this.minUsedQuantity.getValue().ifPresent(s -> {
            final double minUsedValue;
            try {
                minUsedValue = Double.parseDouble(s);
            } catch (final NumberFormatException ex) {
                this.showErrorAndWait(MIN_USED_QUANTITY_NOT_DOUBLE);
                return;
            }
            builder.setMinUsedQuantity(Quantity.of(minUsedValue, articleFilter.getValue().get().getUnitOfMeasure()).getValue());
        });
        this.maxUsedQuantity.getValue().ifPresent(s -> {
            final double maxUsedValue;
            try {
                maxUsedValue = Double.parseDouble(s);
            } catch (final NumberFormatException ex) {
                this.showErrorAndWait(MAX_USED_QUANTITY_NOT_DOUBLE);
                return;
            }
            builder.setMaxUsedQuantity(Quantity.of(maxUsedValue, articleFilter.getValue().get().getUnitOfMeasure()).getValue());
        });
        this.stockStateExclude.getValue().ifPresent(builder::setExcludeOnlyStockState);
        this.stockStateInclude.getValue().ifPresent(builder::setIncludeOnlyStockState);

        final Result<QueryStock> queryStockResult = builder.build();
        if (queryStockResult.isError()) {
            this.showErrorAndWait(INTERNAL_ERROR + queryStockResult.getError().getMessage());
            return;
        }
        this.updateStocksTable(queryStockResult.getValue());
    }

    @FXML
    private void changeArticleSelection(final ActionEvent actionEvent) {
        final String minRemQuantityDefaultLabel = "Min Remaining Q";
        final String maxRemQuantityDefaultLabel = "Max Remaining Q";
        final String minUsedQuantityDefaultLabel = "Min Used Q";
        final String maxUsedQuantityDefaultLabel = "Max Used Q";
        final String uomSymbol;

        if (articleFilter.getValue().isPresent()) {
            uomSymbol = " (" + articleFilter.getValue().get().getUnitOfMeasure().getSymbol() + ")";
        } else {
            uomSymbol = "";
        }

        this.maxUsedQuantity.setDisable(!articleFilter.getValue().isPresent());
        this.minUsedQuantity.setDisable(!articleFilter.getValue().isPresent());
        this.maxRemainingQuantity.setDisable(!articleFilter.getValue().isPresent());
        this.minRemainingQuantity.setDisable(!articleFilter.getValue().isPresent());

        this.maxUsedQuantity.getLabel().setText(maxUsedQuantityDefaultLabel + uomSymbol);
        this.minUsedQuantity.getLabel().setText(minUsedQuantityDefaultLabel + uomSymbol);
        this.maxRemainingQuantity.getLabel().setText(maxRemQuantityDefaultLabel + uomSymbol);
        this.minRemainingQuantity.getLabel().setText(minRemQuantityDefaultLabel + uomSymbol);
    }
}
