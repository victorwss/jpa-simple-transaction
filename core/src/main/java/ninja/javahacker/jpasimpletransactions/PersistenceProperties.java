package ninja.javahacker.jpasimpletransactions;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.Value;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
public interface PersistenceProperties {

    public ProviderAdapter getProviderAdapter();

    public String getPersistenceUnitName();

    public PersistenceProperties putExtra(String key, String value);

    public String getExtra(String key);

    public PersistenceProperties removeExtra(String key);

    public PersistenceProperties clearExtras();

    public Map<String, String> getExtras();

    public Map<String, String> buildCore();

    public default Map<String, String> build() {
        var props = buildCore();
        props.putAll(getExtras());
        return props;
    }

    /**
     * Specifies the strategy used for automatic schema generation or validation.
     * @see PersistenceProperties#getSchemaGenerationAction()
     * @see PersistenceProperties#setSchemaGenerationAction(SchemaGenerationAction)
     * @see "The property {@code javax.persistence.schema-generation.database.action}."
     */
    @Value
    public static final class SchemaGenerationAction {
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

    /**
     * Specifies the strategy used for automatic schema generation or validation.
     * @see PersistenceProperties#getSchemaGenerationScriptsCreate()
     * @see PersistenceProperties#withSchemaGenerationScriptsCreate(SchemaGenerationActionTarget)
     * @see "The property {@code javax.persistence.schema-generation.scripts.action}."
     * @see "The property {@code javax.persistence.schema-generation.scripts.create-target}."
     * @see "The property {@code javax.persistence.schema-generation.scripts.drop-target}."
     */
    @Value
    public static final class SchemaGenerationActionTarget {
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
     */
    @Value
    public static final class SchemaGenerationSource {
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
     */
    public static enum TriBoolean {
        UNSPECIFIED, FALSE, TRUE;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static TriBoolean from(boolean b) {
            return b ? TRUE : FALSE;
        }

        public void work(@NonNull String key, @NonNull BiConsumer<String, String> acceptor) {
            if (this == UNSPECIFIED) return;
            acceptor.accept(key, this == TRUE ? "true" : "false");
        }
    }
}
