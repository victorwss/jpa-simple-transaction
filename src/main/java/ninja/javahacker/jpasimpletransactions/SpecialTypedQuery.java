package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.PackagePrivate;

/**
 * @author Victor Williams Stafusa da Silva
 */
@SuppressFBWarnings("RV")
@PackagePrivate
class SpecialTypedQuery<X> implements ExtendedTypedQuery<X> {
    @Delegate(types = DelegatedParts.class)
    private final TypedQuery<X> delegate;

    public SpecialTypedQuery(@NonNull TypedQuery<X> query) {
        this.delegate = query;
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
    public ExtendedTypedQuery<X> setMaxResults(int i) {
        delegate.setMaxResults(i);
        return this;
    }

    @Override
    public ExtendedTypedQuery<X> setFirstResult(int i) {
        delegate.setFirstResult(i);
        return this;
    }

    @Override
    public ExtendedTypedQuery<X> setHint(String string, Object o) {
        delegate.setHint(string, o);
        return this;
    }

    @Override
    public <T> ExtendedTypedQuery<X> setParameter(Parameter<T> prmtr, T t) {
        delegate.setParameter(prmtr, t);
        return this;
    }

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(Parameter<Calendar> prmtr, Calendar clndr, TemporalType tt) {
        delegate.setParameter(prmtr, clndr, tt);
        return this;
    }

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(Parameter<Date> prmtr, Date date, TemporalType tt) {
        delegate.setParameter(prmtr, date, tt);
        return this;
    }

    @Override
    public ExtendedTypedQuery<X> setParameter(String string, Object o) {
        delegate.setParameter(string, o);
        return this;
    }

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(String string, Calendar clndr, TemporalType tt) {
        delegate.setParameter(string, clndr, tt);
        return this;
    }

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(String string, Date date, TemporalType tt) {
        delegate.setParameter(string, date, tt);
        return this;
    }

    @Override
    public ExtendedTypedQuery<X> setParameter(int i, Object o) {
        delegate.setParameter(i, o);
        return this;
    }

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(int i, Calendar clndr, TemporalType tt) {
        delegate.setParameter(i, clndr, tt);
        return this;
    }

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(int i, Date date, TemporalType tt) {
        delegate.setParameter(i, date, tt);
        return this;
    }

    @Override
    public ExtendedTypedQuery<X> setFlushMode(FlushModeType fmt) {
        delegate.setFlushMode(fmt);
        return this;
    }

    @Override
    public ExtendedTypedQuery<X> setLockMode(LockModeType lmt) {
        delegate.setLockMode(lmt);
        return this;
    }

    private static interface DelegatedParts {
        public int executeUpdate();

        public int getMaxResults();

        public int getFirstResult();

        public Map<String, Object> getHints();

        public Set<Parameter<?>> getParameters();

        public Parameter<?> getParameter(String string);

        public <T> Parameter<T> getParameter(String string, Class<T> type);

        public Parameter<?> getParameter(int i);

        public <T> Parameter<T> getParameter(int i, Class<T> type);

        public boolean isBound(Parameter<?> prmtr);

        public <T> T getParameterValue(Parameter<T> prmtr);

        public Object getParameterValue(String string);

        public Object getParameterValue(int i);

        public FlushModeType getFlushMode();

        public LockModeType getLockMode();

        public <T> T unwrap(Class<T> type);
    }
}
