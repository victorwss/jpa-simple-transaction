package ninja.javahacker.ninjdao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ExtendedEntityManager;
import ninja.javahacker.jpasimpletransactions.ExtendedTypedQuery;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

public class DaoFactory {

    private final Supplier<ExtendedEntityManager> giver;

    public DaoFactory(@NonNull Supplier<ExtendedEntityManager> giver) {
        this.giver = giver;
    }

    public <E> E daoFor(Class<E> type) {
        return daoFor(ReifiedGeneric.of(type));
    }

    private static MethodModel of(@NonNull Object proxy, @NonNull Method m) {
        validateParameterSet(m);
        var jpql = m.getAnnotation(JPQL.class);
        /*if (jpql == null) {
            if (isToString(m)) return "Dao[" + type + "] from " + toString();
            if (isHashCode(m)) return System.identityHashCode(proxy);
            if (isEquals(m)) return a[0] == proxy;
            if (m.isDefault()) return m.invoke(proxy, a);
            throw new AssertionError();
        }*/
        var returnType = findReturn(m);
        var type = findType(m);
        var idx = 0;
        List<BiFunction<Call, ExtendedTypedQuery, ExtendedTypedQuery>> proccessParams = new ArrayList<>(m.getParameterCount());
        for (Parameter p : m.getParameters()) {
            proccessParams.add(findParameter(p, idx));
            idx++;
        }
        return new DaoMethodModel(m, returnType, type, proccessParams, null);
    }

    private static BiFunction<Call, ExtendedTypedQuery, ExtendedTypedQuery> findParameter(Parameter p, int index) {
        if (p.isAnnotationPresent(FirstResult.class)) {
            if (p.getType() != int.class) throw new UnsupportedOperationException();
            return (c, x) -> x.setFirstResult((int) c.getParameters()[index]);
        }
        if (p.isAnnotationPresent(MaxResults.class)) {
            if (p.getType() != int.class) throw new UnsupportedOperationException();
            return (c, x) -> x.setMaxResults((int) c.getParameters()[index]);
        }
        return (c, x) -> x.setParameter(p.getName(), c.getParameters()[index]);
    }

    private static BiFunction<Call, ExtendedTypedQuery, ?> findReturn(Method m) {
        var ret = m.getGenericReturnType();
        if (ret instanceof Class<?>) return (c, x) -> x.getSingleResult();
        if (!(ret instanceof ParameterizedType)) throw new UnsupportedOperationException();
        var pt = (ParameterizedType) ret;
        if (pt.getRawType() == Stream.class) return (c, x) -> x.getResultStream();
        if (pt.getRawType() == List.class) return (c, x) -> x.getResultList();
        if (pt.getRawType() == Optional.class) return (c, x) -> x.getSingleOptionalResult();
        throw new UnsupportedOperationException();
    }

    private static Class<?> findType(Method m) {
        var ret = m.getGenericReturnType();
        if (ret instanceof Class<?>) return (Class<?>) ret;
        if (!(ret instanceof ParameterizedType)) throw new UnsupportedOperationException();
        var pt = (ParameterizedType) ret;
        var p1 = pt.getActualTypeArguments()[0];
        if (p1 instanceof Class<?>) return (Class<?>) p1;
        throw new UnsupportedOperationException();
    }

    private static void validateParameterSet(Method m) {
        int maxResults = -1;
        int firstResult = -1;
        int idx = 0;
        for (Parameter p : m.getParameters()) {
            if (p.isAnnotationPresent(FirstResult.class)) {
                if (firstResult != -1) throw new UnsupportedOperationException();
                firstResult = idx;
            }
            if (p.isAnnotationPresent(MaxResults.class)) {
                if (maxResults != -1) throw new UnsupportedOperationException();
                maxResults = idx;
            }
            idx++;
        }
        if (maxResults == firstResult && firstResult != -1) throw new UnsupportedOperationException();
        if (maxResults == firstResult && firstResult != -1) throw new UnsupportedOperationException();
    }

    private static boolean is(Method m, String name, int paramCount) {
        return m.getParameterCount() == paramCount && name.equals(m.getName());
    }

    private static boolean isEquals(Method m) {
        return is(m, "equals", 1);
    }

    private static boolean isHashCode(Method m) {
        return is(m, "hashCode", 0);
    }

    private static boolean isToString(Method m) {
        return is(m, "toString", 0);
    }

    private static boolean isClone(Method m) {
        return is(m, "clone", 0);
    }

    private static boolean isFinalize(Method m) {
        return is(m, "finalize", 0);
    }

    private static boolean isSpecial(Method m) {
        return isEquals(m) || isHashCode(m) || isToString(m) || isClone(m) || isFinalize(m);
    }

    @Getter
    @Data
    @AllArgsConstructor
    private static class Call {

        @NonNull
        private final Method method;

        @NonNull
        private final Object[] parameters;
    }

    private static interface MethodModel {
        public Object run(ExtendedEntityManager em, Object[] args);
    }

    @Getter
    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class DaoMethodModel implements MethodModel {

        @NonNull
        private final Method m;

        @NonNull
        private final BiFunction<Call, ExtendedTypedQuery, ?> returnType;

        @NonNull
        private final Class<?> type;

        @NonNull
        private final List<BiFunction<Call, ExtendedTypedQuery, ExtendedTypedQuery>> proccessParams;

        @NonNull
        private final Function<ExtendedTypedQuery, ?> run;

        @Override
        public Object run(ExtendedEntityManager em, Object[] args) {
            Call c = new Call(m, args);
            throw new UnsupportedOperationException("TO DO");
        }
    }

    public <E> E daoFor(ReifiedGeneric<E> type) {
        var x = type.raw();
        if (!x.isInterface()) throw new IllegalArgumentException();

        for (Method m : x.getMethods()) {
            if (!isSpecial(m) && !m.isDefault() && !m.isAnnotationPresent(JPQL.class)) throw new IllegalArgumentException();
            var t = ReifiedGeneric.of(m.getGenericReturnType());
        }

        InvocationHandler ih = (p, m, a) -> {
            var jpql = m.getAnnotation(JPQL.class);
            if (jpql == null) {
                if (isToString(m)) return "Dao[" + type + "] from " + toString();
                if (isHashCode(m)) return System.identityHashCode(p);
                if (isEquals(m)) return a[0] == p;
                if (m.isDefault()) return m.invoke(p, a);
                throw new AssertionError();
            }
            var ret = ReifiedGeneric.of(m.getGenericReturnType());
            Class<?> k = null;
            TypedQuery<?> q = giver.get().createQuery(jpql.value(), k);
            throw new UnsupportedOperationException("TO DO");
        };
        throw new UnsupportedOperationException("TO DO");
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