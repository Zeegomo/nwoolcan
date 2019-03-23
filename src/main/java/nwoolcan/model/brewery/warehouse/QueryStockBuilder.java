package nwoolcan.model.brewery.warehouse;

import nwoolcan.model.brewery.warehouse.article.Article;
import nwoolcan.model.utils.Quantity;
import nwoolcan.model.utils.UnitOfMeasure;
import nwoolcan.utils.Result;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Builder for {@link QueryStock}.
 */
public final class QueryStockBuilder {

    private static final String UOM_ERROR = "Quantity unit of measure mismatching.\nBe sure to"
                                          + "select an article when selecting a quantity filter"
                                          + " and to select the same unit of measure.";
    @Nullable
    private Article article = null;
    @Nullable
    private Date expiresBefore = null;
    @Nullable
    private Date expiresAfter = null;
    @Nullable
    private Quantity minRemainingQuantity = null;
    @Nullable
    private Quantity maxRemainingQuantity = null;
    @Nullable
    private Quantity minUsedQuantity = null;
    @Nullable
    private Quantity maxUsedQuantity = null;
    @Nullable
    private StockState stockStateIncluded = null;
    @Nullable
    private StockState stockStateExcluded = null;
    private QueryStock.SortParameter sortParameter = QueryStock.SortParameter.NONE;
    private boolean sortDescending = false;

    /**
     * Creates an empty builder.
     */
    public QueryStockBuilder() { }
    /**
     * Sets the article required by the query.
     * @param article to filter the query.
     * @return this.
     */
    public QueryStockBuilder setArticle(final Article article) {
        this.article = article;
        return this;
    }

    /**
     * Sets the last expiration date to be returned by query.
     * @param date the last expiration {@link Date} to be returned by the query.
     * @return this.
     */
    public QueryStockBuilder setExpireBefore(final Date date) {
        this.expiresBefore = date;
        return this;
    }
    /**
     * Sets the first expiration date to be returned by query.
     * @param date the first expiration {@link Date} to be returned by the query.
     * @return this.
     */
    public QueryStockBuilder setExpireAfter(final Date date) {
        this.expiresAfter = date;
        return this;
    }
    /**
     * Sets the minimum remaining quantity to be returned by the query.
     * @param quantity the minimum remaining {@link Quantity} to be returned by the query.
     * @return this.
     */
    public QueryStockBuilder setMinRemainingQuantity(final Quantity quantity) {
        this.minRemainingQuantity = quantity;
        return this;
    }
    /**
     * Sets the maximum remaining quantity to be returned by the query.
     * @param quantity the maximum remaining {@link Quantity} to be returned by the query.
     * @return this.
     */
    public QueryStockBuilder setMaxRemainingQuantity(final Quantity quantity) {
        this.maxRemainingQuantity = quantity;
        return this;
    }
    /**
     * Sets the minimum used quantity to be returned by the query.
     * @param quantity the minimum used {@link Quantity} to be returned by the query.
     * @return this.
     */
    public QueryStockBuilder setMinUsedQuantity(final Quantity quantity) {
        this.minUsedQuantity = quantity;
        return this;
    }
    /**
     * Sets the maximum used quantity to be returned by the query.
     * @param quantity the maximum used {@link Quantity} to be returned by the query.
     * @return this.
     */
    public QueryStockBuilder setMaxUsedQuantity(final Quantity quantity) {
        this.maxUsedQuantity = quantity;
        return this;
    }
    /**
     * Sets the only stock state to be included in the query.
     * @param stockState the only {@link StockState} which will result from the query.
     * @return this.
     */
    public QueryStockBuilder setIncludeOnlyStockState(final StockState stockState) {
        this.stockStateIncluded = stockState;
        return this;
    }
    /**
     * Sets the only stock state not to be included in the query.
     * @param stockState the only {@link StockState} which won't result from the query.
     * @return this.
     */
    public QueryStockBuilder setExcludedOnlyStockState(final StockState stockState) {
        this.stockStateExcluded = stockState;
        return this;
    }

    /**
     * Sets the parameter which will be used to sort the stocks in the query.
     * @param sortParameter the {@link QueryStock.SortParameter} which will be used to sort the
     *                      stocks in the query.
     * @return this.
     */
    public QueryStockBuilder setSortParameter(final QueryStock.SortParameter sortParameter) {
        this.sortParameter = sortParameter;
        return this;
    }

    /**
     * Sets the order of the sorting.
     * @param condition a boolean which is true if the stocks have to be sort in a descending way.
     * @return this.
     */
    public QueryStockBuilder sortDescending(final boolean condition) {
        this.sortDescending = condition;
        return this;
    }

    /**
     * Builds the query, after getting all the parameters. It also checks whether the elements of
     * the query use the same {@link UnitOfMeasure}.
     * @return a {@link Result} of {@link QueryStock} if there are not inconsistencies in the query.
     */
    public Result<QueryStock> build() {
        return Result.of(new QueryStock(article,
            expiresBefore,
            expiresAfter,
            minRemainingQuantity,
            maxRemainingQuantity,
            minUsedQuantity,
            maxUsedQuantity,
            stockStateIncluded,
            stockStateExcluded,
            sortParameter,
            sortDescending))
                     .require(this::checkUOMs, new IllegalStateException(UOM_ERROR));
    }

    private boolean checkUOMs() {
        // In case article is not specified, it is not possible to filter quantities.
        if (article == null) {
            return !(minRemainingQuantity != null
                  || maxRemainingQuantity != null
                  || minUsedQuantity != null
                  || maxUsedQuantity != null);
        }
        final UnitOfMeasure uom = article.getUnitOfMeasure();
        // If article is specified, the same uom has to be used on quantity filters.
        return !((minRemainingQuantity != null && minRemainingQuantity.getUnitOfMeasure() != uom)
              || (maxRemainingQuantity != null && maxRemainingQuantity.getUnitOfMeasure() != uom)
              || (minUsedQuantity != null && minUsedQuantity.getUnitOfMeasure() != uom)
              || (maxUsedQuantity != null && maxUsedQuantity.getUnitOfMeasure() != uom));
    }

}
