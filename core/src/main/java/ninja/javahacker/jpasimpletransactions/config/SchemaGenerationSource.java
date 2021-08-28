package ninja.javahacker.jpasimpletransactions.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

/**
 * Specifies the strategy used for executing custom scripts on creating and dropping database artifacts.
 * <p>Used to set the following properties:</p>
 * <ul>
 * <li>{@code [javax|jakarta].persistence.schema-generation.create-source}.</li>
 * <li>{@code [javax|jakarta].persistence.schema-generation.create-script-source}.</li>
 * <li>{@code [javax|jakarta].persistence.schema-generation.drop-source}.</li>
 * <li>{@code [javax|jakarta].persistence.schema-generation.drop-script-source}.</li>
 * </ul>
 * <p>By default, the object/relational metadata in the persistence unit is used to create the database artifacts.
 * You may also supply scripts used by the provider to create and delete the database artifacts.
 * The {@code [javax|jakarta].persistence.schema-generation.create-source} and
 * {@code [javax|jakarta].persistence.schema-generation.drop-source} properties control how the provider will
 * create or delete the database artifacts.</p>
 * @see StandardConnectorFactory#getSchemaGenerationCreate()
 * @see StandardConnectorFactory#withSchemaGenerationCreate(SchemaGenerationSource)
 * @see StandardConnectorFactory#getSchemaGenerationDrop()
 * @see StandardConnectorFactory#withSchemaGenerationDrop(SchemaGenerationSource)
 * @see #unspecified()
 * @see #metadata()
 * @see #script(String)
 * @see #metadataThenScript(String)
 * @see #scriptThenMetadata(String)
 * @see <a href="https://docs.oracle.com/javaee/7/tutorial/persistence-intro005.htm">Database Schema Creation</a>
 * @author Victor Williams Stafusa da Silva
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaGenerationSource {

    /**
     * The strategy for schema generation, if defined.
     * -- GETTER --
     * Provides what is the strategy for schema generation.
     * @return The strategy for schema generation or an empty string ({@code ""}) if there is none defined.
     */
    @NonNull
    private final String strategy;

    /**
     * The script for schema generation.
     * -- GETTER --
     * If the strategy for schema generation uses an user-provided script for creating or deleting database artifacts, this gives
     * the script location relative to the root of the persistence unit. For example, {@code "META-INF/sql/some-script.sql"}.
     * @return The location of the user-provided script or an empty string ({@code ""}) if there is none.
     */
    @NonNull
    private final String scriptPath;

    /**
     * Defines that the strategy for schema generation is undefined.
     * @return An object representing the strategy detailed above.
     */
    public static SchemaGenerationSource unspecified() {
        return new SchemaGenerationSource("", "");
    }

    /**
     * Defines that the strategy for schema generation is to use the object/relational metadata
     * in the application to create or delete the database artifacts.
     * @return An object representing the strategy detailed above.
     */
    public static SchemaGenerationSource metadata() {
        return new SchemaGenerationSource("metadata", "");
    }

    /**
     * Defines that the strategy for schema generation is to use a provided script for creating
     * or deleting the database artifacts.
     * @param scriptPath The location of the script relative to the root of the persistence unit.
     *     For example, {@code "META-INF/sql/some-script.sql"}.
     * @return An object representing the strategy detailed above.
     * @throws IllegalArgumentException If the {@code script} is {@code null}.
     */
    public static SchemaGenerationSource script(@NonNull String scriptPath) {
        return new SchemaGenerationSource("script", scriptPath);
    }

    /**
     * Defines that the strategy for schema generation is to use a combination of object/relational metadata,
     * then a user-provided script to create or delete the database artifacts.
     * @param scriptPath The location of the script relative to the root of the persistence unit.
     *     For example, {@code "META-INF/sql/some-script.sql"}.
     * @return An object representing the strategy detailed above.
     * @throws IllegalArgumentException If the {@code script} is {@code null}.
     */
    public static SchemaGenerationSource metadataThenScript(@NonNull String scriptPath) {
        return new SchemaGenerationSource("metadata-then-script", scriptPath);
    }

    /**
     * Defines that the strategy for schema generation is to use a combination of a user-provided script,
     * then the object/relational metadata to create or delete the database artifacts.
     * @param scriptPath The location of the script relative to the root of the persistence unit.
     *     For example, {@code "META-INF/sql/some-script.sql"}.
     * @return An object representing the strategy detailed above.
     * @throws IllegalArgumentException If the {@code script} is {@code null}.
     */
    public static SchemaGenerationSource scriptThenMetadata(@NonNull String scriptPath) {
        return new SchemaGenerationSource("script-then-metadata", scriptPath);
    }
}
