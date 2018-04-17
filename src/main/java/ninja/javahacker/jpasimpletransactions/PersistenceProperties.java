package ninja.javahacker.jpasimpletransactions;

import java.sql.Driver;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.Value;

/**
 * @author Victor Williams Stafusad a Silva
 */
@Data
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
    @NonNull private String load = "";
    @NonNull private String schemaGenerationConnection = "";
    @NonNull private Hbm2DdlAuto ddl = Hbm2DdlAuto.unspecified();
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
    @Setter(AccessLevel.PRIVATE)
    private Map<String, String> extras = new HashMap<>();

    public void setDriverName(@NonNull String driver) throws ClassNotFoundException {
        setDriver(Class.forName(driver).asSubclass(Driver.class));
    }

    public void setDialectName(@NonNull String dialect) throws ClassNotFoundException {
        setDialect(Class.forName(dialect));
    }

    public void setJtaPlatformName(@NonNull String jtaPlatform) throws ClassNotFoundException {
        setJtaPlatform(Class.forName(jtaPlatform));
    }

    public void putExtra(String key, String value) {
        extras.put(key, value);
    }

    public void getExtra(String key) {
        extras.get(key);
    }

    public void removeExtra(String key) {
        extras.remove(key);
    }

    public void clearExtras() {
        extras.clear();
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
        schemaGenerationAction.work("javax.persistence.schema-generation.database.action", props);
        schemaGenerationCreate.work(
                "javax.persistence.schema-generation.create-source",
                "javax.persistence.schema-generation.create-script-source",
                props);
        schemaGenerationDrop.work(
                "javax.persistence.schema-generation.drop-source",
                "javax.persistence.schema-generation.drop-script-source",
                props);
        schemaGenerationScriptsCreate.work(
                "javax.persistence.schema-generation.scripts.action",
                "javax.persistence.schema-generation.scripts.create-target",
                "javax.persistence.schema-generation.scripts.drop-target",
                props);
        if (!load.isEmpty()) props.put("javax.persistence.sql-load-script-source", load);
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

        public void work(String key, Map<String, String> map) {
            name.ifPresent(n -> map.put(key, n));
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

        public void work(String key, String createKey, String dropKey, Map<String, String> map) {
            name.ifPresent(n -> map.put(key, n));
            createScript.ifPresent(n -> map.put(createKey, n));
            dropScript.ifPresent(n -> map.put(dropKey, n));
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

        public void work(String key, Map<String, String> map) {
            name.ifPresent(n -> map.put(key, n));
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

        public void work(String key, String scriptKey, Map<String, String> map) {
            name.ifPresent(n -> map.put(key, n));
            script.ifPresent(n -> map.put(scriptKey, n));
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
