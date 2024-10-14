open module ninja.javahacker.test.jpasimpletransactions {
    requires ninja.javahacker.jpasimpletransactions.core;
    requires ninja.javahacker.jpasimpletransactions.hibernate;
    requires ninja.javahacker.jpasimpletransactions.eclipselink;
    requires ninja.javahacker.jpasimpletransactions.openjpa;
    requires ninja.javahacker.ninjadao;
    requires ninja.javahacker.mocker;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires org.hsqldb;
    requires org.hibernate.orm.core;
    uses ninja.javahacker.jpasimpletransactions.ProviderAdapter;
}