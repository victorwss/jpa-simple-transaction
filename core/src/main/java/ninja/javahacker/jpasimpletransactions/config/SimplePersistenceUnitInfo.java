package ninja.javahacker.jpasimpletransactions.config;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceProvider;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
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
    @NonNull Class<? extends Annotation> scopeAnnotation;
    @NonNull Map<String, String> properties;

    /**
     * Creates an instance of a {@code SimplePersistenceUnitInfo} with a minimal set of needed data for that.
     * @param url An {@link Optional} containing the {@link URL} that should be returned by the {@link #getPersistenceUnitRootUrl()} method.
     *     If there is no such  {@link URL}, an empty {@link Optional}.
     * @param providerClass The class to be returned by the {@link #getPersistenceProviderClassName()} method.
     * @param persistenceUnitName The name to be returned by the {@link #getPersistenceUnitName()} method.
     * @param classes The class to be returned by the {@link #getManagedClassNames()} method.
     * @param properties The properties to be returned by the {@link #getProperties()} method.
     * @param scopeAnnotation The annotation for denoting this scope, to be returned by the {@link #getScopeAnnotationName()} method.
     * @throws IllegalArgumentException If any parameter is {@code null}.
     */
    @SuppressFBWarnings({"OI_OPTIONAL_ISSUES_CHECKING_REFERENCE", "EI_EXPOSE_REP2"})
    public SimplePersistenceUnitInfo(
            @NonNull Optional<URL> url,
            @NonNull Class<? extends PersistenceProvider> providerClass,
            @NonNull String persistenceUnitName,
            @NonNull Collection<Class<?>> classes,
            @NonNull Class<? extends Annotation> scopeAnnotation,
            @NonNull Map<String, String> properties)
            throws IllegalArgumentException
    {
        this.url = url;
        this.providerClass = providerClass;
        this.persistenceUnitName = persistenceUnitName;
        this.classes = classes.stream().map(Class::getName).collect(Collectors.toUnmodifiableList());
        this.scopeAnnotation = scopeAnnotation;
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
     * @implSpec This implementation gives the class name informed at the constructor.
     */
    @Override
    public String getScopeAnnotationName() {
        return scopeAnnotation.getName();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns an empty list.
     */
    @Override
    public List<String> getQualifierAnnotationNames() {
        return List.of();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec This implementation always returns {@link PersistenceUnitTransactionType#RESOURCE_LOCAL} in this method.
     */
    @Override
    @SuppressWarnings("removal")
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
        return List.of();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws UncheckedIOException If an {@link IOException} happens when trying to read the data from the JARs in the classpath.
     */
    @Override
    public List<URL> getJarFileUrls() throws UncheckedIOException {
        ClassLoader cl = getClassLoader();
        if (cl == null) return List.of();
        try {
            return Collections.list(cl.getResources(""));
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
        return classes;
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
     * @implSpec This implementation always returns {@code "3.2"} in this method.
     */
    @Override
    public String getPersistenceXMLSchemaVersion() {
        return "3.2";
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
