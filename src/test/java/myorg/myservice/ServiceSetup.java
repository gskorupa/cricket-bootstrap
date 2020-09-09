package myorg.myservice;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.cricketmsf.Runner;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;

/**
 *
 * @author greg
 */
public class ServiceSetup {

    protected static MyService service;
    protected static SSLContext sslContext;
    protected static final String serviceURL = "http://localhost:7770";
    protected static final String erconetURL = "http://localhost:8080";

    @BeforeClass
    public static void setup() {
        //
        final Properties sysProps = System.getProperties();
        sysProps.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        //sysProps.setProperty("jdk.internal.httpclient.debug", Boolean.TRUE.toString());
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            }, new SecureRandom());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            fail(e.getMessage());
        }
        serviceStart();
    }

    public static void serviceStart() {
        String[] args = {"-r", "-s", "MyService1"};
        service = (MyService) Runner.getService(args);
        while (!service.isInitialized()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @AfterClass
    public static void shutdown() {
        try {
            service.shutdown();
        } catch (Exception e) {

        }
    }

}
