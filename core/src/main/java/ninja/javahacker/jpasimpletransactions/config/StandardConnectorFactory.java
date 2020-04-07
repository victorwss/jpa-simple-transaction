package ninja.javahacker.jpasimpletransactions.config;

import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.Connector;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 *
 * @param <E> The properties class for the specific JPA vendor.
 * @author Victor Williams Stafusa da Silva
 */
public interface StandardConnectorFactory<E extends StandardConnectorFactory<E>> extends ConnectorFactory {

    public E withPersistenceUnitName(@NonNull String persistenceUnitName);

    public Class<? extends Driver> getDriver();

    public default E withDriver(@NonNull String driverName) throws ClassNotFoundException {
        return withDriver(Class.forName(driverName).asSubclass(Driver.class));
    }

    public E withDriver(@NonNull Class<? extends Driver> driver);

    public String getUrl();

    public E withUrl(@NonNull String url);

    public String getUser();

    public E withUser(@NonNull String user);

    public String getPassword();

    public E withPassword(@NonNull String password);

    public SchemaGenerationAction getSchemaGenerationAction();

    public E withSchemaGenerationAction(@NonNull SchemaGenerationAction schemaGenerationAction);

    public SchemaGenerationSource getSchemaGenerationCreate();

    public E withSchemaGenerationCreate(@NonNull SchemaGenerationSource schemaGenerationCreate);

    public SchemaGenerationSource getSchemaGenerationDrop();

    public E withSchemaGenerationDrop(@NonNull SchemaGenerationSource schemaGenerationDrop);

    public SchemaGenerationActionTarget getSchemaGenerationScriptsCreate();

    public E withSchemaGenerationScriptsCreate(@NonNull SchemaGenerationActionTarget schemaGenerationScriptsCreate);

    public String getLoadScript();

    public E withLoadScript(@NonNull String loadScript);

    public String getSchemaGenerationConnection();

    public E withSchemaGenerationConnection(@NonNull String schemaGenerationConnection);

    public TriBoolean getCreateDatabaseSchemas();

    public E withCreateDatabaseSchemas(@NonNull TriBoolean createDatabaseSchemas);

    public default E withCreateDatabaseSchemas(boolean newValue) {
        return withCreateDatabaseSchemas(TriBoolean.from(newValue));
    }

    public String getDatabaseProductName();

    public E withDatabaseProductName(@NonNull String databaseProductName);

    public String getDatabaseMajorVersion();

    public E withDatabaseMajorVersion(@NonNull String databaseMajorVersion);

    public String getDatabaseMinorVersion();

    public E withDatabaseMinorVersion(@NonNull String databaseMinorVersion);

    public Map<String, String> getExtras();

    public default String getExtra(String key) {
        return getExtras().get(key);
    }

    public E withExtras(@NonNull Map<String, String> extras);

    public default E putExtra(String key, String value) {
        var m = new HashMap<>(getExtras());
        m.put(key, value);
        return withExtras(Map.copyOf(m));
    }

    public default E removeExtra(String key) {
        var m = new HashMap<>(getExtras());
        m.remove(key);
        return withExtras(Map.copyOf(m));
    }

    public default E clearExtras() {
        return withExtras(Map.of());
    }

    public default Map<String, String> getProperties() {
        Map<String, String> props = new HashMap<>(64);
        Class<? extends Driver> d = getDriver();
        if (d != Driver.class) props.put("javax.persistence.jdbc.driver", d.getName());
        work("javax.persistence.jdbc.url", getUrl(), props::put);
        work("javax.persistence.jdbc.user", getUser(), props::put);
        work("javax.persistence.jdbc.password", getPassword(), props::put);
        work("javax.persistence.database-product-name", getDatabaseProductName(), props::put);
        work("javax.persistence.database-major-version", getDatabaseMajorVersion(), props::put);
        work("javax.persistence.database-minor-version", getDatabaseMinorVersion(), props::put);
        getSchemaGenerationAction().work("javax.persistence.schema-generation.database.action", props::put);
        getSchemaGenerationCreate().work(
                "javax.persistence.schema-generation.create-source",
                "javax.persistence.schema-generation.create-script-source",
                props::put);
        getSchemaGenerationDrop().work(
                "javax.persistence.schema-generation.drop-source",
                "javax.persistence.schema-generation.drop-script-source",
                props::put);
        getSchemaGenerationScriptsCreate().work(
                "javax.persistence.schema-generation.scripts.action",
                "javax.persistence.schema-generation.scripts.create-target",
                "javax.persistence.schema-generation.scripts.drop-target",
                props::put);
        work("javax.persistence.sql-load-script-source", getLoadScript(), props::put);
        work("javax.persistence.schema-generation.connection", getSchemaGenerationConnection(), props::put);
        getCreateDatabaseSchemas().work("javax.persistence.schema-generation.create-database-schemas", props::put);
        props.putAll(getExtras());
        return props;
    }

    public static void work(@NonNull String key, @NonNull String value, @NonNull BiConsumer<String, String> acceptor) {
        if (!value.isEmpty()) acceptor.accept(key, value);
    }
}
