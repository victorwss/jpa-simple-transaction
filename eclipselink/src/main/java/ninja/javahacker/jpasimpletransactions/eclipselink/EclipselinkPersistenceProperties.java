package ninja.javahacker.jpasimpletransactions.eclipselink;

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
public class EclipselinkPersistenceProperties implements PersistenceProperties {

    @Delegate(excludes = Dont.class)
    @NonNull
    final DefaultPersistenceProperties defaultProperties;

    public EclipselinkPersistenceProperties(@NonNull String persistenceUnitName) {
        this.defaultProperties = new DefaultPersistenceProperties(new EclipselinkAdapter(), persistenceUnitName);
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

        public EclipselinkPersistenceProperties withCreateDatabaseSchemas(boolean newValue);

        public EclipselinkPersistenceProperties withCreateDatabaseSchemas(TriBoolean newValue);

        public EclipselinkPersistenceProperties withCreateDatabaseMajorVersion(String newValue);

        public EclipselinkPersistenceProperties withCreateDatabaseMinorVersion(String newValue);

        public EclipselinkPersistenceProperties withDatabaseProductName(String newValue);

        public EclipselinkPersistenceProperties withDriver(String driverName) throws ClassNotFoundException;

        public EclipselinkPersistenceProperties withDriver(Class<? extends Driver> driver) throws ClassNotFoundException;

        public EclipselinkPersistenceProperties withExtras(Map<String, String> values) throws ClassNotFoundException;

        public EclipselinkPersistenceProperties withLoadScript(String loadScript) throws ClassNotFoundException;

        public EclipselinkPersistenceProperties withPassword(String password) throws ClassNotFoundException;

        public EclipselinkPersistenceProperties withPersistenceUnitName(String persistenceUnitName);

        public EclipselinkPersistenceProperties withSchemaGenerationAction(SchemaGenerationAction action);

        public EclipselinkPersistenceProperties withSchemaGenerationConnection(String connection);

        public EclipselinkPersistenceProperties withSchemaGenerationCreate(SchemaGenerationSource source);

        public EclipselinkPersistenceProperties withSchemaGenerationDrop(SchemaGenerationSource source);

        public EclipselinkPersistenceProperties withSchemaGenerationScriptsCreate(SchemaGenerationActionTarget source);

        public EclipselinkPersistenceProperties withUrl(String url);

        public EclipselinkPersistenceProperties withUser(String user);
    }

    public EclipselinkPersistenceProperties withCreateDatabaseSchemas(boolean newValue) {
        defaultProperties.withCreateDatabaseSchemas(newValue);
        return this;
    }

    public EclipselinkPersistenceProperties withCreateDatabaseSchemas(TriBoolean newValue) {
        defaultProperties.withCreateDatabaseSchemas(newValue);
        return this;
    }

    public EclipselinkPersistenceProperties withCreateDatabaseMajorVersion(String newValue) {
        defaultProperties.withDatabaseMajorVersion(newValue);
        return this;
    }

    public EclipselinkPersistenceProperties withCreateDatabaseMinorVersion(String newValue) {
        defaultProperties.withDatabaseMinorVersion(newValue);
        return this;
    }

    public EclipselinkPersistenceProperties withDatabaseProductName(String newValue) {
        defaultProperties.withDatabaseProductName(newValue);
        return this;
    }

    public EclipselinkPersistenceProperties withDriver(String driverName) throws ClassNotFoundException {
        defaultProperties.withDriver(driverName);
        return this;
    }

    public EclipselinkPersistenceProperties withDriver(Class<? extends Driver> driver) throws ClassNotFoundException {
        defaultProperties.withDriver(driver);
        return this;
    }

    public EclipselinkPersistenceProperties withExtras(Map<String, String> values) throws ClassNotFoundException {
        defaultProperties.withExtras(values);
        return this;
    }

    public EclipselinkPersistenceProperties withLoadScript(String loadScript) throws ClassNotFoundException {
        defaultProperties.withLoadScript(loadScript);
        return this;
    }

    public EclipselinkPersistenceProperties withPassword(String password) throws ClassNotFoundException {
        defaultProperties.withPassword(password);
        return this;
    }

    public EclipselinkPersistenceProperties withPersistenceUnitName(String persistenceUnitName) {
        defaultProperties.withPersistenceUnitName(persistenceUnitName);
        return this;
    }

    public EclipselinkPersistenceProperties withSchemaGenerationAction(SchemaGenerationAction action) {
        defaultProperties.withSchemaGenerationAction(action);
        return this;
    }

    public EclipselinkPersistenceProperties withSchemaGenerationConnection(String connection) {
        defaultProperties.withSchemaGenerationConnection(connection);
        return this;
    }

    public EclipselinkPersistenceProperties withSchemaGenerationCreate(SchemaGenerationSource source) {
        defaultProperties.withSchemaGenerationCreate(source);
        return this;
    }

    public EclipselinkPersistenceProperties withSchemaGenerationDrop(SchemaGenerationSource source) {
        defaultProperties.withSchemaGenerationDrop(source);
        return this;
    }

    public EclipselinkPersistenceProperties withSchemaGenerationScriptsCreate(SchemaGenerationActionTarget source) {
        defaultProperties.withSchemaGenerationScriptsCreate(source);
        return this;
    }

    public EclipselinkPersistenceProperties withUrl(String url) {
        defaultProperties.withUrl(url);
        return this;
    }

    public EclipselinkPersistenceProperties withUser(String user) {
        defaultProperties.withUser(user);
        return this;
    }
}
