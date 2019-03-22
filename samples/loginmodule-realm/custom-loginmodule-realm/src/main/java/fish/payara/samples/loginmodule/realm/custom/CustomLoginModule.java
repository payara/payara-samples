/** Copyright Payara Services Limited **/
package fish.payara.samples.loginmodule.realm.custom;

import javax.security.auth.login.LoginException;

import com.sun.enterprise.security.BasePasswordLoginModule;

/**
 * This class implement a Custom Login module for Payara.
 *
 */
public class CustomLoginModule extends BasePasswordLoginModule {

    /**
     * Perform authentication. Delegates to CustomRealm.
     *
     * @throws LoginException If login fails
     */
    @Override
    protected void authenticateUser() throws LoginException {
        CustomRealm customRealm = getRealm(CustomRealm.class, "Realm not found");
        
        if (_username == null || _username.length() == 0) {
            throw new LoginException("No username set");
        }

        String[] groups = customRealm.authenticate(_username, getPasswordChar());

        if (groups == null) { // JAAS behavior
            throw new LoginException("Login failed for " + _username);
        }

        commitUserAuthentication(groups);
    }
}
