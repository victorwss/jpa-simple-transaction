package ninja.javahacker.jpasimpletransactions.openjpa;

import java.lang.annotation.Annotation;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Tolerate;
import ninja.javahacker.jpasimpletransactions.SimpleScope;
import ninja.javahacker.jpasimpletransactions.config.OptionalBoolean;
import ninja.javahacker.jpasimpletransactions.config.ProviderConnectorFactory;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationAction;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationActionTarget;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationSource;

/**
 * Implementation of {@link ProviderConnectorFactory} for Open JPA.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"checkstyle:javadoctagcontinuationindentation", "checkstyle:atclauseorder"})
public class OpenJpaConnectorFactory implements ProviderConnectorFactory<OpenJpaConnectorFactory> {

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
     * The explicitly declared scoped annotation.
     * -- GETTER --
     * {@inheritDoc}
     * @return {@inheritDoc}
     * -- WITH --
     * {@inheritDoc}
     * @param scopedAnnotation {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull Class<? extends Annotation> scopedAnnotation;

    /**
     * Defines if a dynamic enhancement agent should be used at runtime.
     * -- GETTER --
     * Tells if a dynamic enhancement agent should be used at runtime.
     * @return If a dynamic enhancement agent should be used at runtime.
     * -- WITH --
     * Defines if a dynamic enhancement agent should be used at runtime.
     * @param dynamicEnhancementAgent The definition about if a dynamic enhancement agent should be used at runtime.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition about whether a dynamic enhancement agent should be used at runtime.
     * @throws IllegalArgumentException If {@code dynamicEnhancementAgent} is {@code null}.
     */
    @NonNull OptionalBoolean dynamicEnhancementAgent;

    /**
     * The strategy used to handle unenhanced classes at runtime.
     * -- GETTER --
     * The strategy used to handle unenhanced classes at runtime.
     * @return The strategy used to handle unenhanced classes at runtime.
     * -- WITH --
     * Sets the strategy used to handle unenhanced classes at runtime.
     * @param runtimeUnenhancedClasses The strategy used to handle unenhanced classes at runtime.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     strategy used to handle unenhanced classes at runtime.
     * @throws IllegalArgumentException If {@code runtimeUnenhancedClasses} is {@code null}.
     */
    @NonNull Support runtimeUnenhancedClasses;

    /**
     * If OpenJPA's data cache should be used.
     * -- GETTER --
     * If OpenJPA's data cache should be used.
     * @return If OpenJPA's data cache should be used.
     * -- WITH --
     * Sets if OpenJPA's data cache should be used.
     * @param dataCache if OpenJPA's data cache should be used.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     setting about whether OpenJPA's data cache should be used.
     * @throws IllegalArgumentException If {@code dataCache} is {@code null}.
     */
    @NonNull OptionalBoolean dataCache;

    /**
     * If OpenJPA's query cache should be used.
     * -- GETTER --
     * If OpenJPA's query cache should be used.
     * @return If OpenJPA's query cache should be used.
     * -- WITH --
     * Sets if OpenJPA's query cache should be used.
     * @param queryCache if OpenJPA's query cache should be used.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     setting about whether OpenJPA's query cache should be used.
     * @throws IllegalArgumentException If {@code queryCache} is {@code null}.
     */
    @NonNull OptionalBoolean queryCache;

    /**
     * Sole public constructor. Creates an empty instance.
     * To be something useful, the instance should be built by further call to {@code withXXX} methods.
     */
    public OpenJpaConnectorFactory() {
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
        this.scopedAnnotation = SimpleScope.class;

        this.dynamicEnhancementAgent = OptionalBoolean.UNSPECIFIED;
        this.runtimeUnenhancedClasses = Support.UNSUPPORTED;
        this.dataCache = OptionalBoolean.UNSPECIFIED;
        this.queryCache = OptionalBoolean.UNSPECIFIED;
    }

    /**
     * {@inheritDoc}
     * @implNote This returns {@link OpenJpaAdapter#CANONICAL}.
     * @return {@inheritDoc}
     */
    @Override
    public OpenJpaAdapter getProviderAdapter() {
        return OpenJpaAdapter.CANONICAL;
    }

    /**
     * Defines if a dynamic enhancement agent should be used at runtime.
     * @param newValue The definition about if a dynamic enhancement agent should be used at runtime.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition about whether a dynamic enhancement agent should be used at runtime.
     */
    @Tolerate
    public OpenJpaConnectorFactory withDynamicEnhancementAgent(boolean newValue) {
        return withDynamicEnhancementAgent(OptionalBoolean.from(newValue));
    }

    /**
     * Defines OpenJPA's data cache should be used.
     * @param newValue The definition about if OpenJPA's data cache should be used.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition about whether OpenJPA's data cache should be used.
     */
    @Tolerate
    public OpenJpaConnectorFactory withDataCache(boolean newValue) {
        return withDataCache(OptionalBoolean.from(newValue));
    }

    /**
     * Defines OpenJPA's query cache should be used.
     * @param newValue The definition about if OpenJPA's query cache should be used.
     * @return A new instance of this class which is similar to {@code this}, but with the given
     *     definition about whether OpenJPA's query cache should be used.
     */
    @Tolerate
    public OpenJpaConnectorFactory withQueryCache(boolean newValue) {
        return withQueryCache(OptionalBoolean.from(newValue));
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Map<String, String> getStandardProperties() {
        var props = new HashMap<>(ProviderConnectorFactory.super.getStandardProperties());
        BiConsumer<String, String> f = (key, value) -> {
            if (!value.isEmpty()) props.put(key, value);
        };

        f.accept("openjpa.DynamicEnhancementAgent", getDynamicEnhancementAgent().getCode());
        f.accept("openjpa.RuntimeUnenhancedClasses", getRuntimeUnenhancedClasses().getCode());
        f.accept("openjpa.DataCache", getDataCache().getCode());
        f.accept("openjpa.QueryCache", getQueryCache().getCode());
        return Map.copyOf(props);
    }
}
