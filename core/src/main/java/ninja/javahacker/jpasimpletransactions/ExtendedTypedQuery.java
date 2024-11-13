package ninja.javahacker.jpasimpletransactions;

import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Parameter;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import lombok.NonNull;

/**
 * Extends the {@link TypedQuery} interface adding several useful methods into it
 * and deprecating those ones receiving {@link Date} or {@link Calendar} in favor of
 * those which works with the newer {@link java.time} package.
 * @param <X> The generic type of the {@link TypedQuery}.
 * @author Victor Williams Stafusa da Silva
 */
public interface ExtendedTypedQuery<X> extends TypedQuery<X> {

    /**
     * Gets a single line as a result and wraps it inside an {@link Optional}. If there is no result, an empty {@link Optional}
     * is returned instead.
     * @return An {@code Optional} containing the result or an empty {@code Optional} if there is no result.
     */
    public default Optional<X> getOptionalResult() {
        try {
            return Optional.of(getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Produces an instance of an {@code ExtendedTypedQuery} from a common {@link TypedQuery}.
     * If the given instance is already an {@code ExtendedTypedQuery}, simply returns it unchanged.
     * @param <X> The generic type of the {@link TypedQuery}.
     * @param query The {@link TypedQuery} that should be decorated as an {@code ExtendedTypedQuery}.
     * @return An {@code ExtendedTypedQuery} corresponding to a decorator of the given {@link TypedQuery}.
     * @throws IllegalArgumentException If {@code query} is {@code null}.
     */
    public static <X> ExtendedTypedQuery<X> wrap(@NonNull TypedQuery<X> query) throws IllegalArgumentException {
        return query instanceof ExtendedTypedQuery ? (ExtendedTypedQuery<X>) query : new SpecialTypedQuery<>(query);
    }

    /**
     * {@inheritDoc}
     * @param maxResults {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the argument is negative
     */
    @Override
    public ExtendedTypedQuery<X> setMaxResults(int maxResults) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param startPosition {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the argument is negative
     */
    @Override
    public ExtendedTypedQuery<X> setFirstResult(int startPosition) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param hintName {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the second argument is not
     *     valid for the implementation
     */
    @Override
    public ExtendedTypedQuery<X> setHint(String hintName, Object value) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param param {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the parameter
     *     does not correspond to a parameter of the
     *     query
     */
    @Override
    public <T extends Object> ExtendedTypedQuery<X> setParameter(Parameter<T> param, T value) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param param {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the parameter does not
     *     correspond to a parameter of the query
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(Parameter, Object)
     * @deprecated Newly-written code should use the date/time types
     *     defined in {@link java.time}.
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(
            Parameter<Calendar> param,
            Calendar value,
            TemporalType temporalType)
            throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param param {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the parameter does not
     *     correspond to a parameter of the query
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(Parameter, Object)
     * @deprecated Newly-written code should use the date/time types
     *     defined in {@link java.time}.
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param name {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the parameter name does
     *     not correspond to a parameter of the query or if
     *     the argument is of incorrect type
     */
    @Override
    public ExtendedTypedQuery<X> setParameter(String name, Object value) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param name {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the parameter name does
     *     not correspond to a parameter of the query or if
     *     the value argument is of incorrect type
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(String, Object)
     * @deprecated Newly-written code should use the date/time types
     *     defined in {@link java.time}.
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param name {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if position does not
     *     correspond to a positional parameter of the
     *     query or if the argument is of incorrect type
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(String, Object)
     * @deprecated Newly-written code should use the date/time types
     *     defined in {@link java.time}.
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param position {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if position does not
     *     correspond to a positional parameter of the
     *     query or if the argument is of incorrect type
     */
    @Override
    public ExtendedTypedQuery<X> setParameter(int position, Object value) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @param position {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if position does not
     *     correspond to a positional parameter of the query
     *     or if the value argument is of incorrect type
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(int, Object)
     * @deprecated Newly-written code should use the date/time types
     *     defined in {@link java.time}.
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType);

    /**
     * {@inheritDoc}
     * @param position {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if position does not
     *     correspond to a positional parameter of the query
     *     or if the value argument is of incorrect type
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(int, Object)
     * @deprecated Newly-written code should use the date/time types
     *     defined in {@link java.time}.
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(int position, Date value, TemporalType temporalType);

    /**
     * {@inheritDoc}
     * @param flushMode {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public ExtendedTypedQuery<X> setFlushMode(FlushModeType flushMode);

    /**
     * {@inheritDoc}
     * @param lockMode {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException if the query is found not to
     *     be a Jakarta Persistence query language SELECT query
     *     or a {@link jakarta.persistence.criteria.CriteriaQuery}
     *     query
     */
    @Override
    public ExtendedTypedQuery<X> setLockMode(LockModeType lockMode) throws IllegalArgumentException;
}
