package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Parameter;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.PackagePrivate;

/**
 * Implementation of the {@link ExtendedTypedQuery} interface that
 * delegates to some other {@link TypedQuery}.
 * @author Victor Williams Stafusa da Silva
 */
@SuppressFBWarnings(
        value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT",
        justification = "Several 'return this;' to preserve the return type as SpecialTypedQuery<X>"
)
@PackagePrivate
class SpecialTypedQuery<X> implements ExtendedTypedQuery<X> {

    @Delegate(types = DelegatedParts.class)
    private final TypedQuery<X> delegate;

    public SpecialTypedQuery(@NonNull TypedQuery<X> query) {
        this.delegate = query;
    }

    @Override
    public X getSingleResultOrNull() {
        return delegate.getSingleResultOrNull();
    }

    @Override
    public List<X> getResultList() {
        return delegate.getResultList();
    }

    @Override
    public Stream<X> getResultStream() {
        return delegate.getResultStream();
    }

    @Override
    public X getSingleResult() {
        return delegate.getSingleResult();
    }

    @Override
    public SpecialTypedQuery<X> setMaxResults(int maxResults) {
        delegate.setMaxResults(maxResults);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setFirstResult(int startPosition) {
        delegate.setFirstResult(startPosition);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setHint(String hintName, Object value) {
        delegate.setHint(hintName, value);
        return this;
    }

    @Override
    public <T> SpecialTypedQuery<X> setParameter(Parameter<T> param, T value) {
        delegate.setParameter(param, value);
        return this;
    }

    @Deprecated
    @Override
    public SpecialTypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        delegate.setParameter(param, value, temporalType);
        return this;
    }

    @Deprecated
    @Override
    public SpecialTypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        delegate.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setParameter(String name, Object value) {
        delegate.setParameter(name, value);
        return this;
    }

    @Deprecated
    @Override
    public SpecialTypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
        delegate.setParameter(name, value, temporalType);
        return this;
    }

    @Deprecated
    @Override
    public SpecialTypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
        delegate.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setParameter(int position, Object value) {
        delegate.setParameter(position, value);
        return this;
    }

    @Deprecated
    @Override
    public SpecialTypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
        delegate.setParameter(position, value, temporalType);
        return this;
    }

    @Deprecated
    @Override
    public SpecialTypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
        delegate.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setFlushMode(FlushModeType flushMode) {
        delegate.setFlushMode(flushMode);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setLockMode(LockModeType lockMode) {
        delegate.setLockMode(lockMode);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setTimeout(Integer timeout) {
        delegate.setTimeout(timeout);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setCacheStoreMode(CacheStoreMode mode) {
        delegate.setCacheStoreMode(mode);
        return this;
    }

    @Override
    public SpecialTypedQuery<X> setCacheRetrieveMode(CacheRetrieveMode mode) {
        delegate.setCacheRetrieveMode(mode);
        return this;
    }

    /**
     * Exists only to tell lombok which methods should be delegated.
     */
    private static interface DelegatedParts {
        public int executeUpdate();

        public int getMaxResults();

        public int getFirstResult();

        public Integer getTimeout();

        public CacheStoreMode getCacheStoreMode();

        public CacheRetrieveMode getCacheRetrieveMode();

        public Map<String, Object> getHints();

        public Set<Parameter<?>> getParameters();

        public Parameter<?> getParameter(String name);

        public <T> Parameter<T> getParameter(String name, Class<T> type);

        public Parameter<?> getParameter(int position);

        public <T> Parameter<T> getParameter(int position, Class<T> type);

        public boolean isBound(Parameter<?> param);

        public <T> T getParameterValue(Parameter<T> param);

        public Object getParameterValue(String name);

        public Object getParameterValue(int position);

        public FlushModeType getFlushMode();

        public LockModeType getLockMode();

        public <X> X unwrap(Class<X> type);
    }
}
