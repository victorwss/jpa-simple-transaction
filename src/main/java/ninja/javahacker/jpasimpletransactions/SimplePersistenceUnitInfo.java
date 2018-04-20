package ninja.javahacker.jpasimpletransactions;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public final class SimplePersistenceUnitInfo implements PersistenceUnitInfo {

    public static final Class<? extends PersistenceProvider> HIBERNATE_PROVIDER_CLASS;

    static {
        try {
            HIBERNATE_PROVIDER_CLASS = Class
                    .forName("org.hibernate.jpa.HibernatePersistenceProvider")
                    .asSubclass(PersistenceProvider.class);
        } catch (ClassNotFoundException x) {
            throw new ExceptionInInitializerError(x);
        }
    }

    private final String persistenceUnitName;
    private final List<String> classes;
    private final Properties properties;

    public SimplePersistenceUnitInfo(
            @NonNull String persistenceUnitName,
            @NonNull Collection<Class<?>> classes,
            @NonNull Map<String, String> properties)
    {
        this.persistenceUnitName = persistenceUnitName;
        this.classes = classes.stream().map(Class::getName).collect(Collectors.toList());
        this.properties = new Properties();
        this.properties.putAll(properties);
    }

    public static PersistenceProvider makeProvider() {
        try {
            return HIBERNATE_PROVIDER_CLASS.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException x) {
            throw new Error(x);
        } catch (InvocationTargetException x) {
            throw new RuntimeException(x.getCause());
        }
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return HIBERNATE_PROVIDER_CLASS.getName();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    @Override
    public DataSource getJtaDataSource() {
        return null;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return null;
    }

    @Override
    public List<String> getMappingFileNames() {
        return Collections.emptyList();
    }

    @Override
    public List<URL> getJarFileUrls() {
        try {
            return Collections.list(this.getClass().getClassLoader().getResources(""));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return null;
    }

    @Override
    public List<String> getManagedClassNames() {
        return Collections.unmodifiableList(classes);
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return false;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return SharedCacheMode.UNSPECIFIED;
    }

    @Override
    public ValidationMode getValidationMode() {
        return ValidationMode.AUTO;
    }

    @Override
    public Properties getProperties() {
        return (Properties) properties.clone();
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return "2.2";
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return null;
    }
}
