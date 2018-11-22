package ninja.javahacker.jpasimpletransactions.openjpa;

import java.sql.Driver;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
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
public class OpenJpaPersistenceProperties implements PersistenceProperties {

    @Delegate(excludes = Dont.class)
    @NonNull
    final DefaultPersistenceProperties defaultProperties;

    public OpenJpaPersistenceProperties(@NonNull String persistenceUnitName) {
        this.defaultProperties = new DefaultPersistenceProperties(new OpenJpaAdapter(), persistenceUnitName);
    }

    @Override
    public Map<String, String> build() {
        return defaultProperties.buildCore();
    }

    /*private void work(@NonNull String key, @NonNull String value, @NonNull BiConsumer<String, String> acceptor) {
        if (!value.isEmpty()) acceptor.accept(key, value);
    }*/

    /**
     * Methods already implemented below that returns this, so shouldn't delegate to those.
     */
    private static interface Dont {

        public OpenJpaPersistenceProperties withCreateDatabaseSchemas(boolean newValue);

        public OpenJpaPersistenceProperties withCreateDatabaseSchemas(TriBoolean newValue);

        public OpenJpaPersistenceProperties withCreateDatabaseMajorVersion(String newValue);

        public OpenJpaPersistenceProperties withCreateDatabaseMinorVersion(String newValue);

        public OpenJpaPersistenceProperties withDatabaseProductName(String newValue);

        public OpenJpaPersistenceProperties withDriver(String driverName) throws ClassNotFoundException;

        public OpenJpaPersistenceProperties withDriver(Class<? extends Driver> driver) throws ClassNotFoundException;

        public OpenJpaPersistenceProperties withExtras(Map<String, String> values) throws ClassNotFoundException;

        public OpenJpaPersistenceProperties withLoadScript(String loadScript) throws ClassNotFoundException;

        public OpenJpaPersistenceProperties withPassword(String password) throws ClassNotFoundException;

        public OpenJpaPersistenceProperties withPersistenceUnitName(String persistenceUnitName);

        public OpenJpaPersistenceProperties withSchemaGenerationAction(SchemaGenerationAction action);

        public OpenJpaPersistenceProperties withSchemaGenerationConnection(String connection);

        public OpenJpaPersistenceProperties withSchemaGenerationCreate(SchemaGenerationSource source);

        public OpenJpaPersistenceProperties withSchemaGenerationDrop(SchemaGenerationSource source);

        public OpenJpaPersistenceProperties withSchemaGenerationScriptsCreate(SchemaGenerationActionTarget source);

        public OpenJpaPersistenceProperties withUrl(String url);

        public OpenJpaPersistenceProperties withUser(String user);
    }

    public OpenJpaPersistenceProperties withCreateDatabaseSchemas(boolean newValue) {
        defaultProperties.withCreateDatabaseSchemas(newValue);
        return this;
    }

    public OpenJpaPersistenceProperties withCreateDatabaseSchemas(TriBoolean newValue) {
        defaultProperties.withCreateDatabaseSchemas(newValue);
        return this;
    }

    public OpenJpaPersistenceProperties withCreateDatabaseMajorVersion(String newValue) {
        defaultProperties.withDatabaseMajorVersion(newValue);
        return this;
    }

    public OpenJpaPersistenceProperties withCreateDatabaseMinorVersion(String newValue) {
        defaultProperties.withDatabaseMinorVersion(newValue);
        return this;
    }

    public OpenJpaPersistenceProperties withDatabaseProductName(String newValue) {
        defaultProperties.withDatabaseProductName(newValue);
        return this;
    }

    public OpenJpaPersistenceProperties withDriver(String driverName) throws ClassNotFoundException {
        defaultProperties.withDriver(driverName);
        return this;
    }

    public OpenJpaPersistenceProperties withDriver(Class<? extends Driver> driver) throws ClassNotFoundException {
        defaultProperties.withDriver(driver);
        return this;
    }

    public OpenJpaPersistenceProperties withExtras(Map<String, String> values) throws ClassNotFoundException {
        defaultProperties.withExtras(values);
        return this;
    }

    public OpenJpaPersistenceProperties withLoadScript(String loadScript) throws ClassNotFoundException {
        defaultProperties.withLoadScript(loadScript);
        return this;
    }

    public OpenJpaPersistenceProperties withPassword(String password) throws ClassNotFoundException {
        defaultProperties.withPassword(password);
        return this;
    }

    public OpenJpaPersistenceProperties withPersistenceUnitName(String persistenceUnitName) {
        defaultProperties.withPersistenceUnitName(persistenceUnitName);
        return this;
    }

    public OpenJpaPersistenceProperties withSchemaGenerationAction(SchemaGenerationAction action) {
        defaultProperties.withSchemaGenerationAction(action);
        return this;
    }

    public OpenJpaPersistenceProperties withSchemaGenerationConnection(String connection) {
        defaultProperties.withSchemaGenerationConnection(connection);
        return this;
    }

    public OpenJpaPersistenceProperties withSchemaGenerationCreate(SchemaGenerationSource source) {
        defaultProperties.withSchemaGenerationCreate(source);
        return this;
    }

    public OpenJpaPersistenceProperties withSchemaGenerationDrop(SchemaGenerationSource source) {
        defaultProperties.withSchemaGenerationDrop(source);
        return this;
    }

    public OpenJpaPersistenceProperties withSchemaGenerationScriptsCreate(SchemaGenerationActionTarget source) {
        defaultProperties.withSchemaGenerationScriptsCreate(source);
        return this;
    }

    public OpenJpaPersistenceProperties withUrl(String url) {
        defaultProperties.withUrl(url);
        return this;
    }

    public OpenJpaPersistenceProperties withUser(String user) {
        defaultProperties.withUser(user);
        return this;
    }
}
