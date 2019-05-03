package fish.payara.test.ejb.httpclient.server;
import java.util.Map;

import javax.ejb.Remote;


@Remote
public interface EmployeeServiceRemote {
  Map<String, String> doAction();
}
