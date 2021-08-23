package ninja.javahacker.ninjadao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ninja.javahacker.jpasimpletransactions.ExtendedTypedQuery;

/**
 * Marks the parameter as responsible for setting the first result of a query.
 * @see ExtendedTypedQuery#setFirstResult(int)
 * @author Victor Williams Stafusa da Silva
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstResult {
}
