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

public class Service extends BasicService {
    
    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    @EventHook(eventCategory = "*")
    public void processEvent(Event event) {
        logger.info(event.toLogString());
    }

    @PortEventClassHook(className = "HelloEvent", procedureName = "helloProcedure")
    public Object handleGreeting(HelloEvent event) {
        Result result = new StandardResult();
        String name = event.getClientName();
        result.setData(String.format("Hello %1s", name));
        result.setHeader("Content-type", "text/plain");
        dispatchEvent(new MyEvent().data(name));
        return result;
    }
   
    @EventClassHook(className = "myorg.myservice.events.MyEvent")
    public void doSomething(MyEvent event) {
        logger.info("It's done for "+event.getData());
    }

    @Override
    public void getAdapters() {
    }
}
