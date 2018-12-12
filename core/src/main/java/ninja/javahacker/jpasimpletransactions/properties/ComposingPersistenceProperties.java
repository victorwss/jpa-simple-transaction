package ninja.javahacker.jpasimpletransactions.properties;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Driver;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
@AllArgsConstructor
@SuppressFBWarnings("PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS")
public class ComposingPersistenceProperties<E> implements PersistenceProperties {

    private final DefaultPersistenceProperties properties;

    private final Function<DefaultPersistenceProperties, E> out;

    public static interface Build {
        public Object build();
    }

    public ComposingPersistenceProperties(
            @NonNull ProviderAdapter provider,
            @NonNull Function<? super ComposingPersistenceProperties<E>, E> deliver)
    {
        this.properties = new DefaultPersistenceProperties(provider);
        this.out = new Transformer<>(deliver);
    }

    protected ComposingPersistenceProperties() {
        throw new AssertionError();
    }

    private static class Transformer<E> implements Function<DefaultPersistenceProperties, E> {

        private final Function<? super ComposingPersistenceProperties<E>, E> deliver;

        public Transformer(@NonNull Function<? super ComposingPersistenceProperties<E>, E> deliver) {
            this.deliver = deliver;
        }

        @Override
        public E apply(DefaultPersistenceProperties t) {
            return deliver.apply(new ComposingPersistenceProperties<>(t, this));
        }
    }

    @Override
    public ProviderAdapter getProviderAdapter() {
        return properties.getProviderAdapter();
    }

    @Override
    public String getPersistenceUnitName() {
        return properties.getPersistenceUnitName();
    }

    public E withPersistenceUnitName(@NonNull String persistenceUnitName) {
        return out.apply(properties.withPersistenceUnitName(persistenceUnitName));
    }

    public Class<? extends Driver> getDriver() {
        return properties.getDriver();
    }

    public E withDriver(@NonNull String driverName) throws ClassNotFoundException {
        return withDriver(Class.forName(driverName).asSubclass(Driver.class));
    }

    public E withDriver(@NonNull Class<? extends Driver> driver) {
        return out.apply(properties.withDriver(driver));
    }

    public String getUrl() {
        return properties.getUrl();
    }

    public E withUrl(@NonNull String url) {
        return out.apply(properties.withUrl(url));
    }

    public String getUser() {
        return properties.getUser();
    }

    public E withUser(@NonNull String user) {
        return out.apply(properties.withUser(user));
    }

    public String getPassword() {
        return properties.getPassword();
    }

    public E withPassword(@NonNull String password) {
        return out.apply(properties.withPassword(password));
    }

    public SchemaGenerationAction getSchemaGenerationAction() {
        return properties.getSchemaGenerationAction();
    }

    public E withSchemaGenerationAction(@NonNull SchemaGenerationAction schemaGenerationAction) {
        return out.apply(properties.withSchemaGenerationAction(schemaGenerationAction));
    }

    public SchemaGenerationSource getSchemaGenerationCreate() {
        return properties.getSchemaGenerationCreate();
    }

    public E withSchemaGenerationCreate(@NonNull SchemaGenerationSource schemaGenerationCreate) {
        return out.apply(properties.withSchemaGenerationCreate(schemaGenerationCreate));
    }

    public SchemaGenerationSource getSchemaGenerationDrop() {
        return properties.getSchemaGenerationDrop();
    }

    public E withSchemaGenerationDrop(@NonNull SchemaGenerationSource schemaGenerationDrop) {
        return out.apply(properties.withSchemaGenerationDrop(schemaGenerationDrop));
    }

    public SchemaGenerationActionTarget getSchemaGenerationScriptsCreate() {
        return properties.getSchemaGenerationScriptsCreate();
    }

    public E withSchemaGenerationScriptsCreate(@NonNull SchemaGenerationActionTarget schemaGenerationScriptsCreate) {
        return out.apply(properties.withSchemaGenerationScriptsCreate(schemaGenerationScriptsCreate));
    }

    public String getLoadScript() {
        return properties.getLoadScript();
    }

    public E withLoadScript(@NonNull String loadScript) {
        return out.apply(properties.withLoadScript(loadScript));
    }

    public String getSchemaGenerationConnection() {
        return properties.getSchemaGenerationConnection();
    }

    public E withSchemaGenerationConnection(@NonNull String schemaGenerationConnection) {
        return out.apply(properties.withSchemaGenerationConnection(schemaGenerationConnection));
    }

    public TriBoolean getCreateDatabaseSchemas() {
        return properties.getCreateDatabaseSchemas();
    }

    public E withCreateDatabaseSchemas(@NonNull TriBoolean createDatabaseSchemas) {
        return out.apply(properties.withCreateDatabaseSchemas(createDatabaseSchemas));
    }

    public E withCreateDatabaseSchemas(boolean newValue) {
        return withCreateDatabaseSchemas(TriBoolean.from(newValue));
    }

    public E withDatabaseProductName(@NonNull String databaseProductName) {
        return out.apply(properties.withDatabaseProductName(databaseProductName));
    }

    public String getDatabaseMajorVersion() {
        return properties.getDatabaseMajorVersion();
    }

    public E withDatabaseMajorVersion(@NonNull String databaseMajorVersion) {
        return out.apply(properties.withDatabaseMajorVersion(databaseMajorVersion));
    }

    public String getDatabaseMinorVersion() {
        return properties.getDatabaseMinorVersion();
    }

    public E withDatabaseMinorVersion(@NonNull String databaseMinorVersion) {
        return out.apply(properties.withDatabaseMinorVersion(databaseMinorVersion));
    }

    public Map<String, String> getExtras() {
        return properties.getExtras();
    }

    public E withExtras(@NonNull Map<String, String> extras) {
        return out.apply(properties.withExtras(extras));
    }

    public E putExtra(String key, String value) {
        return out.apply(properties.putExtra(key, value));
    }

    public E removeExtra(String key) {
        return out.apply(properties.removeExtra(key));
    }

    public E clearExtras() {
        return out.apply(properties.clearExtras());
    }

    @Override
    public Map<String, String> build() {
        return properties.build();
    }

    public static void work(@NonNull String key, @NonNull String value, @NonNull BiConsumer<String, String> acceptor) {
        if (!value.isEmpty()) acceptor.accept(key, value);
    }
}
