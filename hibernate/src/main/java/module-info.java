/**
 * Hibernate provider for JPA Simple Transactions.
 */
module ninja.javahacker.jpasimpletransactions.hibernate {
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    requires transitive ninja.javahacker.reifiedgeneric;
    requires transitive ninja.javahacker.jpasimpletransactions.core;
    requires transitive org.hibernate.orm.core;
    exports ninja.javahacker.jpasimpletransactions.hibernate;
    provides ninja.javahacker.jpasimpletransactions.ProviderAdapter
            with ninja.javahacker.jpasimpletransactions.hibernate.HibernateAdapter;
}