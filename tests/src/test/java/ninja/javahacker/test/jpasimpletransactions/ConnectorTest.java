package ninja.javahacker.test.jpasimpletransactions;

import java.util.List;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernatePersistenceProperties;
import org.hibernate.dialect.HSQLDialect;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ConnectorTest {
    @Test
    public void testSimpleConnect() throws Exception {
        HibernatePersistenceProperties p = new HibernatePersistenceProperties("test-1")
                .withDialect(HSQLDialect.class)
                .withDriver(JDBCDriver.class);
        var x = Connector.withoutXml(List.of(Fruit.class), p);
    }
}
