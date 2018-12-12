package ninja.javahacker.jpasimpletransactions.properties;

import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.Value;

/**
 * Specifies the strategy used for executing custom scripts on schema generation.
 * @see PersistenceProperties#getSchemaGenerationAction()
 * @see PersistenceProperties#withSchemaGenerationAction(SchemaGenerationSource)
 * @see PersistenceProperties#getSchemaGenerationCreate()
 * @see PersistenceProperties#withSchemaGenerationCreate(SchemaGenerationSource)
 * @see PersistenceProperties#getSchemaGenerationDrop()
 * @see PersistenceProperties#withSchemaGenerationDrop(SchemaGenerationSource)
 * @see "The property {@code javax.persistence.schema-generation.create-source}."
 * @see "The property {@code javax.persistence.schema-generation.create-script-source}."
 * @see "The property {@code javax.persistence.schema-generation.drop-source}."
 * @see "The property {@code javax.persistence.schema-generation.drop-script-source}."
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class SchemaGenerationSource {
    private final String name;
    private final String script;

    public static SchemaGenerationSource unspecified() {
        return new SchemaGenerationSource("", "");
    }

    public static SchemaGenerationSource metadata() {
        return new SchemaGenerationSource("metadata", "");
    }

    public static SchemaGenerationSource script(@NonNull String script) {
        return new SchemaGenerationSource("script", script);
    }

    public static SchemaGenerationSource metadataThenScript(@NonNull String script) {
        return new SchemaGenerationSource("metadata-then-script", script);
    }

    public static SchemaGenerationSource scriptThenMetadata(@NonNull String script) {
        return new SchemaGenerationSource("script-then-metadata", script);
    }

    public void work(@NonNull String key, @NonNull String scriptKey, @NonNull BiConsumer<String, String> acceptor) {
        if (!name.isEmpty()) acceptor.accept(key, name);
        if (!script.isEmpty()) acceptor.accept(scriptKey, script);
    }
}
