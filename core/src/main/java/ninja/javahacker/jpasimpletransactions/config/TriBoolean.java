package ninja.javahacker.jpasimpletransactions.config;

import java.util.Locale;
import java.util.function.BiConsumer;
import lombok.NonNull;

/**
 * An enum that defines the values true, false, or unspecified.
 * @author Victor Williams Stafusa da Silva
 */
public enum TriBoolean {
    UNSPECIFIED, FALSE, TRUE;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static TriBoolean from(boolean b) {
        return b ? TRUE : FALSE;
    }

    public void work(@NonNull String key, @NonNull BiConsumer<String, String> acceptor) {
        if (this != UNSPECIFIED) acceptor.accept(key, this == TRUE ? "true" : "false");
    }
}
