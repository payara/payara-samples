package fish.payara.test.ejb.httpclient.server;

import javax.ejb.Remote;

import fish.payara.test.ejb.httpclient.model.CustomValue;

@Remote
public interface BeanRemote {
    CustomValue method();
}
