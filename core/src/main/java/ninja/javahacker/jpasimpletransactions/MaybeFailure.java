package ninja.javahacker.jpasimpletransactions;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.PackagePrivate;

/**
 * Implementation of {@link Maybe} in case of failure.
 * @param <T> The type of the object representing a success. Irrelevant in case of a failure.
 * @author Victor Williams Stafusa da Silva
 */
@PackagePrivate
@AllArgsConstructor
final class MaybeFailure<T> implements Maybe<T> {

    @NonNull
    private final Throwable oops;

    @Override
    public Optional<Throwable> failure() {
        return Optional.of(oops);
    }
}