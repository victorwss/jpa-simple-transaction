/**
 * OpenJPA provider for JPA Simple Transactions.
 */
@SuppressWarnings({ "requires-automatic", "requires-transitive-automatic" })
module ninja.javahacker.jpasimpletransactions.openjpa {
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    requires transitive ninja.javahacker.reifiedgeneric;
    requires transitive ninja.javahacker.jpasimpletransactions.core;
    requires openjpa;
    exports ninja.javahacker.jpasimpletransactions.openjpa;
    provides ninja.javahacker.jpasimpletransactions.ProviderAdapter
            with ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaAdapter;
}