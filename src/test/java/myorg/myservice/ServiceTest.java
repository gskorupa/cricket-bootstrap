/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package myorg.myservice;

import myorg.myservice.events.HelloEvent;
import org.cricketmsf.Event;
import org.cricketmsf.in.http.StandardResult;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class ServiceTest {

    @Test
    public void testGreetingHandler() {
        final Service service = new Service();
        HelloEvent event = new HelloEvent("tester");
        StandardResult result = (StandardResult) service.handleGreeting(event);
        assertNotNull("app should have a greeting", result);
    }
}
