package ninja.javahacker.jpasimpletransactions.config;

import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.Value;

/**
 * Specifies the strategy used for automatic schema generation or validation.
 * @see PersistenceProperties#getSchemaGenerationAction()
 * @see PersistenceProperties#setSchemaGenerationAction(SchemaGenerationAction)
 * @see "The property {@code javax.persistence.schema-generation.database.action}."
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class SchemaGenerationAction {
    private final String name;

    public static SchemaGenerationAction unspecified() {
        return new SchemaGenerationAction("");
    }

    public static SchemaGenerationAction none() {
        return new SchemaGenerationAction("none");
    }

    public static SchemaGenerationAction drop() {
        return new SchemaGenerationAction("drop");
    }

    public static SchemaGenerationAction create() {
        return new SchemaGenerationAction("create");
    }

    public static SchemaGenerationAction dropAndCreate() {
        return new SchemaGenerationAction("drop-and-create");
    }

    public void work(@NonNull String key, @NonNull BiConsumer<String, String> acceptor) {
        if (!name.isEmpty()) acceptor.accept(key, name);
    }
}
