/*
 * Copyright 2019 Grzegorz Skorupa <g.skorupa at gmail.com>.
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
 */
package org.cricketmsf.services;

import org.cricketmsf.Event;
import org.cricketmsf.Kernel;
import org.cricketmsf.RequestObject;
import java.util.HashMap;
import org.cricketmsf.annotation.EventHook;
import org.cricketmsf.annotation.HttpAdapterHook;
import org.cricketmsf.event.EventMaster;
import org.cricketmsf.exception.EventException;
import org.cricketmsf.exception.QueueException;
import org.cricketmsf.in.http.EchoHttpAdapterIface;
import org.cricketmsf.in.http.HtmlGenAdapterIface;
import org.cricketmsf.in.http.HttpAdapter;
import org.cricketmsf.in.http.ParameterMapResult;
import org.cricketmsf.in.http.StandardResult;
import org.cricketmsf.in.queue.SubscriberIface;
import org.cricketmsf.in.scheduler.SchedulerIface;
import org.cricketmsf.out.db.KeyValueDBException;
import org.cricketmsf.out.db.KeyValueDBIface;
import org.cricketmsf.out.file.FileReaderAdapterIface;
import org.cricketmsf.out.log.LoggerAdapterIface;

/**
 * EchoService
 *
 * @author greg
 */
public class BasicService extends Kernel {

    // standard adapters
    LoggerAdapterIface logAdapter = null;
    EchoHttpAdapterIface echoAdapter = null;
    KeyValueDBIface database = null;
    SchedulerIface scheduler = null;
    HtmlGenAdapterIface htmlAdapter = null;
    FileReaderAdapterIface fileReader = null;
    SubscriberIface queueSubscriber = null;
    //local project

    @Override
    public void getAdapters() {
        // standard Cricket adapters
        logAdapter = (LoggerAdapterIface) getRegistered("Logger");
        echoAdapter = (EchoHttpAdapterIface) getRegistered("Echo");
        database = (KeyValueDBIface) getRegistered("Database");
        scheduler = (SchedulerIface) getRegistered("Scheduler");
        htmlAdapter = (HtmlGenAdapterIface) getRegistered("WWWService");
        fileReader = (FileReaderAdapterIface) getRegistered("FileReader");
        queueSubscriber = (SubscriberIface) getRegistered("QueueSubscriber");
        // optional
    }

    @Override
    public void runInitTasks() {
        // we should register event categories used by this service
        try {
            EventMaster.registerEventCategories(new Event().getCategories(), Event.class.getName());
        } catch (EventException ex) {
            ex.printStackTrace();
            shutdown();
        }
        try {
            if(null!=queueSubscriber){
                queueSubscriber.init();
            }
        } catch (QueueException ex) {
        }
        try {
            database.addTable("webcache", 100, false);
        } catch (KeyValueDBException e) {
        }
        
    }

    @Override
    public void runFinalTasks() {
        //System.out.println(printStatus());
    }

    @Override
    public void runOnce() {
        super.runOnce();
        Kernel.getInstance().dispatchEvent(Event.logInfo("BasicService.runOnce()", "executed"));
    }

    /**
     * Process requests from simple web server implementation given by
     * HtmlGenAdapter access web web resources
     *
     * @param event
     * @return ParameterMapResult with the file content as a byte array
     */
    @HttpAdapterHook(adapterName = "WWWService", requestMethod = "GET")
    public Object doGet(Event event) {
        try {
            RequestObject request = event.getRequest();
            if ("true".equalsIgnoreCase(((HttpAdapter) Kernel.getInstance().getAdaptersMap().get("WWWService")).getProperty("dump-request"))) {
                System.out.println(HttpAdapter.dumpRequest(request));
            }
            ParameterMapResult result
                    = (ParameterMapResult) fileReader
                            .getFile(request, htmlAdapter.useCache() ? database : null, "webcache");
            // caching policy 
            result.setMaxAge(120);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @HttpAdapterHook(adapterName = "Test", requestMethod = "*")
    public Object doTest(Event event) {
        try {
            RequestObject request = event.getRequest();
            if ("true".equalsIgnoreCase(((HttpAdapter) Kernel.getInstance().getAdaptersMap().get("Test")).getProperty("dump-request"))) {
                System.out.println(HttpAdapter.dumpRequest(request));
            }
            StandardResult result = new StandardResult();
            result.setData("Test OK");
            Kernel.getInstance().dispatchEvent(new Event(this.getName(), "TEST", "", null, ""));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @HttpAdapterHook(adapterName = "StatusService", requestMethod = "GET")
    public Object handleStatusRequest(Event requestEvent) {
        StandardResult result = new StandardResult();
        result.setCode(HttpAdapter.SC_OK);
        result.setData(reportStatus());
        return result;
    }

    @HttpAdapterHook(adapterName = "Echo", requestMethod = "*")
    public Object doGetEcho(Event requestEvent) {
        return sendEcho(requestEvent.getRequest());
    }

    @EventHook(eventCategory = Event.CATEGORY_LOG)
    @EventHook(eventCategory = Event.CATEGORY_HTTP_LOG)
    @EventHook(eventCategory = "Category-Test")
    public void logEvent(Event event) {
        logAdapter.log(event);
    }

    @EventHook(eventCategory = "*")
    public void processEvent(Event event) {
        if (event.getTimePoint() != null) {
            scheduler.handleEvent(event);
        } else {
            Kernel.getInstance().dispatchEvent(Event.logInfo("Event category "+event.getCategory()+" is not handled by BasicService. Payload: ", event.getPayload().toString()));
        }
    }

    public Object sendEcho(RequestObject request) {
        StandardResult r = new StandardResult();
        r.setCode(HttpAdapter.SC_OK);
        try {
            if (!echoAdapter.isSilent()) {
                // with echo counter
                Long counter;
                counter = (Long) database.get("counters", "echo.count", new Long(0));
                counter++;
                database.put("counters", "echo.count", counter);
                HashMap<String, Object> data = new HashMap<>(request.parameters);
                data.put("service.id", getId());
                data.put("service.uuid", getUuid().toString());
                data.put("service.name", getName());
                data.put("request.method", request.method);
                data.put("request.pathExt", request.pathExt);
                data.put("echo.counter", database.get("counters", "echo.count"));
                if (data.containsKey("error")) {
                    int errCode = HttpAdapter.SC_INTERNAL_SERVER_ERROR;
                    try {
                        errCode = Integer.parseInt((String) data.get("error"));
                    } catch (Exception e) {
                    }
                    r.setCode(errCode);
                    data.put("error", "error forced by request");
                }
                r.setData(data);
                r.setHeader("x-echo-greeting", "hello");
            } else {
                Kernel.getInstance().dispatchEvent(Event.logFine("BasicService", "echo service is silent"));
            }
        } catch (KeyValueDBException e) {
            Kernel.getInstance().dispatchEvent(Event.logSevere(this.getClass().getSimpleName(), e.getMessage()));
        }
        return r;
    }
}
