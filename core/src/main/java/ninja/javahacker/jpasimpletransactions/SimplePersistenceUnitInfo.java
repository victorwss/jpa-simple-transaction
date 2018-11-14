package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.PackagePrivate;

/**
 * A simple minimalist implementation of the {@link PersistenceUnitInfo} interface.
 * @author Victor Williams Stafusa da Silva
 */
@PackagePrivate
@ToString
final class SimplePersistenceUnitInfo implements PersistenceUnitInfo {

    // TODO:Coupled to Hibernate.
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

    private final ProviderAdapter adapter;
    private final String persistenceUnitName;
    private final List<String> classes;
    private final Map<String, String> properties;

    public SimplePersistenceUnitInfo(
            @NonNull ProviderAdapter adapter,
            @NonNull String persistenceUnitName,
            @NonNull Collection<Class<?>> classes,
            @NonNull Map<String, String> properties)
    {
        this.adapter = adapter;
        this.persistenceUnitName = persistenceUnitName;
        this.classes = classes.stream().map(Class::getName).collect(Collectors.toList());
        this.properties = new HashMap<>();
        this.properties.putAll(properties);
    }

    public EntityManagerFactory createEntityManagerFactory() {
        return adapter.createEntityManagerFactory(this, properties);
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return adapter.getJpaProvider().getClass().getName();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    @Override
    @Nullable
    public DataSource getJtaDataSource() {
        return null;
    }

    @Override
    @Nullable
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
    @Nullable
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
        Properties p = new Properties();
        p.putAll(properties);
        return p;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return "2.2";
    }

    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
    }

    @Override
    @Nullable
    public ClassLoader getNewTempClassLoader() {
        return null;
    }
}
