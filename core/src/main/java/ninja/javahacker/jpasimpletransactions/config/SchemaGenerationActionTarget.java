package ninja.javahacker.jpasimpletransactions.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

/**
 * Specifies whether scripts for creation and dropping of the tables should be automatically generated and where they should be stored.
 * <p>Used to set the following properties:</p>
 * <ul>
 * <li>{@code [javax|jakarta].persistence.schema-generation.scripts.action}.</li>
 * <li>{@code [javax|jakarta].persistence.schema-generation.scripts.create-target}.</li>
 * <li>{@code [javax|jakarta].persistence.schema-generation.scripts.drop-target}.</li>
 * </ul>
 * @see StandardConnectorFactory#getSchemaScriptStoreLocation()
 * @see StandardConnectorFactory#withSchemaScriptStoreLocation(SchemaGenerationActionTarget)
 * @see #unspecified()
 * @see #none()
 * @see #drop(String)
 * @see #create(String)
 * @see #dropAndCreate(String, String)
 * @see <a href="https://docs.oracle.com/javaee/7/tutorial/persistence-intro005.htm">Database Schema Creation</a>
 * @author Victor Williams Stafusa da Silva
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaGenerationActionTarget {

    /**
     * The set of scripts produced for table creation and droppings, if defined.
     * -- GETTER --
     * Provides what is the set of scripts used for table creation and droppings.
     * @return The set of scripts used for table creation and droppings, or an empty string ({@code ""}) if there is none defined.
     */
    @NonNull
    private final String strategy;

    /**
     * The location for the produced script for table creation.
     * -- GETTER --
     * If an user-provided script should be created for creating database artifacts, this gives the target
     * script location relative to the root of the persistence unit. For example, {@code "META-INF/sql/some-create-script.sql"}.
     * @return The location of the user-provided script for table creation or an empty string ({@code ""}) if there is none.
     */
    @NonNull
    private final String createScript;

    /**
     * The location for the produced script for table dropping.
     * -- GETTER --
     * If an user-provided script should be created for droppind database artifacts, this gives the target
     * script location relative to the root of the persistence unit. For example, {@code "META-INF/sql/some-drop-script.sql"}.
     * @return The location of the user-provided script for table dropiings or an empty string ({@code ""}) if there is none.
     */
    @NonNull
    private final String dropScript;

    /**
     * Defines that the production of scripts for table creation and droppings is left unspecified.
     * @return An object representing the strategy detailed above.
     */
    public static SchemaGenerationActionTarget unspecified() {
        return new SchemaGenerationActionTarget("", "", "");
    }

    /**
     * Defines that no production of scripts for table creation and dropping should happen.
     * @return An object representing the strategy detailed above.
     */
    public static SchemaGenerationActionTarget none() {
        return new SchemaGenerationActionTarget("none", "", "");
    }

    /**
     * Defines that no scripts for table creation should be produced, but a script for table droppings should.
     * @param dropScript The location where the script to drop tables relative to the root of the persistence unit should be stored.
     *     For example, {@code "META-INF/sql/some-create-script.sql"}.
     * @return An object representing the strategy detailed above.
     */
    public static SchemaGenerationActionTarget drop(@NonNull String dropScript) {
        return new SchemaGenerationActionTarget("drop", "", dropScript);
    }

    /**
     * Defines that no scripts for table droppings should be produced, but a script for table creation should.
     * @param createScript The location where the script to create tables relative to the root of the persistence unit should be stored.
     *     For example, {@code "META-INF/sql/some-drop-script.sql"}.
     * @return An object representing the strategy detailed above.
     */
    public static SchemaGenerationActionTarget create(@NonNull String createScript) {
        return new SchemaGenerationActionTarget("create", createScript, "");
    }

    /**
     * Defines that scripts for both table creation and droppings should be produced.
     * @param dropScript The location where the script to drop tables relative to the root of the persistence unit should be stored.
     *     For example, {@code "META-INF/sql/some-create-script.sql"}.
     * @param createScript The location where the script to create tables relative to the root of the persistence unit should be stored.
     *     For example, {@code "META-INF/sql/some-drop-script.sql"}.
     * @return An object representing the strategy detailed above.
     */
    public static SchemaGenerationActionTarget dropAndCreate(@NonNull String createScript, @NonNull String dropScript) {
        return new SchemaGenerationActionTarget("drop-and-create", createScript, dropScript);
    }
}
