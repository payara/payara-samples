package fish.payara.test.ejb.httpclient.server;

import java.io.Serializable;

import javax.ejb.Stateless;

import fish.payara.test.ejb.httpclient.model.CustomValue;

@Stateless
public class Bean implements BeanRemote, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public CustomValue method() {
        CustomValue res = new CustomValue();
        res.setValue(5);
        return res;
    }

}

