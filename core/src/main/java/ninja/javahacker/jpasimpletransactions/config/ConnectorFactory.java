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
     * @throws WrongProviderException If the provider is not capable of instantiating a connector.
     */
    public Connector connect() throws WrongProviderException;

    /**
     * Thrown when a provider is not capable of instantiating a connector.
     * @author Victor Williams Stafusa da Silva
     */
    public static class WrongProviderException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        /**
         * Sole constructor.
         */
        public WrongProviderException() {
        }
    }
}
