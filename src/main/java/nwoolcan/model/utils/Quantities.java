package nwoolcan.model.utils;

import nwoolcan.utils.Result;

/**
 * Utils class for operations with quantity objects.
 */
public final class Quantities {

    private static final String NOT_SAME_UM_MESSAGE = "Cannot perform operation beacause quantities have different unit of measures";
    private static final String NEGATIVE_VALUE_MESSAGE = "Quantity value cannot be under zero";

    private Quantities() { }

    private static boolean checkSameUM(final Quantity q1, final Quantity q2) {
        return q1.getUnitOfMeasure().equals(q2.getUnitOfMeasure());
    }

    /**
     * Returns a new {@link Result<Quantity>} that is the adding quantity added to the base quantity.
     * If the quantities' unit of measures do not match, the Result will contain a {@link ArithmeticException}.
     * This method does not modify the quantities passed by parameters, it creates a new one.
     * @param base base quantity.
     * @param adding adding quantity.
     * @return a new {@link Result<Quantity>} that is the adding quantity added to the base quantity.
     */
    public static Result<Quantity> add(final Quantity base, final Quantity adding) {
        return checkSameUM(base, adding)
            ? Result.of(Quantity.of(Numbers.add(base.getValue(), adding.getValue()), base.getUnitOfMeasure()))
            : Result.error(new ArithmeticException(NOT_SAME_UM_MESSAGE));
    }

    /**
     * Returns a new {@link Result<Quantity>} that is the removing quantity removed to the base quantity.
     * If the quantities' unit of measures do not match, the Result will contain a {@link ArithmeticException}.
     * If the remaining quantity value will drop below zero after the operation, the Result will contain a {@link IllegalStateException}.
     * This method does not modify the quantities passed by parameters, it creates a new one.
     * @param base base quantity.
     * @param removing removing quantity.
     * @return a new {@link Result<Quantity>} that is the removing quantity removed to the base quantity.
     */
    public static Result<Quantity> remove(final Quantity base, final Quantity removing) {
        return checkSameUM(base, removing)
            ? Numbers.subtract(base.getValue(), removing.getValue()).doubleValue() >= 0
                ? Result.of(Quantity.of(Numbers.subtract(base.getValue(), removing.getValue()), base.getUnitOfMeasure()))
                : Result.error(new IllegalStateException(NEGATIVE_VALUE_MESSAGE))
            : Result.error(new ArithmeticException(NOT_SAME_UM_MESSAGE));
    }
}
