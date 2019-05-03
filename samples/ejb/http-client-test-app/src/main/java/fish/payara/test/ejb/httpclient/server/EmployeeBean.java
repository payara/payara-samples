package fish.payara.test.ejb.httpclient.server;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

@Stateless
public class EmployeeBean implements EmployeeServiceLocal, EmployeeServiceRemote {

    @Override
    public Map<String, String> doAction() {
        HashMap<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        return map;
    }
    
}
