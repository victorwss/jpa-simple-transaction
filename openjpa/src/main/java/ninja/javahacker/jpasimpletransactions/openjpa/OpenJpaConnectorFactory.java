package ninja.javahacker.jpasimpletransactions.openjpa;

import java.sql.Driver;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Tolerate;
import ninja.javahacker.jpasimpletransactions.config.ProviderConnectorFactory;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationAction;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationActionTarget;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationSource;
import ninja.javahacker.jpasimpletransactions.config.TriBoolean;

/**
 * Implementation of {@link ProviderConnectorFactory} for Open JPA.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenJpaConnectorFactory implements ProviderConnectorFactory<OpenJpaConnectorFactory> {

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
    @NonNull Set<Class<?>> entities;

    @NonNull TriBoolean dynamicEnhancementAgent;
    @NonNull Support runtimeUnenhancedClasses;

    public OpenJpaConnectorFactory() {
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
        this.entities = Set.of();

        this.dynamicEnhancementAgent = TriBoolean.UNSPECIFIED;
        this.runtimeUnenhancedClasses = Support.UNSPECIFIED;
    }

    @Override
    public OpenJpaAdapter getProviderAdapter() {
        return OpenJpaAdapter.CANONICAL;
    }

    @Tolerate
    public OpenJpaConnectorFactory withDynamicEnhancementAgent(boolean newValue) {
        return withDynamicEnhancementAgent(TriBoolean.from(newValue));
    }

    @Override
    public Map<String, String> getProperties() {
        var props = ProviderConnectorFactory.super.getProperties();
        getDynamicEnhancementAgent().work("openjpa.DynamicEnhancementAgent", props::put);
        getRuntimeUnenhancedClasses().work("openjpa.RuntimeUnenhancedClasses", props::put);
        return Map.copyOf(props);
    }
}
