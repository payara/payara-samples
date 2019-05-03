package fish.payara.test.ejb.httpclient.client;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import fish.payara.test.ejb.httpclient.server.Bean;
import fish.payara.test.ejb.httpclient.server.BeanRemote;
import fish.payara.test.ejb.httpclient.server.EmployeeBean;
import fish.payara.test.ejb.httpclient.server.EmployeeServiceRemote;

/**
 * Client application to verify EJB over HTTP implementation.
 * 
 * To package the server create a jar with the model and server package in it and deploy the application named "test".
 */
public class Client {

    public static void main(String[] args) throws Exception {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "fish.payara.ejb.rest.client.RemoteEJBContextFactory");
        environment.put(Context.PROVIDER_URL, "http://localhost:8080/ejb-invoker");

        Context context = new InitialContext(environment);
        ejbByClassAndInterfaceName(context);
        ejbByClassName(context);
        ejbByInterfaceName(context);
    }

    private static void ejbByInterfaceName(Context context) {
        try {
            BeanRemote bean = (BeanRemote) context.lookup(BeanRemote.class.getName());
            System.out.println(bean.method());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void ejbByClassName(Context context) {
        try {
            BeanRemote bean = (BeanRemote) context.lookup("java:global/test/" + Bean.class.getSimpleName());
            System.out.println(bean.method());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void ejbByClassAndInterfaceName(Context context) {
        try {
            EmployeeServiceRemote bean = (EmployeeServiceRemote) context.lookup("java:global/test/"
                    + EmployeeBean.class.getSimpleName() + "!" + EmployeeServiceRemote.class.getName());
            System.out.println(bean.doAction());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
