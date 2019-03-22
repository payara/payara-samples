/** Copyright Payara Services Limited **/
package fish.payara.samples.loginmodule.realm.custom;

import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;

import java.util.Enumeration;
import java.util.Properties;

import org.jvnet.hk2.annotations.Service;

import com.sun.enterprise.security.BaseRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;
import com.sun.enterprise.security.auth.realm.Realm;

/**
 * Realm wrapper for supporting Custom authentication.
 *
 */
@Service
public final class CustomRealm extends BaseRealm {

    public static final String AUTH_TYPE = "custom";

    @Override
    public synchronized void init(Properties props) throws BadRealmException, NoSuchRealmException {
        super.init(props);
        
        String jaasCtx = props.getProperty(Realm.JAAS_CONTEXT_PARAM);
        if (jaasCtx == null) {
            throw new BadRealmException("No jaax-ctx specified");
        }

        setProperty(Realm.JAAS_CONTEXT_PARAM, jaasCtx);
    }

    @Override
    public String getAuthType() {
        return AUTH_TYPE;
    }
   
    public String[] authenticate(String username, char[] password) {
        return new String[] {"g1"};
    }
    
    @Override
    public Enumeration<String> getGroupNames(String username) throws InvalidOperationException, NoSuchUserException {
        return enumeration(asList("g1"));
    }
    

}
