package myorg.myservice;

import myorg.myservice.events.HelloEvent;
import org.cricketmsf.in.http.StandardResult;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyServiceTest extends ServiceSetup {
    
    public MyServiceTest(){
        super();
    }

    @Test
    public void testGreetingHandler() {
        HelloEvent event = new HelloEvent("tester1", "");
        StandardResult result = (StandardResult) service.handleAddUser(event);
        Assert.assertEquals(200, result.getCode());
        result = (StandardResult) service.handleGreeting(event);
        Assert.assertEquals(200, result.getCode());
    }
    
    @Test
    public void testUserNotRegistered() {
        service.userManager.clear();
        HelloEvent event = new HelloEvent("tester1", "");
        StandardResult result = (StandardResult) service.handleGreeting(event);
        Assert.assertEquals(404, result.getCode());
    }
}
