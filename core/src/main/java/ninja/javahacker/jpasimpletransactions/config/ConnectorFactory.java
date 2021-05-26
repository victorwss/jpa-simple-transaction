package ninja.javahacker.jpasimpletransactions.config;

import ninja.javahacker.jpasimpletransactions.Connector;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
public interface ConnectorFactory {

    /**
     * Gets the persistence unit's name.
     * @return The persistence unit's name.
     */
    public String getPersistenceUnitName();

    /**
     * Creates a new {@link Connector}.
     * @return A new {@link Connector}.
     */
    public Connector connect();
}
