open module ninja.javahacker.jpasimpletransactions.test {
    requires static lombok;
    requires static com.github.spotbugs.annotations;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires org.hsqldb;
    requires ninja.javahacker.reifiedgeneric;
    requires ninja.javahacker.mocker;
    requires ninja.javahacker.jpasimpletransactions.core;
    requires ninja.javahacker.jpasimpletransactions.openjpa;
    requires ninja.javahacker.jpasimpletransactions.hibernate;
    requires ninja.javahacker.jpasimpletransactions.eclipselink;
    requires ninja.javahacker.ninjadao;
    uses ninja.javahacker.jpasimpletransactions.ProviderAdapter;
}