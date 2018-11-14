package ninja.javahacker.jpasimpletransactions.hibernate;

import java.sql.Driver;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Tolerate;
import lombok.experimental.Wither;
import ninja.javahacker.jpasimpletransactions.DefaultPersistenceProperties;
import ninja.javahacker.jpasimpletransactions.PersistenceProperties;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
@Wither
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HibernatePersistenceProperties implements PersistenceProperties {

    @Delegate(excludes = Dont.class)
    @NonNull
    final DefaultPersistenceProperties defaultProperties;

    @NonNull Class<?> dialect = void.class;
    @NonNull Class<?> jtaPlatform = void.class;
    @NonNull String schema = "";
    @NonNull TriBoolean showSql = TriBoolean.UNSPECIFIED;
    @NonNull TriBoolean formatSql = TriBoolean.UNSPECIFIED;
    @NonNull TriBoolean useSqlComments = TriBoolean.UNSPECIFIED;
    boolean multipleLinesCommands = true;
    @NonNull TriBoolean newGeneratorMappings = TriBoolean.UNSPECIFIED;

    public HibernatePersistenceProperties(@NonNull String persistenceUnitName) {
        this.defaultProperties = new DefaultPersistenceProperties(HibernateAdapter.INSTANCE, persistenceUnitName);
    }

    @Tolerate
    public HibernatePersistenceProperties withDialect(@NonNull String dialectName) throws ClassNotFoundException {
        return withDialect(Class.forName(dialectName));
    }

    @Tolerate
    public HibernatePersistenceProperties withJtaPlatform(@NonNull String jtaPlatformName) throws ClassNotFoundException {
        return withJtaPlatform(Class.forName(jtaPlatformName));
    }

    @Tolerate
    public HibernatePersistenceProperties withShowSql(boolean newValue) {
        this.showSql = TriBoolean.from(newValue);
        return this;
    }

    @Tolerate
    public HibernatePersistenceProperties withFormatSql(boolean newValue) {
        this.formatSql = TriBoolean.from(newValue);
        return this;
    }

    @Tolerate
    public HibernatePersistenceProperties withUseSqlComments(boolean newValue) {
        this.useSqlComments = TriBoolean.from(newValue);
        return this;
    }

    @Tolerate
    public HibernatePersistenceProperties withNewGeneratorMappings(boolean newValue) {
        this.newGeneratorMappings = TriBoolean.from(newValue);
        return this;
    }

    @Override
    public Map<String, String> build() {
        Map<String, String> props = defaultProperties.buildCore();

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
        return props;
    }

    private void work(@NonNull String key, @NonNull String value, @NonNull BiConsumer<String, String> acceptor) {
        if (!value.isEmpty()) acceptor.accept(key, value);
    }

    /**
     * Method already implemented, so shouldn't delegate to those.
     */
    private static interface Dont {

        public HibernatePersistenceProperties withCreateDatabaseSchemas(boolean newValue);

        public HibernatePersistenceProperties withCreateDatabaseSchemas(TriBoolean newValue);

        public HibernatePersistenceProperties withCreateDatabaseMajorVersion(String newValue);

        public HibernatePersistenceProperties withCreateDatabaseMinorVersion(String newValue);

        public HibernatePersistenceProperties withDatabaseProductName(String newValue);

        public HibernatePersistenceProperties withDriver(String driverName) throws ClassNotFoundException;

        public HibernatePersistenceProperties withDriver(Class<? extends Driver> driver) throws ClassNotFoundException;

        public HibernatePersistenceProperties withExtras(Map<String, String> values) throws ClassNotFoundException;

        public HibernatePersistenceProperties withLoadScript(String loadScript) throws ClassNotFoundException;

        public HibernatePersistenceProperties withPassword(String password) throws ClassNotFoundException;

        public HibernatePersistenceProperties withPersistenceUnitName(String persistenceUnitName);

        public HibernatePersistenceProperties withSchemaGenerationAction(SchemaGenerationAction action);

        public HibernatePersistenceProperties withSchemaGenerationConnection(String connection);

        public HibernatePersistenceProperties withSchemaGenerationCreate(SchemaGenerationSource source);

        public HibernatePersistenceProperties withSchemaGenerationDrop(SchemaGenerationSource source);

        public HibernatePersistenceProperties withSchemaGenerationScriptsCreate(SchemaGenerationActionTarget source);

        public HibernatePersistenceProperties withUrl(String url);

        public HibernatePersistenceProperties withUser(String user);
    }

    public HibernatePersistenceProperties withCreateDatabaseSchemas(boolean newValue) {
        defaultProperties.withCreateDatabaseSchemas(newValue);
        return this;
    }

    public HibernatePersistenceProperties withCreateDatabaseSchemas(TriBoolean newValue) {
        defaultProperties.withCreateDatabaseSchemas(newValue);
        return this;
    }

    public HibernatePersistenceProperties withCreateDatabaseMajorVersion(String newValue) {
        defaultProperties.withDatabaseMajorVersion(newValue);
        return this;
    }

    public HibernatePersistenceProperties withCreateDatabaseMinorVersion(String newValue) {
        defaultProperties.withDatabaseMinorVersion(newValue);
        return this;
    }

    public HibernatePersistenceProperties withDatabaseProductName(String newValue) {
        defaultProperties.withDatabaseProductName(newValue);
        return this;
    }

    public HibernatePersistenceProperties withDriver(String driverName) throws ClassNotFoundException {
        defaultProperties.withDriver(driverName);
        return this;
    }

    public HibernatePersistenceProperties withDriver(Class<? extends Driver> driver) throws ClassNotFoundException {
        defaultProperties.withDriver(driver);
        return this;
    }

    public HibernatePersistenceProperties withExtras(Map<String, String> values) throws ClassNotFoundException {
        defaultProperties.withExtras(values);
        return this;
    }

    public HibernatePersistenceProperties withLoadScript(String loadScript) throws ClassNotFoundException {
        defaultProperties.withLoadScript(loadScript);
        return this;
    }

    public HibernatePersistenceProperties withPassword(String password) throws ClassNotFoundException {
        defaultProperties.withPassword(password);
        return this;
    }

    public HibernatePersistenceProperties withPersistenceUnitName(String persistenceUnitName) {
        defaultProperties.withPersistenceUnitName(persistenceUnitName);
        return this;
    }

    public HibernatePersistenceProperties withSchemaGenerationAction(SchemaGenerationAction action) {
        defaultProperties.withSchemaGenerationAction(action);
        return this;
    }

    public HibernatePersistenceProperties withSchemaGenerationConnection(String connection) {
        defaultProperties.withSchemaGenerationConnection(connection);
        return this;
    }

    public HibernatePersistenceProperties withSchemaGenerationCreate(SchemaGenerationSource source) {
        defaultProperties.withSchemaGenerationCreate(source);
        return this;
    }

    public HibernatePersistenceProperties withSchemaGenerationDrop(SchemaGenerationSource source) {
        defaultProperties.withSchemaGenerationDrop(source);
        return this;
    }

    public HibernatePersistenceProperties withSchemaGenerationScriptsCreate(SchemaGenerationActionTarget source) {
        defaultProperties.withSchemaGenerationScriptsCreate(source);
        return this;
    }

    public HibernatePersistenceProperties withUrl(String url) {
        defaultProperties.withUrl(url);
        return this;
    }

    public HibernatePersistenceProperties withUser(String user) {
        defaultProperties.withUser(user);
        return this;
    }
}
