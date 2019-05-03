package fish.payara.test.ejb.httpclient.server;


import java.util.Map;

import javax.ejb.Local;

@Local
public interface EmployeeServiceLocal {
    Map<String, String> doAction();


}
