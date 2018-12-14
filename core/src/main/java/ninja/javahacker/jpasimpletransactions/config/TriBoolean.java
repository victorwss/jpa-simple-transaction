package ninja.javahacker.jpasimpletransactions.config;

import java.util.Locale;
import java.util.function.BiConsumer;
import lombok.NonNull;

/**
 * Specifies the values true, false, or unspecified.
 * @see PersistenceProperties#getCreateDatabaseSchemas()
 * @see PersistenceProperties#withCreateDatabaseSchemas(TriBoolean)
 * @see PersistenceProperties#getShowSql()
 * @see PersistenceProperties#withShowSql(TriBoolean)
 * @see PersistenceProperties#getFormatSql()
 * @see PersistenceProperties#withFormatSql(TriBoolean)
 * @see PersistenceProperties#getUseSqlComments()
 * @see PersistenceProperties#withUseSqlComments(TriBoolean)
 * @see PersistenceProperties#getNewGeneratorMappings()
 * @see PersistenceProperties#withNewGeneratorMappings(TriBoolean)
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
