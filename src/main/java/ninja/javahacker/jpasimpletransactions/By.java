package ninja.javahacker.jpasimpletransactions;

import lombok.NonNull;
import lombok.Value;

/**
 * @author Victor Williams Stfausa da Silva
 */
@Value
public class By {
    @NonNull
    String field;

    boolean desc;

    public static By desc(@NonNull String field) {
        return new By(field, true);
    }

    public static By asc(@NonNull String field) {
        return new By(field, false);
    }
}
