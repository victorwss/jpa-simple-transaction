package ninja.javahacker.jpasimpletransactions.openjpa;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;
import ninja.javahacker.jpasimpletransactions.properties.ComposingPersistenceProperties;
import ninja.javahacker.jpasimpletransactions.properties.PersistenceProperties;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@Wither
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenJpaPersistenceProperties implements PersistenceProperties {
    @Getter(AccessLevel.NONE)
    @Wither(AccessLevel.PRIVATE)
    @Delegate(types = Hack.class, excludes = ComposingPersistenceProperties.Build.class)
    @NonNull ComposingPersistenceProperties<OpenJpaPersistenceProperties> hack;

    private static final class Hack extends ComposingPersistenceProperties<OpenJpaPersistenceProperties> {}

    public OpenJpaPersistenceProperties() {
        this.hack = new ComposingPersistenceProperties<>(OpenJpaAdapter.CANONICAL, this::withHack);
    }

    @Override
    public Map<String, String> build() {
        return Map.copyOf(hack.build());
    }
}
