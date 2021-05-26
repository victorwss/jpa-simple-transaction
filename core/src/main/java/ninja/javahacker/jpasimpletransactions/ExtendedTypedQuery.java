package ninja.javahacker.jpasimpletransactions;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public static <X> ExtendedTypedQuery<X> wrap(@NonNull TypedQuery<X> query) {
        return query instanceof ExtendedTypedQuery ? (ExtendedTypedQuery<X>) query : new SpecialTypedQuery<>(query);
    }

    /**
     * {@inheritDoc}
     * @param maxResults {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ExtendedTypedQuery<X> setMaxResults(int maxResults);

    /**
     * {@inheritDoc}
     * @param startPosition {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ExtendedTypedQuery<X> setFirstResult(int startPosition);

    /**
     * {@inheritDoc}
     * @param hintName {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ExtendedTypedQuery<X> setHint(String hintName, Object value);

    /**
     * {@inheritDoc}
     * @param param {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public <T extends Object> ExtendedTypedQuery<X> setParameter(Parameter<T> param, T value);

    /**
     * {@inheritDoc}
     * @param param {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @deprecated Do not use the horrible {@link Calendar} class anymore. Use {@link LocalDate}, {@link LocalDateTime} or some
     *     other better suited date/time class from the {@link java.time} package and then simply use the
     *     {@link #setParameter(Parameter, Object)} on it.
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(Parameter, Object)
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType);

    /**
     * {@inheritDoc}
     * @param param {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @deprecated Do not use the horrible {@link Date} class anymore. Use {@link LocalDate}, {@link LocalDateTime} or some
     *     other better suited date/time class from the {@link java.time} package and then simply use the
     *     {@link #setParameter(Parameter, Object)} on it.
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(Parameter, Object)
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType);

    /**
     * {@inheritDoc}
     * @param name {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ExtendedTypedQuery<X> setParameter(String name, Object value);

    /**
     * {@inheritDoc}
     * @param name {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @deprecated Do not use the horrible {@link Calendar} class anymore. Use {@link LocalDate}, {@link LocalDateTime} or some
     *     other better suited date/time class from the {@link java.time} package and then simply use the
     *     {@link #setParameter(String, Object)} on it.
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(String, Object)
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType);

    /**
     * {@inheritDoc}
     * @param name {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @deprecated Do not use the horrible {@link Date} class anymore. Use {@link LocalDate}, {@link LocalDateTime} or some
     *     other better suited date/time class from the {@link java.time} package and then simply use the
     *     {@link #setParameter(String, Object)} on it.
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(String, Object)
     */
    @Deprecated
    @Override
    public ExtendedTypedQuery<X> setParameter(String name, Date value, TemporalType temporalType);

    /**
     * {@inheritDoc}
     * @param position {@inheritDoc}
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ExtendedTypedQuery<X> setParameter(int position, Object value);

    /**
     * {@inheritDoc}
     * @param position {@inheritDoc}
     * @param value {@inheritDoc}
     * @param temporalType {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @deprecated Do not use the horrible {@link Calendar} class anymore. Use {@link LocalDate}, {@link LocalDateTime} or some
     *     other better suited date/time class from the {@link java.time} package and then simply use the
     *     {@link #setParameter(int, Object)} on it.
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(int, Object)
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
     * @throws IllegalArgumentException {@inheritDoc}
     * @deprecated Do not use the horrible {@link Date} class anymore. Use {@link LocalDate}, {@link LocalDateTime} or some
     *     other better suited date/time class from the {@link java.time} package and then simply use the
     *     {@link #setParameter(int, Object)} on it.
     * @see LocalDate
     * @see LocalDateTime
     * @see #setParameter(int, Object)
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
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public ExtendedTypedQuery<X> setLockMode(LockModeType lockMode);
}
