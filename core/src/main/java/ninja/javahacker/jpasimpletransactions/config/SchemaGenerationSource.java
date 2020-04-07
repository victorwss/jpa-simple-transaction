package ninja.javahacker.jpasimpletransactions.config;

import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.Value;

/**
 * Specifies the strategy used for executing custom scripts on schema generation.
 * <p>Used to set the following properties:</p>
 * <ul>
 * <li>{@code javax.persistence.schema-generation.create-source}.</li>
 * <li>{@code javax.persistence.schema-generation.create-script-source}.</li>
 * <li>{@code javax.persistence.schema-generation.drop-source}.</li>
 * <li>{@code javax.persistence.schema-generation.drop-script-source}.</li>
 * </ul>
 * @see StandardConnectorFactory#getSchemaGenerationCreate()
 * @see StandardConnectorFactory#withSchemaGenerationCreate(SchemaGenerationSource)
 * @see StandardConnectorFactory#getSchemaGenerationDrop()
 * @see StandardConnectorFactory#withSchemaGenerationDrop(SchemaGenerationSource)
 * @see #unspecified()
 * @see #metadata()
 * @see #script(String)
 * @see #metadataThenScript(String)
 * @see #scriptThenMetadata(String)
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
