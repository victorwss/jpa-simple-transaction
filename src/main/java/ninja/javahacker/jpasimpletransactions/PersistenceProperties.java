package ninja.javahacker.jpasimpletransactions;

import java.sql.Driver;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * @author Victor Williams Stafusad a Silva
 */
@Getter
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PersistenceProperties {
    @NonNull private Class<? extends Driver> driver = Driver.class;
    @NonNull private Class<?> dialect = void.class;
    @NonNull private Class<?> jtaPlatform = void.class;
    @NonNull private String url = "";
    @NonNull private String user = "";
    @NonNull private String password = "";
    @NonNull private SchemaGenerationAction schemaGenerationAction = SchemaGenerationAction.unspecified();
    @NonNull private SchemaGenerationSource schemaGenerationCreate = SchemaGenerationSource.unspecified();
    @NonNull private SchemaGenerationSource schemaGenerationDrop = SchemaGenerationSource.unspecified();
    @NonNull private SchemaGenerationActionTarget schemaGenerationScriptsCreate = SchemaGenerationActionTarget.unspecified();
    @NonNull private String loadScript = "";
    @NonNull private String schemaGenerationConnection = "";
    @NonNull private Hbm2DdlAuto hbm2DdlAuto = Hbm2DdlAuto.unspecified();
    @NonNull private TriBoolean createDatabaseSchemas = TriBoolean.UNSPECIFIED;
    @NonNull private TriBoolean showSql = TriBoolean.UNSPECIFIED;
    @NonNull private TriBoolean formatSql = TriBoolean.UNSPECIFIED;
    @NonNull private TriBoolean useSqlComments = TriBoolean.UNSPECIFIED;
    private boolean multipleLinesCommands = true;
    @NonNull private String databaseProductName = "";
    @NonNull private String databaseMajorVersion = "";
    @NonNull private String databaseMinorVersion = "";
    @NonNull private TriBoolean newGeneratorMappings = TriBoolean.UNSPECIFIED;

    @NonNull
    private final Map<String, String> extras = new HashMap<>();

    public PersistenceProperties withDriverName(@NonNull String drivername) throws ClassNotFoundException {
        return withDriver(Class.forName(drivername).asSubclass(Driver.class));
    }

    public PersistenceProperties withDialectName(@NonNull String dialectName) throws ClassNotFoundException {
        return withDialect(Class.forName(dialectName));
    }

    public PersistenceProperties withJtaPlatformName(@NonNull String jtaPlatformName) throws ClassNotFoundException {
        return withJtaPlatform(Class.forName(jtaPlatformName));
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
        Map<String, String> props = new HashMap<>();
        if (driver != Driver.class) props.put("javax.persistence.jdbc.driver", driver.getName());
        if (!url.isEmpty()) props.put("javax.persistence.jdbc.url", url);
        if (!user.isEmpty()) props.put("javax.persistence.jdbc.user", user);
        if (!password.isEmpty()) props.put("javax.persistence.jdbc.password", password);
        if (!databaseProductName.isEmpty()) props.put("javax.persistence.database-product-name", databaseProductName);
        if (!databaseMajorVersion.isEmpty()) props.put("javax.persistence.database-major-version", databaseMajorVersion);
        if (!databaseMinorVersion.isEmpty()) props.put("javax.persistence.database-minor-version", databaseMinorVersion);
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
        if (!loadScript.isEmpty()) props.put("javax.persistence.sql-load-script-source", loadScript);
        if (!schemaGenerationConnection.isEmpty()) {
            props.put("javax.persistence.schema-generation.connection", schemaGenerationConnection);
        }
        if (dialect != void.class) props.put("hibernate.dialect", dialect.getName());
        if (jtaPlatform != void.class) props.put("hibernate.transaction.jta.platform", jtaPlatform.getName());
        if (createDatabaseSchemas != TriBoolean.UNSPECIFIED) {
            props.put("javax.persistence.schema-generation.create-database-schemas", createDatabaseSchemas.toString());
        }
        if (multipleLinesCommands) {
            props.put(
                    "hibernate.hbm2ddl.import_files_sql_extractor",
                    "org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor");
        }
        if (showSql != TriBoolean.UNSPECIFIED) props.put("hibernate.show_sql", showSql.toString());
        if (formatSql != TriBoolean.UNSPECIFIED) props.put("hibernate.format_sql", formatSql.toString());
        if (useSqlComments != TriBoolean.UNSPECIFIED) props.put("hibernate.use_sql_comments", useSqlComments.toString());
        if (newGeneratorMappings != TriBoolean.UNSPECIFIED) {
            props.put("hibernate.id.new_generator_mappings", newGeneratorMappings.toString());
        }
        props.putAll(extras);
        return props;
    }

    @Value
    public static class SchemaGenerationAction {
        private final Optional<String> name;

        public static SchemaGenerationAction unspecified() {
            return new SchemaGenerationAction(Optional.empty());
        }

        public static SchemaGenerationAction none() {
            return new SchemaGenerationAction(Optional.of("none"));
        }

        public static SchemaGenerationAction drop() {
            return new SchemaGenerationAction(Optional.of("drop"));
        }

        public static SchemaGenerationAction create() {
            return new SchemaGenerationAction(Optional.of("create"));
        }

        public static SchemaGenerationAction dropAndCreate() {
            return new SchemaGenerationAction(Optional.of("drop-and-create"));
        }

        public void work(@NonNull String key, @NonNull BiConsumer<String, String> acceptor) {
            name.ifPresent(n -> acceptor.accept(key, n));
        }
    }

    @Value
    public static class SchemaGenerationActionTarget {
        private final Optional<String> name;
        private final Optional<String> createScript;
        private final Optional<String> dropScript;

        public static SchemaGenerationActionTarget unspecified() {
            return new SchemaGenerationActionTarget(Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static SchemaGenerationActionTarget none() {
            return new SchemaGenerationActionTarget(Optional.of("none"), Optional.empty(), Optional.empty());
        }

        public static SchemaGenerationActionTarget drop(@NonNull String dropScript) {
            return new SchemaGenerationActionTarget(Optional.of("drop"), Optional.empty(), Optional.of(dropScript));
        }

        public static SchemaGenerationActionTarget create(@NonNull String createScript) {
            return new SchemaGenerationActionTarget(Optional.of("create"), Optional.of(createScript), Optional.empty());
        }

        public static SchemaGenerationActionTarget dropAndCreate(@NonNull String createScript, @NonNull String dropScript) {
            return new SchemaGenerationActionTarget(Optional.of("drop-and-create"), Optional.of(createScript), Optional.of(dropScript));
        }

        public void work(
                @NonNull String key,
                @NonNull String createKey,
                @NonNull String dropKey,
                @NonNull BiConsumer<String, String> acceptor)
        {
            name.ifPresent(n -> acceptor.accept(key, n));
            createScript.ifPresent(n -> acceptor.accept(createKey, n));
            dropScript.ifPresent(n -> acceptor.accept(dropKey, n));
        }
    }

    @Value
    public static class Hbm2DdlAuto {
        private final Optional<String> name;

        public static Hbm2DdlAuto unspecified() {
            return new Hbm2DdlAuto(Optional.empty());
        }

        public static Hbm2DdlAuto validate() {
            return new Hbm2DdlAuto(Optional.of("validate"));
        }

        public static Hbm2DdlAuto update() {
            return new Hbm2DdlAuto(Optional.of("update"));
        }

        public static Hbm2DdlAuto create() {
            return new Hbm2DdlAuto(Optional.of("create"));
        }

        public static Hbm2DdlAuto createDrop() {
            return new Hbm2DdlAuto(Optional.of("create-drop"));
        }

        public void work(@NonNull String key, @NonNull BiConsumer<String, String> acceptor) {
            name.ifPresent(n -> acceptor.accept(key, n));
        }
    }

    @Value
    public static class SchemaGenerationSource {
        private final Optional<String> name;
        private final Optional<String> script;

        public static SchemaGenerationSource unspecified() {
            return new SchemaGenerationSource(Optional.empty(), Optional.empty());
        }

        public static SchemaGenerationSource metadata() {
            return new SchemaGenerationSource(Optional.of("metadata"), Optional.empty());
        }

        public static SchemaGenerationSource script(@NonNull String script) {
            return new SchemaGenerationSource(Optional.of("script"), Optional.of(script));
        }

        public static SchemaGenerationSource metadataThenScript(@NonNull String script) {
            return new SchemaGenerationSource(Optional.of("metadata-then-script"), Optional.of(script));
        }

        public static SchemaGenerationSource scriptThenMetadata(@NonNull String script) {
            return new SchemaGenerationSource(Optional.of("script-then-metadata"), Optional.of(script));
        }

        public void work(@NonNull String key, @NonNull String scriptKey, @NonNull BiConsumer<String, String> acceptor) {
            name.ifPresent(n -> acceptor.accept(key, n));
            script.ifPresent(n -> acceptor.accept(scriptKey, n));
        }
    }

    public static enum TriBoolean {
        UNSPECIFIED, FALSE, TRUE;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
