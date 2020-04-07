package ninja.javahacker.ninjdao;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ExtendedEntityManager;
import ninja.javahacker.jpasimpletransactions.ExtendedTypedQuery;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

public class DaoFactory {

    private final Supplier<ExtendedEntityManager> giver;

    public DaoFactory(@NonNull Supplier<ExtendedEntityManager> giver) {
        this.giver = giver;
    }

    @Nullable
    private Object execute(@NonNull Method m, @NonNull Object... params) {
        return execute(ReifiedGeneric.of(m.getGenericReturnType()), m, params);
    }

    @Nullable
    @SuppressWarnings({"unchecked", "UnnecessaryBoxing"})
    @SuppressFBWarnings("URV_UNRELATED_RETURN_VALUES")
    private <X> X execute(@NonNull ReifiedGeneric<X> ret, @NonNull Method m, @NonNull Object... params) {
        ExtendedEntityManager em = giver.get();
        boolean execute = m.isAnnotationPresent(Execute.class);
        String jpql = execute ? m.getAnnotation(Execute.class).value() : m.getAnnotation(Select.class).value();
        ExtendedTypedQuery<X> q = em.createQuery(jpql, ret.raw());

        var idx = 0;
        for (Parameter p : m.getParameters()) {
            q = p.isAnnotationPresent(FirstResult.class) ? q.setFirstResult((int) params[idx])
                    : p.isAnnotationPresent(MaxResults.class) ? q.setMaxResults((int) params[idx])
                    : q.setParameter(p.getName(), params[idx]);
            idx++;
        }

        var r = m.getGenericReturnType();
        if (execute) {
            int result = q.executeUpdate();
            if (r == void.class) return null;
            if (r == int.class || r == Integer.class) return (X) Integer.valueOf(result);
            if (r == OptionalInt.class) return (X) OptionalInt.of(result);
        } else {
            if (r instanceof Class<?>) return q.getSingleResult();
            if (!(r instanceof ParameterizedType)) throw new AssertionError();
            var pt = (ParameterizedType) r;
            if (pt.getRawType() == Stream.class) return (X) q.getResultStream();
            if (pt.getRawType() == List.class) return (X) q.getResultList();
            if (pt.getRawType() == Optional.class) return (X) q.getSingleOptionalResult();
        }
        throw new AssertionError();
    }

