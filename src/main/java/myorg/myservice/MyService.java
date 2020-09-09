/*
 * Copyright 2020 Grzegorz Skorupa <g.skorupa at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package myorg.myservice;

import myorg.myservice.events.HelloEvent;
import myorg.myservice.events.MyEvent;
import org.cricketmsf.Event;
import org.cricketmsf.annotation.EventClassHook;
import org.cricketmsf.annotation.EventHook;
import org.cricketmsf.annotation.PortEventClassHook;
import org.cricketmsf.in.http.Result;
import org.cricketmsf.in.http.StandardResult;
import org.cricketmsf.services.MinimalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import myorg.myservice.out.MyOutIface;
import myorg.myservice.out.UserManagerIface;

public class MyService extends MinimalService {

    private static final Logger logger = LoggerFactory.getLogger(MyService.class);

    protected MyOutIface worker = null;
    protected UserManagerIface userManager = null;

    @EventHook(eventCategory = "*")
    public void processEvent(Event event) {
        logger.info(event.toLogString());
    }

    /**
     * The event from an inbound adapter can be processed here
     *
     * @param event
     * @return
     */
    @PortEventClassHook(className = "HelloEvent", procedureName = "sayHello")
    public Object handleGreeting(HelloEvent event) {

        HashMap<String, String> eventData = (HashMap) event.getData();
        String name = eventData.get("name");

        Result result = new StandardResult();

        // A new event type can be created and send to the microservices kernel
        // to fire additional business logic asynchronously
        dispatchEvent(new MyEvent().data(name));

        // If the dedicated adapter is not used to produce a Result object
        // then 
        //the name must be registered first
        if (!userManager.isRegistered(name)) {
            result.setCode(404);
            result.setData("unknown user name\n");
            result.setHeader("Content-type", "text/plain");
            return result;
        } else {
            String greetings = userManager.getGreeting(name, eventData.get("friendName"));
            result.setData(greetings);
            result.setHeader("Content-type", "text/plain");
            return result;
        }
    }

    @PortEventClassHook(className = "HelloEvent", procedureName = "addUser")
    public Object handleAddUser(HelloEvent event) {
        Result result = new StandardResult();
        HashMap<String, String> eventData = (HashMap) event.getData();
        String name = eventData.get("name");
        userManager.addUser(name);
        result.setData(String.format("User %1s added", name));
        result.setHeader("Content-type", "text/plain");
        return result;
    }

    @EventClassHook(className = "myorg.myservice.events.MyEvent")
    public void doSomething(MyEvent event) {
        worker.printOut(event.getData());
        logger.info("It's done for " + event.getData());
    }

    @Override
    public void getAdapters() {
        super.getAdapters();
        worker = (MyOutIface) getAdaptersMap().get("Worker");
        userManager = (UserManagerIface) getAdaptersMap().get("UserManager");
    }

    @Override
    public void runInitTasks() {
        super.runInitTasks();
    }
}
