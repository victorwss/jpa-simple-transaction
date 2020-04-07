package ninja.javahacker.jpasimpletransactions;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import lombok.NonNull;

/**
 * Extends the {@link TypedQuery} interface adding several useful methods into it
 * and deprecating those ones receiving {@link Date} or {@link Calendar} in favor of
 * those which works with the newer {@link java.time} package.
 * @author Victor Williams Stafusa da Silva
 */
public interface ExtendedTypedQuery<X> extends TypedQuery<X> {

    public default Optional<X> getSingleOptionalResult() {
        try {
            return Optional.of(getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public static <X> ExtendedTypedQuery<X> wrap(@NonNull TypedQuery<X> query) {
        return query instanceof ExtendedTypedQuery ? (ExtendedTypedQuery<X>) query : new SpecialTypedQuery<>(query);
    }

    @Override
    public ExtendedTypedQuery<X> setMaxResults(int i);

    @Override
    public ExtendedTypedQuery<X> setFirstResult(int i);

    @Override
    public ExtendedTypedQuery<X> setHint(String string, Object o);

    @Override
    public <T extends Object> ExtendedTypedQuery<X> setParameter(Parameter<T> prmtr, T t);

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(Parameter<Calendar> prmtr, Calendar clndr, TemporalType tt);

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(Parameter<Date> prmtr, Date date, TemporalType tt);

    @Override
    public ExtendedTypedQuery<X> setParameter(String string, Object o);

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(String string, Calendar clndr, TemporalType tt);

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(String string, Date date, TemporalType tt);

    @Override
    public ExtendedTypedQuery<X> setParameter(int i, Object o);

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(int i, Calendar clndr, TemporalType tt);

    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(int i, Date date, TemporalType tt);

    @Override
    public ExtendedTypedQuery<X> setFlushMode(FlushModeType fmt);

    @Override
    public ExtendedTypedQuery<X> setLockMode(LockModeType lmt);
}
