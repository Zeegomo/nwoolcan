package nwoolcan.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Result.
 * @param <T> Result type.
 */
public final class Result<T> {

    private final Optional<T> elem;
    private final Optional<Exception> exception;

    private Result(final Optional<T> elem, final Optional<Exception> e) {
        this.elem = elem;
        this.exception = e;
    }
    /**
     * @param <T> the type of elem
     * @param elem the result to be encapsulated.
     * @return a new Result<T> holding elem
     */
    public static <T> Result<T> of(final T elem) {
        return new Result<>(Optional.of(elem), Optional.empty());
    }
    /**
     * @param <T> the type of elem
     * @param elem the result to be encapsulated.
     * @return a new Result<T> holding elem
     */
    public static <T> Result<T> ofNullable(final T elem) {
        return new Result<>(Optional.ofNullable(elem), Optional.empty());
    }
    /**
     * @param <T> the type of the value to be returned, if any
     * @param e the exception to be encapsulated.
     * @return a new {@link Result} holding e
     */
    public static <T> Result<T> error(final Exception e) {
        return new Result<>(Optional.empty(), Optional.of(e));
    }
    /**
     *
     * @return a new {@link Result} holding an {@link Empty} value
     */
    public static Result<Empty> ofEmpty() {
        return new Result<>(Optional.of(new Empty() { }), Optional.empty());
    }
    /**
     * Return true if the {@link Result} holds a value.
     * @return wheter the {@link Result} holds a value
     */
    public boolean isPresent() {
        return this.elem.isPresent();
    }
    /**
     * Return true if the {@link Result} holds an exception.
     * @return wheter the {@link Result} holds an exception
     */
    public boolean isError() {
        return this.exception.isPresent();
    }
    /**
     * Return the value held by the {@link Result} if any, otherwise throws a {@link Exception}.
     * @return the value held by the {@link Result}
     */
    public T getValue() {
        return this.elem.get();
    }
    /**
     * Return the exception held by the {@link Result} if any, otherwise throws a {@link Exception}.
     * @return the exception held by the {@link Result}
     */
    public Exception getError() {
        return this.exception.get();
    }
    /**
     * If a value is present, apply the provided function to it returning a {@link Result} describing it. Otherwise return a {@link Result} holding the original exception.
     * @param mapper a mapping function to apply to the value, if present
     * @param <U> the type of the result of the mapping function
     * @return a {@link Result} describing the result of applying a mapping function to the value of this {@link Result}, if a value is present, otherwise a {@link Result} holding the original exception.
     */
    @SuppressWarnings("unchecked")
    public <U> Result<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return this.isPresent() ? Result.ofNullable(mapper.apply(this.elem.get())) : (Result<U>) this;
    }
    /**
     * If a value is present, apply the provided {@link Result}-bearing function to it returning that {@link Result}. Otherwise return a {@link Result} holding the original exception.
     * @param mapper a mapping function to apply to the value, if present. The method is similar to {link} map(Function) but the provided mapper is one whose result is already a {@link Result}, and if invoked, flatMap does not wrap it with an additional {@link Result}.
     * @param <U> the type of the result of the mapping function
     * @return a {link} Result describing the result of applying a mapping function to the value of this {@link Result}, if a value is present, otherwise a {@link Result} holding the original exception.
     */
    @SuppressWarnings("unchecked")
    public <U> Result<U> flatMap(final Function<? super T, Result<U>> mapper) {
        Objects.requireNonNull(mapper);
        return this.isPresent() ? mapper.apply(this.elem.get()) : (Result<U>) this;
    }
    /**
     * Return the value if present, otherwise return other.
     * @param other the value to be returned if there is no value present
     * @return the value, if present, otherwise other
     */
    public T orElse(final T other) {
        return this.isPresent() ? this.elem.get() : other;
    }
    /**
     * Return the value if present, otherwise invoke other and return the result of that invocation.
     * @param other a {@link Supplier} whose result is returned if no value is present
     * @return the value if present otherwise the result of other.get()
     */
    public T orElse(final Supplier<? extends T> other) {
        return this.isPresent() ? this.elem.get() : other.get();
    }
    /*
    /**
     * If a value is present, and the value matches the given predicate, return a {@link Result} describing the value, otherwise return a {@link Result} holding the original exception.
     * @param predicate a predicate to apply to the value, if present
     * @return a {@link Result} describing the value of this Optional if a value is present and the value matches the given predicate
     *//*
    Result<T> filter(Predicate<? super T> predicate);*/
    /**
     * Indicates wheter some other object is "equal to" this {@link Result}. The other object is considered equal if:
     *  - it is also a {@link Result} and:
     *  - the present values are "equal to" each other via equals().
     * @param obj an object to be tested for equality
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Result)) {
            return false;
        }
        Result<?> other = (Result<?>) obj;
        if (this.isPresent()) {
            return this.elem.equals(other.elem);
        } else {
            return false;
        }
    }
    /**
     * Returns a non-empty string representation of this {@link Result} suitable for debugging. The exact presentation format is unspecified and may vary between implementations and versions.
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return this.isPresent() ? this.elem.get().toString() : this.exception.get().toString();
    }
    /**
     * Returns the hash code value of the present value, if any, or 0 if no value is present.
     * @return hash code value of the present value or 0
     */
    @Override
    public int hashCode() {
        return this.isPresent() ? this.elem.get().hashCode() : 0;
    }
    /**
     * Return a {@link Optional}  desribing the value if present. Otherwise returns an empty {@link Optional}
     * @return a {@link Optional} describing the value if present, or an empty {@link Optional} if this {@link Result} hold an exception
     */
    public Optional<T> toOptional() {
        return this.elem;
    }
}
