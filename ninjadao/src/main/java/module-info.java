import jakarta.persistence.EntityManager;

/**
 * Allows the creation of simple JPA Data Access Objects using the {@link EntityManager} and an annotation-based approach.
 */
@SuppressWarnings({
    "requires-automatic", "requires-transitive-automatic" // com.github.spotbugs.annotations
})
module ninja.javahacker.ninjadao {
    requires transitive jakarta.persistence;
    requires transitive ninja.javahacker.reifiedgeneric;
    requires transitive ninja.javahacker.jpasimpletransactions.core;
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    exports ninja.javahacker.ninjadao;
    opens ninja.javahacker.ninjadao;
}