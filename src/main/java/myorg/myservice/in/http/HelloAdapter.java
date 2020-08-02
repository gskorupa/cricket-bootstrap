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
 */
package myorg.myservice.in.http;

import java.util.HashMap;
import myorg.myservice.events.HelloEvent;
import org.cricketmsf.RequestObject;
import org.cricketmsf.event.ProcedureCall;
import org.cricketmsf.in.http.HttpPortedAdapter;

/**
 *
 * @author Grzegorz Skorupa <g.skorupa at gmail.com>
 */
public class HelloAdapter extends HttpPortedAdapter {

    public static int PARAM_NOT_FOUND = 1;

    public HelloAdapter() {
        super();
    }

    @Override
    protected ProcedureCall preprocess(RequestObject request, long rootEventId) {
        switch (request.method.toUpperCase()) {
            case "GET":
                return preprocessGet(request);
            default:
                HashMap<String, Object> err = new HashMap<>();
                err.put("code", 405); //code<100 || code >1000
                err.put("message", String.format("method %1s not allowed",request.method.toUpperCase()));
                return ProcedureCall.respond(405, err);
        }
    }

    private ProcedureCall preprocessGet(RequestObject request) {
        // validation and translation 
        String name = (String) request.parameters.getOrDefault("name", "");
        if (name.isEmpty() || !"world".equalsIgnoreCase(name)) {
            HashMap<String, Object> err = new HashMap<>();
            err.put("code", PARAM_NOT_FOUND); //code<100 || code >1000
            // http status codes can be used directly:
            // err.put("code", 404);
            err.put("message", "unknown name or name parameter not found (must be 'world')");
            return ProcedureCall.respond(PARAM_NOT_FOUND, err);
        }
        // forwarding to the service method
        return ProcedureCall.forward(new HelloEvent(name), "helloProcedure");
    }

}
