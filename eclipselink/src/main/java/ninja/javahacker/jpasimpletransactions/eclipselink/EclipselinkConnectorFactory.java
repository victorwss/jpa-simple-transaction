package ninja.javahacker.jpasimpletransactions.eclipselink;

import java.net.URL;
import java.sql.Driver;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.experimental.FieldDefaults;
import ninja.javahacker.jpasimpletransactions.config.OptionalBoolean;
import ninja.javahacker.jpasimpletransactions.config.ProviderConnectorFactory;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationAction;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationActionTarget;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationSource;

/**
 * Implementation of {@link ProviderConnectorFactory} for Eclipselink.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("checkstyle:javadoctagcontinuationindentation")
public class EclipselinkConnectorFactory implements ProviderConnectorFactory<EclipselinkConnectorFactory> {

    /**
     * The persistence unit's name.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param persistenceUnitName {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String persistenceUnitName;

    /**
     * The database's {@link Driver}.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param driver {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull Class<? extends Driver> driver;

    /**
     * The database's URL for connection.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param url {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String url;

    /**
     * The database's user for connection.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param user {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String user;

    /**
     * The database's password for connection.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param password {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String password;

    /**
     * The strategy used for automatic schema generation or validation.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param schemaGenerationAction {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull SchemaGenerationAction schemaGenerationAction;

    /**
     * The strategy used for executing custom scripts on creating database artifacts.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param schemaGenerationCreate {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull SchemaGenerationSource schemaGenerationCreate;

    /**
     * The strategy used for executing custom scripts on dropping database artifacts.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param schemaGenerationDrop {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull SchemaGenerationSource schemaGenerationDrop;

    /**
     * Which and where should scripts for table creation and droppings be stored.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param schemaScriptStoreLocation {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull SchemaGenerationActionTarget schemaScriptStoreLocation;

    /**
     * Where the script for table initialization is stored, if it exists.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param loadScript {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String loadScript;

    /**
     * The JDBC connection that should be used for schema generation. This is intended mainly for Java EE / Jakarta EE environments.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param schemaGenerationConnection {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String schemaGenerationConnection;

    /**
     * If a database schema script should or not be created.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param createDatabaseSchemas {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull OptionalBoolean createDatabaseSchemas;

    /**
     * The database brand or vendor name.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param databaseProductName {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String databaseProductName;

    /**
     * The database major version number.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param databaseMajorVersion {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String databaseMajorVersion;

    /**
     * The database minor version number.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param databaseMinorVersion {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull String databaseMinorVersion;

    /**
     * The set of extra custom properties.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param extras {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull Map<String, String> extras;

    /**
     * The set of explicitly declared entity classes that should be recognized as entity types.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param entities {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull Set<Class<?>> entities;

    /**
     * Sole public constructor. Creates an empty instance.
     * To be something useful, the instance should be built by further call to {@code withXXX} methods.
     */
    public EclipselinkConnectorFactory() {
        this.persistenceUnitName = "";
        this.driver = Driver.class;
        this.url = "";
        this.user = "";
        this.password = "";
        this.schemaGenerationAction = SchemaGenerationAction.UNSPECIFIED;
        this.schemaGenerationCreate = SchemaGenerationSource.unspecified();
        this.schemaGenerationDrop = schemaGenerationCreate;
        this.schemaScriptStoreLocation = SchemaGenerationActionTarget.unspecified();
        this.loadScript = "";
        this.schemaGenerationConnection = "";
        this.createDatabaseSchemas = OptionalBoolean.UNSPECIFIED;
        this.databaseProductName = "";
        this.databaseMajorVersion = "";
        this.databaseMinorVersion = "";
        this.extras = Map.of();
        this.entities = Set.of();
    }

    /**
     * {@inheritDoc}
     * @implNote This implementation always returns {@link #NOWHERE}.
     * @return {@inheritDoc}
     */
    @Override
    public Optional<URL> getPersistenceUnitUrl() {
        return Optional.of(NOWHERE);
    }

    /**
     * {@inheritDoc}
     * @implNote This returns {@link EclipselinkAdapter#CANONICAL}.
     * @return {@inheritDoc}
     */
    @Override
    public EclipselinkAdapter getProviderAdapter() {
        return EclipselinkAdapter.CANONICAL;
    }
}
