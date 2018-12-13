package ninja.javahacker.jpasimpletransactions.eclipselink;

import java.sql.Driver;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
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
public class EclipselinkPersistenceProperties implements StandardPersistenceProperties<EclipselinkPersistenceProperties> {

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

    public EclipselinkPersistenceProperties() {
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
    }

    @Override
    public EclipselinkAdapter getProviderAdapter() {
        return EclipselinkAdapter.CANONICAL;
    }
}
