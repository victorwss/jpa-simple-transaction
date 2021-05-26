package ninja.javahacker.jpasimpletransactions;

import lombok.NonNull;
import lombok.Value;

/**
 * Value-based class to describe the ordering of an <code>order by</code>
 * statement in JPQL queries.
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class By {
    @NonNull
    String field;

    boolean descending;

    public static By desc(@NonNull String field) {
        return new By(field, true);
    }

    public static By asc(@NonNull String field) {
        return new By(field, false);
    }
}
