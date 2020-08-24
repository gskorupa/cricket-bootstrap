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
import org.cricketmsf.services.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

public class Service extends BasicService {
    
    private static final Logger logger = LoggerFactory.getLogger(Service.class);
    
    private HashMap<String, String> users;

    @EventHook(eventCategory = "*")
    public void processEvent(Event event) {
        logger.info(event.toLogString());
    }

    @PortEventClassHook(className = "HelloEvent", procedureName = "sayHello")
    public Object handleGreeting(HelloEvent event) {
        Result result = new StandardResult();
       
        String name = event.getUserName();
        //the name must be registered first
        if(null==users.get(name)){
            result.setCode(404);
            result.setData("unknown user name\n");
            result.setHeader("Content-type", "text/plain");
            return result;        
        }
        String friendName= event.getFriendName();
        if(friendName.isBlank()){
            result.setData(String.format("Hello %1s!", name));
        }else{
            result.setData(String.format("Hello %1s! Greetings from %2s.", friendName,name));
        }
        result.setHeader("Content-type", "text/plain");
        dispatchEvent(new MyEvent().data(name));
        return result;
    }
    
    @PortEventClassHook(className = "HelloEvent", procedureName = "addUser")
    public Object handleAddUser(HelloEvent event) {
        Result result = new StandardResult();
        String name = event.getUserName();
        users.put(name, name);
        result.setData(String.format("User %1s added", name));
        result.setHeader("Content-type", "text/plain");
        return result;
    }
   
    @EventClassHook(className = "myorg.myservice.events.MyEvent")
    public void doSomething(MyEvent event) {
        logger.info("It's done for "+event.getData());
    }

    @Override
    public void getAdapters() {
        super.getAdapters();
    }
    
    @Override
    public void runInitTasks(){
        super.runInitTasks();
        users=new HashMap<>();
    }
}
