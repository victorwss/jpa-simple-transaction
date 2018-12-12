package ninja.javahacker.jpasimpletransactions.properties;

import java.util.Map;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
public interface PersistenceProperties {

    public ProviderAdapter getProviderAdapter();

    public String getPersistenceUnitName();

    public Map<String, String> build();
}
