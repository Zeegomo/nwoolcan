package nwoolcan.viewmodel.brewery.production.batch;

import nwoolcan.model.utils.UnitOfMeasure;
import nwoolcan.viewmodel.brewery.warehouse.article.BeerArticleViewModel;

import java.util.Collections;
import java.util.List;

/**
 * View model representing the info needed for the stock batch modal.
 */
public final class StockBatchViewModel {

    private final int batchId;
    private final UnitOfMeasure unitOfMeasure;
    private final List<BeerArticleViewModel> possibleArticles;

    /**
     * Basic constructor.
     * @param batchId the batch id.
     * @param unitOfMeasure the unit of measure of the new possible article created before stocking.
     * @param possibleArticles all possible beer article that can be used to stock the batch.
     */
    public StockBatchViewModel(final int batchId, final UnitOfMeasure unitOfMeasure, final List<BeerArticleViewModel> possibleArticles) {
        this.batchId = batchId;
        this.unitOfMeasure = unitOfMeasure;
        this.possibleArticles = Collections.unmodifiableList(possibleArticles);
    }

    /**
     * Returns the batch id.
     * @return the batch id.
     */
    public int getBatchId() {
        return this.batchId;
    }

    /**
     * Returns the unit of measure of the new possible beer article to create.
     * @return the unit of measure of the new possible beer article to create.
     */
    public UnitOfMeasure getUnitOfMeasure() {
        return this.unitOfMeasure;
    }

    /**
     * Returns the list of all beer article that can be used to stock the batch.
     * @return the list of all beer article that can be used to stock the batch.
     */
    public List<BeerArticleViewModel> getPossibleArticles() {
        return this.possibleArticles;
    }
}
