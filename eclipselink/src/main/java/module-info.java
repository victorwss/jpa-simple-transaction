/**
 * Eclipselink provider for JPA Simple Transactions.
 */
module ninja.javahacker.jpasimpletransactions.eclipselink {
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    requires transitive ninja.javahacker.reifiedgeneric;
    requires transitive ninja.javahacker.jpasimpletransactions.core;
    requires transitive org.eclipse.persistence.jpa;
    exports ninja.javahacker.jpasimpletransactions.eclipselink;
    provides ninja.javahacker.jpasimpletransactions.ProviderAdapter
            with ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkAdapter;
}