/**
 * Simplifies the development of JPA applications without needing configuration files.
 */
module ninja.javahacker.jpasimpletransactions.core {
    requires transitive jakarta.persistence;
    requires transitive jakarta.inject;
    requires transitive java.sql;
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    requires transitive ninja.javahacker.reifiedgeneric;
    exports ninja.javahacker.jpasimpletransactions;
    exports ninja.javahacker.jpasimpletransactions.config;
    uses ninja.javahacker.jpasimpletransactions.ProviderAdapter;
}