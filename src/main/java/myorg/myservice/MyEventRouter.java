package myorg.myservice;

import java.util.HashMap;
import myorg.myservice.events.HelloEvent;
import myorg.myservice.events.MyEvent;
import myorg.myservice.events.UserEvent;
import org.cricketmsf.annotation.EventHook;
import org.cricketmsf.api.StandardResult;
import org.cricketmsf.event.Event;
import org.cricketmsf.event.Procedures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author greg
 */
public class MyEventRouter {

    private static final Logger logger = LoggerFactory.getLogger(MyEventRouter.class);
    
    private MyService service;
    
    public MyEventRouter(MyService service){
        this.service=service;
    }

        @EventHook(className="", procedure = Procedures.DEFAULT)
    public void processEvent(Event event) {
        logger.info(event.toString());
    }

    /**
     * An event from inbound adapter can be processed here
     *
     * @param event
     * @return
     */
    @EventHook(className = "myorg.myservice.events.HelloEvent", procedure = Procedures.DEFAULT)
    public Object handleGreeting(HelloEvent event) {

        HashMap<String, String> eventData = (HashMap) event.getData();
        String name = eventData.get("name");

        StandardResult result = new StandardResult();

        // A new event type can be created and send to the microservices kernel
        // to fire additional business logic asynchronously
        service.dispatchEvent(new MyEvent().data(name));

        // If the dedicated adapter is not used to produce a Result object
        // then 
        //the name must be registered first
        if (!service.getUserManager().isRegistered(name)) {
            result.setCode(404);
            result.setData("unknown user name\n");
            result.setHeader("Content-type", "text/plain");
            return result;
        } else {
            String greetings = service.getUserManager().getGreeting(name, eventData.get("friendName"));
            result.setData(greetings);
            result.setHeader("Content-type", "text/plain");
            return result;
        }
    }

    @EventHook(className = "myorg.myservice.events.UserEvent", procedure = Procedures.DEFAULT)
    public Object handleAddUser(UserEvent event) {
        StandardResult result = new StandardResult();
        HashMap<String, String> eventData = (HashMap) event.getData();
        String name = eventData.get("name");
        service.getUserManager().addUser(name);
        result.setData(String.format("User %1s added", name));
        result.setHeader("Content-type", "text/plain");
        return result;
    }

    @EventHook(className = "myorg.myservice.events.MyEvent", procedure=Procedures.DEFAULT)
    public void doSomething(MyEvent event) {
        service.getWorker().printOut(event.getData());
        logger.info("It's done for " + event.getData());
    }


}
