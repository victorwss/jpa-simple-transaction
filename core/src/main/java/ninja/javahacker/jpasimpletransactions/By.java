package ninja.javahacker.jpasimpletransactions;

import lombok.NonNull;
import lombok.Value;

/**
 * Value-based class to describe one of the fields within the ordering of an <code>order by</code>
 * statement in JPQL queries.
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class By {

    /**
     * The name of the field within the <code>order by</code> statement.
     * -- GETTER --
     * Provides the name of the field within the <code>order by</code> statement.
     * @return The name of the field within the <code>order by</code> statement.
     */
    @NonNull
    String field;

    /**
     * Whether the field is sorted by descending order or not.
     * -- GETTER --
     * Tells whether the field is sorted by descending order or not.
     * @return Whether the field is sorted by descending order or not.
     */
    boolean descending;

    /**
     * Creates an instance descending-ordered by the given field name.
     * @param field The name of the field within the <code>order by</code> statement.
     * @return An instance descending-ordered by the given field name.
     */
    public static By desc(@NonNull String field) {
        return new By(field, true);
    }

    /**
     * Creates an instance ascending-ordered by the given field name.
     * @param field The name of the field within the <code>order by</code> statement.
     * @return An instance ascending-ordered by the given field name.
     */
    public static By asc(@NonNull String field) {
        return new By(field, false);
    }
}
