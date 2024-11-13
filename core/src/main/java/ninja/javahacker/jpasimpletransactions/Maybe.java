package ninja.javahacker.jpasimpletransactions;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * Denotes something that may be available or may be failed with some exception.
 * @param <T> The type of the object representing a success.
 * @author Victor Williams Stafusa da Silva
 */
public sealed interface Maybe<T> permits MaybeSuccess, MaybeFailure {

    /**
     * True if this represents a success.
     * @return if this represents a success.
     */
    public default boolean isSuccess() {
        return this instanceof MaybeSuccess;
    }

    /**
     * Returns an {@link Optional} containing the failure or empty if it wasn't a failure.
     * @return An {@link Optional} containing the failure or empty if it wasn't a failure.
     */
    public default Optional<Throwable> failure() {
        return Optional.empty();
    }

    /**
     * Produces a failure instance containing the given error.
     * @param <T> The type of the success instance, even if it wasn't a success afterall, needed to typecheck correctly.
     * @param oops The error producing the failure.
     * @return An instance of the {@code Maybe} representing a failure containing the given entry.
     * @throws IllegalArgumentException If {@code oops} is {@code null}.
     */
    public static <T> Maybe<T> failure(@NonNull Throwable oops) {
        return new MaybeFailure<>(oops);
    }

    /**
     * Returns an {@link Optional} containing the resulting success or empty if it wasn't a success.
     * @return An {@link Optional} containing the resulting success or empty if it wasn't a success.
     */
    public default Optional<T> success() {
        return Optional.empty();
    }

    /**
     * Produces a success instance containing the given entry.
     * @param <T> The type of the success instance.
     * @param entry The success instance.
     * @return An instance of the {@code Maybe} representing a success containing the given entry.
     * @throws IllegalArgumentException If {@code entry} is {@code null}.
     */
    public static <T> Maybe<T> success(@NonNull T entry) {
        return new MaybeSuccess<>(entry);
    }

    /**
     * Returns the content, be it a success instance or an error.
     * @return The content, whatever it is.
     */
    public default Object content() {
        return isSuccess() ? success().get() : failure().get();
    }

    /**
     * Wrap a {@code Supplier<T>} that could throw an exception into one that gives off a {@code Maybe} instance instead.
     * @param <T> The given {@code Supplier}'s type.
     * @param inner The given {@code Supplier}'s to be wrapped.
     * @return The given {@code Supplier} wrapped into a {@code Supplier} that produces a {@code Maybe} instance.
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public static <T> Supplier<Maybe<T>> wrap(@NonNull Supplier<T> inner) {
        return () -> {
            try {
                return success(inner.get());
            } catch (Throwable oops) {
                return failure(oops);
            }
        };
    }

    /**
     * Flatten out a {@code Maybe} of another {@code Maybe}.
     * @param <T> The type of the inner {@code Maybe}.
     * @param tooDeep The two-layered {@code Maybe} to be flatten out.
     * @return A {@code Maybe} instance with only one layer.
     */
    public static <T> Maybe<T> flatten(@NonNull Maybe<Maybe<T>> tooDeep) {
        return !tooDeep.isSuccess() ? failure(tooDeep.failure().get()) : tooDeep.success().get();
    }
}