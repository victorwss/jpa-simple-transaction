package ninja.javahacker.jpasimpletransactions;

import java.sql.Driver;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Tolerate;
import lombok.experimental.Wither;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PersistenceProperties {
    @NonNull String persistenceUnitName;
    @NonNull Class<? extends Driver> driver = Driver.class;
    @NonNull Class<?> dialect = void.class;
    @NonNull Class<?> jtaPlatform = void.class;
    @NonNull String url = "";
    @NonNull String user = "";
    @NonNull String password = "";
    @NonNull String schema = "";
    @NonNull SchemaGenerationAction schemaGenerationAction = SchemaGenerationAction.unspecified();
    @NonNull SchemaGenerationSource schemaGenerationCreate = SchemaGenerationSource.unspecified();
    @NonNull SchemaGenerationSource schemaGenerationDrop = SchemaGenerationSource.unspecified();
    @NonNull SchemaGenerationActionTarget schemaGenerationScriptsCreate = SchemaGenerationActionTarget.unspecified();
    @NonNull String loadScript = "";
    @NonNull String schemaGenerationConnection = "";
    @NonNull TriBoolean createDatabaseSchemas = TriBoolean.UNSPECIFIED;
    @NonNull TriBoolean showSql = TriBoolean.UNSPECIFIED;
    @NonNull TriBoolean formatSql = TriBoolean.UNSPECIFIED;
    @NonNull TriBoolean useSqlComments = TriBoolean.UNSPECIFIED;
    boolean multipleLinesCommands = true;
    @NonNull String databaseProductName = "";
    @NonNull String databaseMajorVersion = "";
    @NonNull String databaseMinorVersion = "";
    @NonNull TriBoolean newGeneratorMappings = TriBoolean.UNSPECIFIED;

    @NonNull Map<String, String> extras = new HashMap<>(20);

    @Tolerate
    public PersistenceProperties withDriver(@NonNull String driverName) throws ClassNotFoundException {
        return withDriver(Class.forName(driverName).asSubclass(Driver.class));
    }

    @Tolerate
    public PersistenceProperties withDialect(@NonNull String dialectName) throws ClassNotFoundException {
        return withDialect(Class.forName(dialectName));
    }

    @Tolerate
    public PersistenceProperties withJtaPlatform(@NonNull String jtaPlatformName) throws ClassNotFoundException {
        return withJtaPlatform(Class.forName(jtaPlatformName));
    }

    @Tolerate
    public PersistenceProperties withCreateDatabaseSchemas(boolean newValue) {
        this.createDatabaseSchemas = TriBoolean.from(newValue);
        return this;
    }

    @Tolerate
    public PersistenceProperties withShowSql(boolean newValue) {
        this.showSql = TriBoolean.from(newValue);
        return this;
    }

    @Tolerate
    public PersistenceProperties withFormatSql(boolean newValue) {
        this.formatSql = TriBoolean.from(newValue);
        return this;
    }

    @Tolerate
    public PersistenceProperties withUseSqlComments(boolean newValue) {
        this.useSqlComments = TriBoolean.from(newValue);
        return this;
    }

    @Tolerate
    public PersistenceProperties withNewGeneratorMappings(boolean newValue) {
        this.newGeneratorMappings = TriBoolean.from(newValue);
        return this;
    }

    public PersistenceProperties putExtra(String key, String value) {
        extras.put(key, value);
        return this;
    }

    public String getExtra(String key) {
        return extras.get(key);
    }

    public PersistenceProperties removeExtra(String key) {
        extras.remove(key);
        return this;
    }

    public PersistenceProperties clearExtras() {
        extras.clear();
        return this;
    }

    public Map<String, String> getExtras() {
        return Collections.unmodifiableMap(new HashMap<>(extras));
    }

    public Map<String, String> build() {
        Map<String, String> props = new HashMap<>(64);
        if (driver != Driver.class) props.put("javax.persistence.jdbc.driver", driver.getName());
        work("javax.persistence.jdbc.url", url, props::put);
        work("javax.persistence.jdbc.user", user, props::put);
        work("javax.persistence.jdbc.password", password, props::put);
        work("javax.persistence.database-product-name", databaseProductName, props::put);
        work("javax.persistence.database-major-version", databaseMajorVersion, props::put);
        work("javax.persistence.database-minor-version", databaseMinorVersion, props::put);
        schemaGenerationAction.work("javax.persistence.schema-generation.database.action", props::put);
        schemaGenerationCreate.work(
                "javax.persistence.schema-generation.create-source",
                "javax.persistence.schema-generation.create-script-source",
                props::put);
        schemaGenerationDrop.work(
                "javax.persistence.schema-generation.drop-source",
                "javax.persistence.schema-generation.drop-script-source",
                props::put);
        schemaGenerationScriptsCreate.work(
                "javax.persistence.schema-generation.scripts.action",
                "javax.persistence.schema-generation.scripts.create-target",
                "javax.persistence.schema-generation.scripts.drop-target",
                props::put);
        work("javax.persistence.sql-load-script-source", loadScript, props::put);
        work("javax.persistence.schema-generation.connection", schemaGenerationConnection, props::put);
        createDatabaseSchemas.work("javax.persistence.schema-generation.create-database-schemas", props::put);

        // TODO: Coupling with Hibernate.
        work("hibernate.default_schema", schema, props::put);
        if (dialect != void.class) props.put("hibernate.dialect", dialect.getName());
        if (jtaPlatform != void.class) props.put("hibernate.transaction.jta.platform", jtaPlatform.getName());
        if (multipleLinesCommands) {
            props.put(
                    "hibernate.hbm2ddl.import_files_sql_extractor",
                    "org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor");
        }
        showSql.work("hibernate.show_sql", props::put);
        formatSql.work("hibernate.format_sql", props::put);
        useSqlComments.work("hibernate.use_sql_comments", props::put);
        newGeneratorMappings.work("hibernate.id.new_generator_mappings", props::put);
        props.putAll(extras);
        return props;
    }

    private void work(@NonNull String key, @NonNull String value, @NonNull BiConsumer<String, String> acceptor) {
        if (!value.isEmpty()) acceptor.accept(key, value);
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
