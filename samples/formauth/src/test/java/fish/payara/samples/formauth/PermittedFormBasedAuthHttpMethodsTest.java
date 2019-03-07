package fish.payara.samples.formauth;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import fish.payara.samples.CliCommands;

/**
 * Verifies that the {@code fish.payara.permittedFormBasedAuthHttpMethods} system property has an effect.
 */
@RunWith(Arquillian.class)
public class PermittedFormBasedAuthHttpMethodsTest {

    @ArquillianResource
    private URL base;

    @Deployment(testable=false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.createFromZipFile(WebArchive.class, new File("src/test/resources/formauth.war"));
    }

    @BeforeClass
    public static void setUp() throws Exception {
        addContainerSystemProperty("fish.payara.permittedFormBasedAuthHttpMethods", "POST");
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        clearContainerSystemProperty("fish.payara.permittedFormBasedAuthHttpMethods", "POST");
    }

    @Test
    public void formAuthIsForbiddenForMethodsNotPermitted() throws Exception {
        assertFormAuthResponse(HttpURLConnection.HTTP_FORBIDDEN, "GET");
        assertFormAuthResponse(HttpURLConnection.HTTP_FORBIDDEN, "HEAD");
        assertFormAuthResponse(HttpURLConnection.HTTP_FORBIDDEN, "PUT");
    }

    @Test
    public void formAuthIsOkForMethodsPermitted() throws Exception {
        assertFormAuthResponse(HttpURLConnection.HTTP_OK, "POST");
    }

    private void assertFormAuthResponse(int expectedStatus, String usedHttpMethod)
            throws MalformedURLException, IOException, ProtocolException {
        URL url = new URL(base, "/formauth/j_security_check?j_username=foo&j_password=bar");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(usedHttpMethod);
        assertEquals(expectedStatus, connection.getResponseCode());
    }

    public static void addContainerSystemProperty(String key, String value) {
        CliCommands.payaraGlassFish(Arrays.asList("create-jvm-options", "-D" + key + "=\"" + value + "\""));
    }

    public static void clearContainerSystemProperty(String key, String value) {
        CliCommands.payaraGlassFish(Arrays.asList("delete-jvm-options", "-D" + key+ "=\"" + value + "\""));
    }
}
