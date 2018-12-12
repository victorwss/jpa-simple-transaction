package ninja.javahacker.jpasimpletransactions.eclipselink;

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
public class EclipselinkPersistenceProperties implements PersistenceProperties {

    @Getter(AccessLevel.NONE)
    @Wither(AccessLevel.PRIVATE)
    @Delegate(types = Hack.class, excludes = ComposingPersistenceProperties.Build.class)
    @NonNull ComposingPersistenceProperties<EclipselinkPersistenceProperties> hack;

    private static final class Hack extends ComposingPersistenceProperties<EclipselinkPersistenceProperties> {}

    public EclipselinkPersistenceProperties() {
        this.hack = new ComposingPersistenceProperties<>(EclipselinkAdapter.CANONICAL, this::withHack);
    }

    @Override
    public Map<String, String> build() {
        return Map.copyOf(hack.build());
    }
}
