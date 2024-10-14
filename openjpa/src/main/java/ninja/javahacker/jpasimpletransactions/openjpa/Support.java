package ninja.javahacker.jpasimpletransactions.openjpa;

import java.util.Locale;

/**
 * Enum for the values of the Open JPA property {@code openjpa.RuntimeUnenhancedClasses}.
 * @author Victor Williams Stafusa da Silva
 */
public enum Support {

    /**
     * Used to represent that no setting about the usage of enhanced classes should be defined.
     */
    UNSPECIFIED,

    /**
     * Used to represent that unenhanced classes should be supported at runtime.
     */
    SUPPORTED,

    /**
     * Used to represent that unenhanced classes should not be supported at runtime.
     * Since the official documentation strongly recommends this, it was defined as default.
     */
    UNSUPPORTED,

    /**
     * Used to represent that unenhanced classes should be supported at runtime, but a warning should be emitted for them.
     */
    WARN;

    private final String asString;
    private final String code;

    private Support() {
        this.asString = name().toLowerCase(Locale.ROOT);
        this.code = ordinal() == 0 ? "" : asString;
    }

    /**
     * Returns {@code "unspecified"}, {@code "supported"}, {@code "unsupported"} or {@code "warn"},
     * depending on which elements of the enum {@code this} is.
     * @return {@code "unspecified"}, {@code "supported"}, {@code "unsupported"} or {@code "warn"}.
     */
    @Override
    public String toString() {
        return asString;
    }

    /**
     * Returns {@code ""}, {@code "supported"}, {@code "unsupported"} or {@code "warn"},
     * depending on which elements of the enum {@code this} is.
     * @return {@code ""}, {@code "supported"}, {@code "unsupported"} or {@code "warn"}.
     */
    public String getCode() {
        return code;
    }
}
