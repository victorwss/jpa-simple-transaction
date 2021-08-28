package ninja.javahacker.jpasimpletransactions.config;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A simple minimalist implementation of the {@link PersistenceUnitInfo} interface.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SimplePersistenceUnitInfo implements PersistenceUnitInfo {

    @NonNull Optional<URL> url;
    @NonNull Class<? extends PersistenceProvider> providerClass;
    @NonNull String persistenceUnitName;
    @NonNull List<String> classes;
    @NonNull Map<String, String> properties;

    /**
     * Creates an instance of a {@code SimplePersistenceUnitInfo} with a minimal set of needed data for that.
     * @param url An {@link Optional} containg the {@link URL} that should be returned by the {@link #getPersistenceUnitRootUrl()} method.
     *     If there is no such  {@link URL}, an empty {@link Optional}.
     * @param providerClass The class to be returned by the {@link #getPersistenceProviderClassName()} method.
     * @param persistenceUnitName The name to be returned by the {@link #getPersistenceUnitName()} method.
     * @param classes The class to be returned by the {@link #getManagedClassNames()} method.
     * @param properties The properties to be returned by the {@link #getProperties()} method.
     * @throws IllegalArgumentException If any parameter is {@code null}.
     */
    @SuppressFBWarnings("OI_OPTIONAL_ISSUES_CHECKING_REFERENCE")
    public SimplePersistenceUnitInfo(
            @NonNull Optional<URL> url,
            @NonNull Class<? extends PersistenceProvider> providerClass,
            @NonNull String persistenceUnitName,
            @NonNull Collection<Class<?>> classes,
            @NonNull Map<String, String> properties)
    {
        this.url = url;
        this.providerClass = providerClass;
        this.persistenceUnitName = persistenceUnitName;
        this.classes = classes.stream().map(Class::getName).collect(Collectors.toList());
        this.properties = new HashMap<>();
        this.properties.putAll(properties);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String getPersistenceProviderClassName() {
        return providerClass.getName();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@link PersistenceUnitTransactionType#RESOURCE_LOCAL} in this method.
     */
    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@code null} in this method.
     */
    @Override
    @Nullable
    public DataSource getJtaDataSource() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@code null} in this method.
     */
    @Override
    @Nullable
    public DataSource getNonJtaDataSource() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns an empty list in this method.
     */
    @Override
    public List<String> getMappingFileNames() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws UncheckedIOException If an {@link IOException} happens when trying to read the data from the JARs in the classpath.
     */
    @Override
    public List<URL> getJarFileUrls() {
        try {
            return Collections.list(getClassLoader().getResources(""));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public URL getPersistenceUnitRootUrl() {
        return url.orElse(null);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public List<String> getManagedClassNames() {
        return Collections.unmodifiableList(classes);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@code false} in this method.
     */
    @Override
    public boolean excludeUnlistedClasses() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@link SharedCacheMode#UNSPECIFIED} in this method.
     */
    @Override
    public SharedCacheMode getSharedCacheMode() {
        return SharedCacheMode.UNSPECIFIED;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@link ValidationMode#AUTO} in this method.
     */
    @Override
    public ValidationMode getValidationMode() {
        return ValidationMode.AUTO;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Properties getProperties() {
        Properties p = new Properties();
        p.putAll(properties);
        return p;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@code "2.2"} in this method.
     */
    @Override
    public String getPersistenceXMLSchemaVersion() {
        return "2.2";
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns the context class loader of the current thread in this method.
     */
    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * {@inheritDoc}
     * @param transformer {@inheritDoc}
     * @implSpec This implementation does nothing in this method.
     */
    @Override
    public void addTransformer(ClassTransformer transformer) {
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@code null} in this method.
     */
    @Override
    @Nullable
    public ClassLoader getNewTempClassLoader() {
        return null;
    }
}
