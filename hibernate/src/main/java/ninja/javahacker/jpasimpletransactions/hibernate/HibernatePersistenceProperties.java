package ninja.javahacker.jpasimpletransactions.hibernate;

import java.sql.Driver;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Tolerate;
import lombok.experimental.Wither;
import ninja.javahacker.jpasimpletransactions.properties.SchemaGenerationAction;
import ninja.javahacker.jpasimpletransactions.properties.SchemaGenerationActionTarget;
import ninja.javahacker.jpasimpletransactions.properties.SchemaGenerationSource;
import ninja.javahacker.jpasimpletransactions.properties.StandardPersistenceProperties;
import ninja.javahacker.jpasimpletransactions.properties.TriBoolean;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@Wither
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HibernatePersistenceProperties implements StandardPersistenceProperties<HibernatePersistenceProperties> {

    @NonNull String persistenceUnitName;
    @NonNull Class<? extends Driver> driver;
    @NonNull String url;
    @NonNull String user;
    @NonNull String password;
    @NonNull SchemaGenerationAction schemaGenerationAction;
    @NonNull SchemaGenerationSource schemaGenerationCreate;
    @NonNull SchemaGenerationSource schemaGenerationDrop;
    @NonNull SchemaGenerationActionTarget schemaGenerationScriptsCreate;
    @NonNull String loadScript;
    @NonNull String schemaGenerationConnection;
    @NonNull TriBoolean createDatabaseSchemas;
    @NonNull String databaseProductName;
    @NonNull String databaseMajorVersion;
    @NonNull String databaseMinorVersion;
    @NonNull Map<String, String> extras;

    @NonNull Class<?> dialect;
    @NonNull Class<?> jtaPlatform;
    @NonNull String schema;
    @NonNull TriBoolean showSql;
    @NonNull TriBoolean formatSql;
    @NonNull TriBoolean useSqlComments;
    boolean multipleLinesCommands;
    @NonNull TriBoolean newGeneratorMappings;

    public HibernatePersistenceProperties() {
        this.persistenceUnitName = "";
        this.driver = Driver.class;
        this.url = "";
        this.user = "";
        this.password = "";
        this.schemaGenerationAction = SchemaGenerationAction.unspecified();
        this.schemaGenerationCreate = SchemaGenerationSource.unspecified();
        this.schemaGenerationDrop = schemaGenerationCreate;
        this.schemaGenerationScriptsCreate = SchemaGenerationActionTarget.unspecified();
        this.loadScript = "";
        this.schemaGenerationConnection = "";
        this.createDatabaseSchemas = TriBoolean.UNSPECIFIED;
        this.databaseProductName = "";
        this.databaseMajorVersion = "";
        this.databaseMinorVersion = "";
        this.extras = Map.of();

        this.dialect = void.class;
        this.jtaPlatform = void.class;
        this.schema = "";
        this.showSql = TriBoolean.UNSPECIFIED;
        this.formatSql = TriBoolean.UNSPECIFIED;
        this.useSqlComments = TriBoolean.UNSPECIFIED;
        this.multipleLinesCommands = true;
        this.newGeneratorMappings = TriBoolean.UNSPECIFIED;
    }

    @Override
    public HibernateAdapter getProviderAdapter() {
        return HibernateAdapter.CANONICAL;
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
        return withShowSql(TriBoolean.from(newValue));
    }

    @Tolerate
    public HibernatePersistenceProperties withFormatSql(boolean newValue) {
        return withFormatSql(TriBoolean.from(newValue));
    }

    @Tolerate
    public HibernatePersistenceProperties withUseSqlComments(boolean newValue) {
        return withUseSqlComments(TriBoolean.from(newValue));
    }

    @Tolerate
    public HibernatePersistenceProperties withNewGeneratorMappings(boolean newValue) {
        return withNewGeneratorMappings(TriBoolean.from(newValue));
    }

    @Override
    public Map<String, String> build() {
        var props = StandardPersistenceProperties.super.build();

        StandardPersistenceProperties.work("hibernate.default_schema", getSchema(), props::put);
        Class<?> d = getDialect();
        if (d != void.class) props.put("hibernate.dialect", d.getName());
        Class<?> p = getJtaPlatform();
        if (p != void.class) props.put("hibernate.transaction.jta.platform", p.getName());
        if (isMultipleLinesCommands()) {
            props.put(
                    "hibernate.hbm2ddl.import_files_sql_extractor",
                    "org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor");
        }
        getShowSql().work("hibernate.show_sql", props::put);
        getFormatSql().work("hibernate.format_sql", props::put);
        getUseSqlComments().work("hibernate.use_sql_comments", props::put);
        getNewGeneratorMappings().work("hibernate.id.new_generator_mappings", props::put);
        return props;
    }
}
