package ninja.javahacker.jpasimpletransactions.properties;

import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.Value;

/**
 * Specifies the strategy used for automatic schema generation or validation.
 * @see PersistenceProperties#getSchemaGenerationScriptsCreate()
 * @see PersistenceProperties#withSchemaGenerationScriptsCreate(SchemaGenerationActionTarget)
 * @see "The property {@code javax.persistence.schema-generation.scripts.action}."
 * @see "The property {@code javax.persistence.schema-generation.scripts.create-target}."
 * @see "The property {@code javax.persistence.schema-generation.scripts.drop-target}."
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class SchemaGenerationActionTarget {
    private final String name;
    private final String createScript;
    private final String dropScript;

    public static SchemaGenerationActionTarget unspecified() {
        return new SchemaGenerationActionTarget("", "", "");
    }

    public static SchemaGenerationActionTarget none() {
        return new SchemaGenerationActionTarget("none", "", "");
    }

    public static SchemaGenerationActionTarget drop(@NonNull String dropScript) {
        return new SchemaGenerationActionTarget("drop", "", dropScript);
    }

    public static SchemaGenerationActionTarget create(@NonNull String createScript) {
        return new SchemaGenerationActionTarget("create", createScript, "");
    }

    public static SchemaGenerationActionTarget dropAndCreate(@NonNull String createScript, @NonNull String dropScript) {
        return new SchemaGenerationActionTarget("drop-and-create", createScript, dropScript);
    }

    public void work(
            @NonNull String key,
            @NonNull String createKey,
            @NonNull String dropKey,
            @NonNull BiConsumer<String, String> acceptor)
    {
        if (!name.isEmpty()) acceptor.accept(key, name);
        if (!createScript.isEmpty()) acceptor.accept(createKey, createScript);
        if (!dropScript.isEmpty()) acceptor.accept(dropKey, dropScript);
    }
}
