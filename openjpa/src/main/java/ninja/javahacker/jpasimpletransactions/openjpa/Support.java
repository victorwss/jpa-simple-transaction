package ninja.javahacker.jpasimpletransactions.openjpa;

import java.util.Locale;
import java.util.function.BiConsumer;
import lombok.NonNull;

/**
 * Enum for the values of the Open JPA property {@code openjpa.RuntimeUnenhancedClasses}.
 * @see "The property {@code openjpa.RuntimeUnenhancedClasses}."
 * @author Victor Williams Stafusa da Silva
 */
public enum Support {
    UNSPECIFIED, SUPPORTED, UNSUPPORTED, WARN;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    public void work(@NonNull String key, @NonNull BiConsumer<String, String> acceptor) {
        if (this != UNSPECIFIED) acceptor.accept(key, toString());
    }
}
