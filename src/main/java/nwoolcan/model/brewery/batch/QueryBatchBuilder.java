package nwoolcan.model.brewery.batch;

import nwoolcan.model.utils.Quantity;
import nwoolcan.model.utils.UnitOfMeasure;
import nwoolcan.utils.Result;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * The builder of {@link QueryBatch}.
 */
public class QueryBatchBuilder {

    @Nullable private Integer batchId;
    @Nullable private String beerName;
    @Nullable private String beerStyle;
    @Nullable private BatchMethod batchMethod;
    @Nullable private Quantity minBatchSize;
    @Nullable private Quantity maxBatchSize;
    @Nullable private Date minStartDate;
    @Nullable private Boolean onlyEnded;

    /**
     * Constructor to initialize the queryBuilder.
     */
    public QueryBatchBuilder() { }

    /**
     * Setter of the batch id.
     * @param batchId the batch id to filter.
     * @return this.
     */
    public QueryBatchBuilder setBatchId(final int batchId) {
        this.batchId = batchId;
        return this;
    }
    /**
     * Setter of the beer name.
     * @param beerName the beer name to filter.
     * @return this.
     */
    public QueryBatchBuilder setBeerName(final String beerName) {
        this.beerName = beerName;
        return this;
    }
    /**
     * Setter of the beer style id.
     * @param beerStyle the beer style to filter.
     * @return this.
     */
    public QueryBatchBuilder setBeerStyle(final String beerStyle) {
        this.beerStyle = beerStyle;
        return this;
    }
    /**
     * Setter of the {@link BatchMethod}.
     * @param method the {@link BatchMethod}.
     * @return this.
     */
    public QueryBatchBuilder setBatchMethod(final BatchMethod method) {
        this.batchMethod = method;
        return this;
    }
    /**
     * Setter of the min batch size.
     * @param size a {@link Quantity} denoting the min batch size.
     * @return this.
     */
    public QueryBatchBuilder setMinBatchSize(final Quantity size) {
        this.minBatchSize = size;
        return this;
    }
    /**
     * Setter of the max batch size.
     * @param size a {@link Quantity} denoting the max batch size.
     * @return this.
     */
    public QueryBatchBuilder setMaxBatchSize(final Quantity size) {
        this.maxBatchSize = size;
        return this;
    }
    /**
     * Setter of the lower bound for the batch start date.
     * @param minStartDate the minimum start date of the filtered batches.
     * @return this.
     */
    public QueryBatchBuilder setMinStartDate(final Date minStartDate) {
        this.minStartDate = new Date(minStartDate.getTime());
        return this;
    }
    /**
     * Setter of the boolean filter for choosing only ended batches.
     * @param onlyEnded true to filter only ended batches.
     * @return this.
     */
    public QueryBatchBuilder setOnlyEnded(final boolean onlyEnded) {
        this.onlyEnded = onlyEnded;
        return this;
    }
    /**
     * Builds the QueryBatch.
     * @return the built {@link QueryBatch}.
     */
    public Result<QueryBatch> build() {
        return Result.of(new QueryBatch(
            this.batchId,
            this.beerName,
            this.beerStyle,
            this.batchMethod,
            this.minBatchSize,
            this.maxBatchSize,
            this.minStartDate,
            this.onlyEnded))
                     .require(this::checkBatchSize);
    }
    /**
     * Check the {@link UnitOfMeasure} of the batch size.
     * @return a boolean denoting the consistency of the uom used.
     */
    private boolean checkBatchSize() {
        if (this.minBatchSize != null
         && this.minBatchSize.getUnitOfMeasure() != UnitOfMeasure.LITER) {
            return false;
        }
        if (this.maxBatchSize != null
         && this.maxBatchSize.getUnitOfMeasure() != UnitOfMeasure.LITER) {
            return false;
        }
        return true;
    }
}
