package ninja.javahacker.jpasimpletransactions.config;

import jakarta.persistence.PersistenceConfiguration;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    /**
     * Sets the persistence unit's name into a new object.
     * @param persistenceUnitName The new persistence unit's name.
     * @return A new instance of this class which is similar to {@code this}, but with the given persistence unit's name.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withPersistenceUnitName(@NonNull String persistenceUnitName) throws IllegalArgumentException;

    /**
     * Gets the database's {@link Driver}.
     * @return The database's {@link Driver}.
     */
    public Class<? extends Driver> getDriver();

    /**
     * Sets the database's {@link Driver} into a new object.
     * @param driverName The new database's {@link Driver} name.
     * @return A new instance of this class which is similar to {@code this}, but with the given database's {@link Driver}.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     * @throws ClassNotFoundException If the given parameter does not resolves to any known class.
     */
    public default E withDriver(@NonNull String driverName) throws ClassNotFoundException, IllegalArgumentException {
        return withDriver(Class.forName(driverName).asSubclass(Driver.class));
    }

    /**
     * Sets the database's {@link Driver} into a new object.
     * @param driver The new database's {@link Driver}.
     * @return A new instance of this class which is similar to {@code this}, but with the given database's {@link Driver}.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withDriver(@NonNull Class<? extends Driver> driver) throws IllegalArgumentException;

    /**
     * Gets the database's URL for connection.
     * @return The database's URL for connection.
     */
    public String getUrl();

    /**
     * Sets the database's URL for connection into a new object.
     * @param url The new database's URL for connection.
     * @return A new instance of this class which is similar to {@code this}, but with the given database's URL for connection.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withUrl(@NonNull String url) throws IllegalArgumentException;

    /**
     * Gets the database's user for connection.
     * @return The database's user for connection.
     */
    public String getUser();

    /**
     * Sets the database's user for connection into a new object.
     * @param user The new database's user for connection.
     * @return A new instance of this class which is similar to {@code this}, but with the given database's user for connection.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withUser(@NonNull String user) throws IllegalArgumentException;

    /**
     * Gets the database's password for connection.
     * @return The database's password for connection.
     */
    public String getPassword();

    /**
     * Sets the database's password for connection into a new object.
     * @param password The new database's password for connection.
     * @return A new instance of this class which is similar to {@code this}, but with the given database's password for connection.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withPassword(@NonNull String password) throws IllegalArgumentException;

    /**
     * Gets the strategy used for automatic schema generation or validation.
     * @return The strategy used for automatic schema generation or validation.
     */
    public SchemaGenerationAction getSchemaGenerationAction();

    /**
     * Sets the strategy used for automatic schema generation or validation.
     * @param schemaGenerationAction The new strategy used for automatic schema generation or validation.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     strategy for automatic schema generation or validation.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withSchemaGenerationAction(@NonNull SchemaGenerationAction schemaGenerationAction) throws IllegalArgumentException;

    /**
     * Gets the strategy used for executing custom scripts on creating database artifacts.
     * @return The strategy used for executing custom scripts on creating database artifacts.
     */
    public SchemaGenerationSource getSchemaGenerationCreate();

    /**
     * Sets the strategy used for executing custom scripts on creating database artifacts.
     * @param schemaGenerationCreate The new strategy used for executing custom scripts on creating database artifacts.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     strategy used for executing custom scripts on creating database artifacts.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withSchemaGenerationCreate(@NonNull SchemaGenerationSource schemaGenerationCreate) throws IllegalArgumentException;

    /**
     * Gets the strategy used for executing custom scripts on dropping database artifacts.
     * @return The strategy used for executing custom scripts on dropping database artifacts.
     */
    public SchemaGenerationSource getSchemaGenerationDrop();

    /**
     * Sets the strategy used for executing custom scripts on dropping database artifacts.
     * @param schemaGenerationDrop The new strategy used for executing custom scripts on dropping database artifacts.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     strategy used for executing custom scripts on dropping database artifacts.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withSchemaGenerationDrop(@NonNull SchemaGenerationSource schemaGenerationDrop) throws IllegalArgumentException;

    /**
     * Tells which and where should scripts for table creation and droppings be stored.
     * @return The definition of which and where should scripts for table creation and droppings be stored.
     */
    public SchemaGenerationActionTarget getSchemaScriptStoreLocation();

    /**
     * Sets the strategy used for executing custom scripts on dropping database artifacts.
     * @param schemaScriptStoreLocation The definition of which and where should scripts for table creation and droppings be stored.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition of which and where should scripts for table creation and droppings be stored.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withSchemaScriptStoreLocation(@NonNull SchemaGenerationActionTarget schemaScriptStoreLocation) throws IllegalArgumentException;

    /**
     * Tells where the script for table initialization is stored, if it exists.
     * @return The place where the script for table initialization is stored, if it exists, or an empty string if there isn't any.
     */
    public String getLoadScript();

    /**
     * Sets the place where the script for table initialization is stored.
     * @param loadScript The place where the script for table initialization is stored.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition of where the script for table initialization is stored.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withLoadScript(@NonNull String loadScript) throws IllegalArgumentException;

    /**
     * Tells which is the JDBC connection that should be used for schema generation. This is intended mainly for Java EE / Jakarta EE
     * environments.
     * @return Which JDBC connection should be used for schema generation, or an empty string if there isn't any such definition.
     */
    public String getSchemaGenerationConnection();

    /**
     * Sets which is the JDBC connection that should be used for schema generation. This is intended mainly for Java EE / Jakarta EE
     * environments.
     * @param schemaGenerationConnection The JDBC connection that should be used for schema generation.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition of which is the JDBC connection that should be used for schema generation.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withSchemaGenerationConnection(@NonNull String schemaGenerationConnection) throws IllegalArgumentException;

    /**
     * Tells if database schema script should or not be created.
     * @return {@link OptionalBoolean#TRUE} if a database schema script should be created, {@link OptionalBoolean#FALSE} if not
     *     and {@link OptionalBoolean#UNSPECIFIED} if no setting is defined.
     */
    public OptionalBoolean getCreateDatabaseSchemas();

    /**
     * Defines if a database schema script should or not be created.
     * @param createDatabaseSchemas {@link OptionalBoolean#TRUE} if a database schema script should be created,
     *     {@link OptionalBoolean#FALSE} if not and {@link OptionalBoolean#UNSPECIFIED} if the setting should not be defined.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition of whether a database schema script should or not be created.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withCreateDatabaseSchemas(@NonNull OptionalBoolean createDatabaseSchemas) throws IllegalArgumentException;

    /**
     * Defines if a database schema script should or not be created.
     * @param newValue {@code true} if a database schema script should be created, {@code false} if not.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition of whether a database schema script should or not be created.
     */
    public default E withCreateDatabaseSchemas(boolean newValue) {
        return withCreateDatabaseSchemas(OptionalBoolean.from(newValue));
    }

    /**
     * Retrieves the database brand or vendor name.
     * @return The database brand or vendor name.
     */
    public String getDatabaseProductName();

    /**
     * Defines the database brand or vendor name.
     * @param databaseProductName The database brand or vendor name.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition of the database brand or vendor name.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withDatabaseProductName(@NonNull String databaseProductName) throws IllegalArgumentException;

    /**
     * Retrieves the database major version number.
     * @return The database major version number.
     */
    public String getDatabaseMajorVersion();

    /**
     * Defines the database major version number.
     * @param databaseMajorVersion The database major version number.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition of major version number.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withDatabaseMajorVersion(@NonNull String databaseMajorVersion) throws IllegalArgumentException;

    /**
     * Retrieves the database minor version number.
     * @return The database minor version number.
     */
    public String getDatabaseMinorVersion();

    /**
     * Defines the database minor version number.
     * @param databaseMinorVersion The database minor version number.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition of minor version number.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withDatabaseMinorVersion(@NonNull String databaseMinorVersion) throws IllegalArgumentException;

    /**
     * Retrieves the set of extra custom properties.
     * @implSpec The returned map should be immutable. Those properties have priority over the standard ones and might override them.
     * @return The set of extra custom properties.
     */
    public Map<String, String> getExtras();

    /**
     * Retrieves the value of some extra custom property, if it exists.
     * @param key The extra custom property to be retrieved.
     * @return An {@link Optional} containing the value of some extra custom property, if it exists,
     *     or an empty {@link Optional} if it doesn't.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public default Optional<String> getExtra(@NonNull String key) throws IllegalArgumentException {
        return Optional.ofNullable(getExtras().get(key));
    }

    /**
     * Replaces all the extra custom properties.
     * @param extras A mapping containing all the new extra custom properties.
     * @return A new instance of this class which is similar to {@code this}, but with the new given custom extra properties.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     * @implSpec Further modifications to the given map should not be reflected into the produced instance.
     */
    public E withExtras(@NonNull Map<String, String> extras) throws IllegalArgumentException;

    /**
     * Adds or replaces a custom extra property.
     * @param key The extra custom property to be added.
     * @param value The extra custom property value to be added.
     * @return A new instance of this class which is similar to {@code this}, but with a new definition of the given custom extra property.
     * @throws IllegalArgumentException If either parameter is {@code null}.
     */
    public default E putExtra(@NonNull String key, @NonNull String value) throws IllegalArgumentException {
        var m = new HashMap<>(getExtras());
        m.put(key, value);
        return withExtras(m);
    }

    /**
     * Removes an extra custom property.
     * @param key The extra custom property to be removed.
     * @return A new instance of this class which is similar to {@code this}, but without the given custom extra property.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public default E removeExtra(@NonNull String key) throws IllegalArgumentException {
        var m = new HashMap<>(getExtras());
        m.remove(key);
        return withExtras(m);
    }

    /**
     * Removes all the extra custom properties.
     * @return A new instance of this class which is similar to {@code this}, but without the extra custom properties.
     */
    public default E clearExtras() {
        return withExtras(Map.of());
    }

    /**
     * Creates an immutable map containing all the standard properties known by this {@code StandardConnectorFactory}
     * which have a value.
     * <p>Extra custom properties are not presented yet into this map. Those will be added later when the
     * {@link #getProperties()} method is called and might override the standard ones.</p>
     * @return The set of properties standard properties.
     * @implSpec Subinterfaces and implementers should override this method and add provider-specific properties
     *     by calling {@code super.getStandardProperties()} and adding into it such properties before returning it.
     */
    public default Map<String, String> getStandardProperties() {
        var props = new HashMap<String, String>(64);
        BiConsumer<String, String> f = (key, value) -> {
            if (!value.isEmpty()) props.put(key, value);
        };
        var d = getDriver();
        if (d != Driver.class) f.accept(PersistenceConfiguration.JDBC_DRIVER, d.getName());

        f.accept(PersistenceConfiguration.JDBC_URL, getUrl());
        f.accept(PersistenceConfiguration.JDBC_USER, getUser());
        f.accept(PersistenceConfiguration.JDBC_PASSWORD, getPassword());
        f.accept("jakarta.persistence.database-product-name", getDatabaseProductName());
        f.accept("jakarta.persistence.database-major-version", getDatabaseMajorVersion());
        f.accept("jakarta.persistence.database-minor-version", getDatabaseMinorVersion());
        f.accept("jakarta.persistence.sql-load-script-source", getLoadScript());
        f.accept("jakarta.persistence.schema-generation.connection", getSchemaGenerationConnection());

        //JDBC_DATASOURCE
        //LOCK_TIMEOUT
        //QUERY_TIMEOUT
        //VALIDATION_FACTORY
        //VALIDATION_GROUP_PRE_PERSIST
        //VALIDATION_GROUP_PRE_UPDATE
        //VALIDATION_GROUP_PRE_REMOVE
        //CACHE_MODE
        var ga = getSchemaGenerationAction();
        f.accept(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION, ga.getCode());

        var gc = getSchemaGenerationCreate();
        f.accept(PersistenceConfiguration.SCHEMAGEN_CREATE_SOURCE, gc.getStrategy());
        f.accept(PersistenceConfiguration.SCHEMAGEN_CREATE_SCRIPT_SOURCE, gc.getScriptPath());

        var gd = getSchemaGenerationDrop();
        f.accept(PersistenceConfiguration.SCHEMAGEN_DROP_SOURCE, gd.getStrategy());
        f.accept(PersistenceConfiguration.SCHEMAGEN_DROP_SCRIPT_SOURCE, gd.getScriptPath());

        var sssl = getSchemaScriptStoreLocation();
        f.accept(PersistenceConfiguration.SCHEMAGEN_SCRIPTS_ACTION, sssl.getStrategy());
        f.accept(PersistenceConfiguration.SCHEMAGEN_CREATE_TARGET, sssl.getCreateScript());
        f.accept(PersistenceConfiguration.SCHEMAGEN_DROP_TARGET, sssl.getDropScript());

        var cds = getCreateDatabaseSchemas();
        f.accept("jakarta.persistence.schema-generation.create-database-schemas", cds.getCode());

        return Map.copyOf(props);
    }

    /**
     * Creates an immutable map containing all the properties defined in this {@code StandardConnectorFactory}
     * which have a value.
     * @implSpec Subinterfaces and implementers generally should not override this method, or have little reason for doing so.
     *     Instead, it is preferable to override the {@link #getStandardProperties()}.
     *     If this method is overriden, a call to {@code super.getProperties()} should probably be performed.
     * @return The set of all defined properties, with custom extra properties possibly overriding the standard ones.
     */
    public default Map<String, String> getProperties() {
        var props = new HashMap<>(getStandardProperties());
        props.putAll(getExtras());
        return Map.copyOf(props);
    }
}
