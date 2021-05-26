package ninja.javahacker.jpasimpletransactions.config;

import java.util.Locale;

/**
 * An enum that defines the values true, false, or unspecified.
 * @author Victor Williams Stafusa da Silva
 */
public enum OptionalBoolean {

    /**
     * Used to represent that some setting was left undefined with neither {@link #TRUE} nor {@link #FALSE} being specified as its value.
     */
    UNSPECIFIED,

    /**
     * Used to represent that some setting was defined as being false.
     */
    FALSE,

    /**
     * Used to represent that some setting was defined as being true.
     */
    TRUE;

    private final String asString;
    private final String code;

    private OptionalBoolean() {
        this.asString = name().toLowerCase(Locale.ROOT);
        this.code = ordinal() == 0 ? "" : asString;
    }

    /**
     * Returns {@code "unspecified"}, {@code "true"} or {@code "false"} depending on which elements of the enum {@code this} is.
     * @return {@code "unspecified"}, {@code "true"} or {@code "false"}.
     */
    @Override
    public String toString() {
        return asString;
    }

    /**
     * Returns {@code ""}, {@code "true"} or {@code "false"} depending on which elements of the enum {@code this} is.
     * @return {@code ""}, {@code "true"} or {@code "false"}.
     */
    public String getCode() {
        return code;
    }

    /**
     * Converts a {@code boolean} to either {@link #TRUE} or {@link #FALSE}. Never returns {@link #UNSPECIFIED}.
     * @param b The value to be convert.
     * @return The converted value.
     */
    public static OptionalBoolean from(boolean b) {
        return b ? TRUE : FALSE;
    }

    /**
     * Converts a {@link Boolean} to either {@link #TRUE} or {@link #FALSE} or even {@link #UNSPECIFIED} if converting from {@code null}.
     * @param b The value to be convert.
     * @return The converted value.
     */
    public static OptionalBoolean from(Boolean b) {
        return b == null ? UNSPECIFIED : b ? TRUE : FALSE;
    }
}
