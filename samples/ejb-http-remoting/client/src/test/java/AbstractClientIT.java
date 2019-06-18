import java.util.Arrays;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import fish.payara.ejb.http.protocol.SerializationType;
import fish.payara.samples.ejbhttp.api.RemoteService;
import fish.payara.samples.ejbhttp.client.RemoteConnector;

@RunWith(Parameterized.class)
public abstract class AbstractClientIT {

    protected RemoteService remoteService;

    @Parameters(name = "{0}")
    public static Iterable<String> connectors() {
        return Arrays.asList(RemoteConnector.values()).stream().map(
                item -> item.name()).collect(Collectors.toList());
    }

    @Parameter
    public String connectorName;

    @Before
    public void lookup() throws NamingException {
        remoteService = getConnector().lookup("java:global/server-app/RemoteServiceBean");
    }

    protected final RemoteConnector getConnector() {
        return RemoteConnector.valueOf(connectorName);
    }

    protected final boolean isJavaSerialization() {
        return getSerializationType() == SerializationType.JAVA;
    }

    protected final SerializationType getSerializationType() {
        return getConnector().getSerializationType();
    }

    protected final int getVersion() {
        return getConnector().getVersion();
    }
}
