package ninja.javahacker.ninjadao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the annotated method executes an JPQL instruction (and not an JPQL query).
 * @author Victor Williams Stafusa da Silva
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Execute {

    /**
     * JPQL instruction to be executed.
     * @return The JPQL instruction to be executed.
     */
    public String value();
}