    @SuppressWarnings("element-type-mismatch")
    private static void validateMethod(Method m) {
        var ret = m.getGenericReturnType();
        ReifiedGeneric.of(ret); // Check for sanity.

        if (isClone(m) || isFinalize(m)) {
            throw new UnsupportedOperationException("The method " + m.toGenericString() + " is forbidden for this interface.");
        }
        if (isEquals(m) || isHashCode(m) || isToString(m)) {
            if (m.isAnnotationPresent(Select.class)) {
                throw new UnsupportedOperationException("The method " + m.toGenericString()
                        + " shouldn't do database operations, so it shouldn't feature the @Select annotation.");
            }
            if (m.isAnnotationPresent(Execute.class)) {
                throw new UnsupportedOperationException("The method " + m.toGenericString()
                        + " shouldn't do database operations, so it shouldn't feature the @Execute annotation.");
            }
        }
        if (isStatic(m)) {
            if (m.isAnnotationPresent(Select.class)) {
                throw new UnsupportedOperationException("The @Select annotation can't be applied to static method, "
                        + "so it can't be applied to the method " + m.toGenericString() + ".");
            }
            if (m.isAnnotationPresent(Execute.class)) {
                throw new UnsupportedOperationException("The @Execute annotation can't be applied to static method, "
                        + "so it can't be applied to the method " + m.toGenericString() + ".");
            }
        }

        int maxResults = -1;
        int firstResult = -1;
        int idx = 0;
        for (Parameter p : m.getParameters()) {
            if (p.isAnnotationPresent(FirstResult.class)) {
                if (firstResult != -1) {
                    throw new UnsupportedOperationException("Can't feature multiple parameters annotated with @FirstResult.");
                }
                if (int.class != p.getType()) {
                    throw new UnsupportedOperationException("The parameter annotated with @FirstResult must be of type 'int'.");
                }
                firstResult = idx;
            }
            if (p.isAnnotationPresent(MaxResults.class)) {
                if (maxResults != -1) {
                    throw new UnsupportedOperationException("Can't feature multiple parameters annotated with @MaxResults.");
                }
                if (int.class != p.getType()) {
                    throw new UnsupportedOperationException("The parameter annotated with @MaxResults must be of type 'int'.");
                }
                maxResults = idx;
            }
            idx++;
        }
        if (maxResults == firstResult && firstResult != -1) {
            throw new UnsupportedOperationException("Can't feature both the annotations "
                    + "@FirstResult and @MaxResults at the same parameter.");
        }
        if (m.isAnnotationPresent(Select.class) && m.isAnnotationPresent(Execute.class)) {
            throw new UnsupportedOperationException("The method " + m.toGenericString()
                    + " can't feature both the @Select and the @Execute anotations.");
        }
        if (m.isAnnotationPresent(Select.class)) {
            if (ret instanceof ParameterizedType
                    ? !List.of(Stream.class, List.class, Optional.class).contains(((ParameterizedType) ret).getRawType())
                    : !(ret instanceof Class<?>))
            {
                throw new UnsupportedOperationException("Don't know how to produce an instance of the given return type for method "
                        + m.toGenericString() + ".");
            }
        }
        if (m.isAnnotationPresent(Execute.class)) {
            if (!List.of(void.class, int.class).contains(ret)) {
                throw new UnsupportedOperationException("Don't know how to produce an instance of the given return type for method "
                        + m.toGenericString() + ".");
            }
        }
        if (!m.isAnnotationPresent(Select.class) && !m.isAnnotationPresent(Execute.class)) {
            if (firstResult != -1) {
                throw new UnsupportedOperationException("The @FirstResult annotation does not makes sense for the method "
                        + m.toGenericString() + " which lacks a @Select or an @Execute annotation.");
            }
            if (maxResults != -1) {
                throw new UnsupportedOperationException("The @MaxResults annotation does not makes sense for the method "
                        + m.toGenericString() + " which lacks a @Select or an @Execute annotation.");
            }
            if (!isEquals(m) && !isHashCode(m) && !isToString(m) && !isStatic(m) && !m.isDefault()) {
                throw new UnsupportedOperationException("Method " + m.toGenericString()
                        + " is lacking the @Select or @Execute annottion.");
            }
        }
    }

    private static boolean is(@NonNull Method m, @NonNull String name, @NonNull Class<?>... params) {
        return Arrays.equals(m.getParameterTypes(), params) && name.equals(m.getName());
    }

    private static boolean isEquals(Method m) {
        return is(m, "equals", Object.class);
    }

    private static boolean isHashCode(Method m) {
        return is(m, "hashCode");
    }

    private static boolean isToString(Method m) {
        return is(m, "toString");
    }

    private static boolean isClone(Method m) {
        return is(m, "clone");
    }

    private static boolean isFinalize(Method m) {
        return is(m, "clone") || is(m, "finalize");
    }

    private static boolean isStatic(Method m) {
        return Modifier.isStatic(m.getModifiers());
    }

    public <E> E daoFor(Class<E> type) {
        return daoFor(ReifiedGeneric.of(type));
    }

    public <E> E daoFor(ReifiedGeneric<E> type) {
        Class<E> targetInterface = type.raw();
        if (!targetInterface.isInterface()) throw new IllegalArgumentException();

        for (Method m : targetInterface.getMethods()) {
            validateMethod(m);
        }

        InvocationHandler ih = (p, m, a) -> {
            if (m.isAnnotationPresent(Select.class) || m.isAnnotationPresent(Execute.class)) return execute(m, a);
            if (isToString(m)) return "Dao[" + type + "] from " + toString();
            if (isHashCode(m)) return System.identityHashCode(p);
            if (isEquals(m)) return a[0] == p;
            if (m.isDefault()) return m.invoke(p, a);
            throw new AssertionError();
        };
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        Object proxy = Proxy.newProxyInstance(ccl, new Class<?>[] {targetInterface}, ih);
        return targetInterface.cast(proxy);
    }

    @Override
    public String toString() {
        return "DaoFactory[" + giver.toString() + "]";
    }

    @Override
    public int hashCode() {
        return giver.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DaoFactory)) return false;
        return giver.equals(((DaoFactory) other).giver);
    }
}