/**
 * Simplifies the development of JPA applications without needing configuration files.
 */
@SuppressWarnings({ "requires-automatic", "requires-transitive-automatic" })
module ninja.javahacker.jpasimpletransactions.core {
    requires transitive java.persistence;
    requires transitive java.sql;
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    requires transitive ninja.javahacker.reifiedgeneric;
    exports ninja.javahacker.jpasimpletransactions;
    exports ninja.javahacker.jpasimpletransactions.config;
}