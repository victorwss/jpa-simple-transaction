package ninja.javahacker.jpasimpletransactions.config;

import ninja.javahacker.jpasimpletransactions.Connector;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
public interface ConnectorFactory {

    public String getPersistenceUnitName();

    public Connector connect();
}
