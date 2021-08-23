package ninja.javahacker.ninjadao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the annotated method executes an JPQL query (and not an JPQL instruction).
 * @author Victor Williams Stafusa da Silva
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Select {

    /**
     * JPQL query to be executed.
     * @return The JPQL query to be executed.
     */
    public String value();
}
