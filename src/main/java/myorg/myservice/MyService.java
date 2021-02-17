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
import org.cricketmsf.event.Event;
import org.cricketmsf.annotation.EventHook;
import org.cricketmsf.services.MinimalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import myorg.myservice.events.UserEvent;
import myorg.myservice.out.MyOutIface;
import myorg.myservice.out.UserManagerIface;
import org.cricketmsf.api.StandardResult;
import org.cricketmsf.event.Procedures;

public class MyService extends MinimalService {

    private static final Logger logger = LoggerFactory.getLogger(MyService.class);

    private MyOutIface worker = null;
    private UserManagerIface userManager = null;
    
    public MyService(){
        super();
        eventRouter=new MyEventRouter(this);
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
        // your code goes here
    }

    /**
     * @return the worker
     */
    public MyOutIface getWorker() {
        return worker;
    }

    /**
     * @return the userManager
     */
    public UserManagerIface getUserManager() {
        return userManager;
    }
}
