package nwoolcan.model.brewery.warehouse.stock;

import nwoolcan.model.utils.Quantity;
import nwoolcan.utils.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Class which keeps track of a {@link Quantity} and a {@link Date}.
 */
public final class Record {
    /**
     * Defines if the record is positive or negative.
     */
    public enum Action {
        /**
         * If the record Action is REMOVING the quantity will be subtracted,
         * while it will be added if the Action is ADDING.
         */
        REMOVING, ADDING;

        @Override
        public String toString() {
            return StringUtils.underscoreSeparatedToHuman(this.name());
        }
    }

    private final Quantity quantity;
    private final Date date;
    private final Action action;

    /**
     * Constructor with all parameters.
     * @param quantity to be recorded.
     * @param date in which the recorded happened.
     * @param action which states if quantity is added or subtracted.
     */
    public Record(final Quantity quantity, final Date date, final Action action) {
        this.quantity = quantity;
        this.date = DateUtils.truncate(date, Calendar.SECOND);
        this.action = action;
    }
    /**
     * Constructor which auto instantiate the current {@link Date}.
     * @param quantity to be recorded.
     * @param action which states if quantity is added or subtracted.
     */
    public Record(final Quantity quantity, final Action action) {
        this(quantity, new Date(), action);
    }
    /**
     * Getter for the date.
     * @return the date of the record.
     */
    public Date getDate() {
        return (Date) this.date.clone();
    }
    /**
     * Getter for the quantity. It is not protected since it does not have any setter.
     * @return the quantity of the record.
     */
    public Quantity getQuantity() {
        return this.quantity;
    }
    /**
     * Getter for the quantity.
     * @return the action of the current record.
     */
    public Action getAction() {
        return this.action;
    }

}
