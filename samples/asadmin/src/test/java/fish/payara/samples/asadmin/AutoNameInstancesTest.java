package fish.payara.samples.asadmin;

import fish.payara.samples.ServerOperations;
import org.glassfish.embeddable.CommandResult;
import org.junit.BeforeClass;
import org.junit.Test;

public class AutoNameInstancesTest extends AsadminTest {

    private static String domainName = "domain1";

    @BeforeClass
    public static void setup() {
        // Get domain name
        domainName = ServerOperations.getPayaraDomainFromServer();
    }

    @Test
    public void testInstanceNameConflict() {
        String conflictInstanceName = "Scrumptious-Swordfish";
        // Create expected conflict if it doesn't already exist.
        CommandResult result = asadmin("list-instances", "-t", "--nostatus");
        if (!result.getOutput().contains(conflictInstanceName)) {
            asadmin("create-instance",
                    "--node", "localhost-" + domainName,
                    conflictInstanceName);
        }

        result = asadmin("create-instance",
                "--autoname", "true",
                "--node", "localhost-" + domainName,
                "-T",
                conflictInstanceName);
        assertSuccess(result);

        // Cleanup
        String generatedInstanceName = result.getOutput();
        asadmin("delete-instance", conflictInstanceName);
        asadmin("delete-instance", generatedInstanceName);
    }

    @Test
    public void testGenerateInstanceName() {
        CommandResult result = asadmin("create-instance",
                "-a",
                "--node", "localhost-" + domainName,
                "--extraTerse", "true");
        assertSuccess(result);

        // Cleanup
        String generatedInstanceName = result.getOutput();
        asadmin("delete-instance", generatedInstanceName);
    }

}
