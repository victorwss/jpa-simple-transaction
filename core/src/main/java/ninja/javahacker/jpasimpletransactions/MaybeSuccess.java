package ninja.javahacker.jpasimpletransactions;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.PackagePrivate;

/**
 * Implementation of {@link Maybe} in case of success.
 * @param <T> The type of the object representing a success.
 * @author Victor Williams Stafusa da Silva
 */
@PackagePrivate
@AllArgsConstructor
final class MaybeSuccess<T> implements Maybe<T> {

    @NonNull
    private final T entry;

    @Override
    public Optional<T> success() {
        return Optional.of(entry);
    }
}
