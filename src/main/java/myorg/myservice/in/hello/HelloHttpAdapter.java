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
package myorg.myservice.in.hello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import myorg.myservice.events.HelloEvent;
import myorg.myservice.events.UserEvent;
import org.cricketmsf.RequestObject;
import org.cricketmsf.event.ProcedureCall;
import org.cricketmsf.in.http.HttpPortedAdapter;
import org.cricketmsf.in.openapi.BodyContent;
import org.cricketmsf.in.openapi.Operation;
import org.cricketmsf.in.openapi.Parameter;
import org.cricketmsf.in.openapi.ParameterLocation;
import org.cricketmsf.in.openapi.RequestBody;
import org.cricketmsf.in.openapi.Response;
import org.cricketmsf.in.openapi.Schema;
import org.cricketmsf.in.openapi.SchemaProperty;
import org.cricketmsf.in.openapi.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Grzegorz Skorupa <g.skorupa at gmail.com>
 */
public class HelloHttpAdapter extends HttpPortedAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HelloHttpAdapter.class);

    public static int PARAM_NOT_FOUND = 1;

    public HelloHttpAdapter() {
        super();
    }

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
        // "context" parameter is read by HttpPortedAdapter class
    }

    @Override
    protected ProcedureCall preprocess(RequestObject request, long rootEventId) {
        switch (request.method) {
            case "GET":
                return preprocessGet(request);
            case "POST":
                return preprocessPost(request);
            case "OPTIONS":
                return ProcedureCall.toRespond(200, null);
            default:
                HashMap<String, Object> err = new HashMap<>();
                err.put("code", 405); //code<100 || code >1000
                err.put("message", String.format("method %1s not allowed", request.method));
                return ProcedureCall.toRespond(405, err);
        }
    }

    private ProcedureCall preprocessGet(RequestObject request) {
        try {
            /*
        Validating request
             */
            ProcedureCall validationResult = validateGet(request);
            if (null != validationResult) {
                return validationResult;
            }

            /*
        Getting request parameters
             */
            String[] pathParams = pathParams = request.pathExt.split("/");
            String name = pathParams.length > 0 ? pathParams[0] : "";
            String friendName = (String) request.parameters.getOrDefault("friend", "");

            /*
        Forwarding dedicated event type to the service
             */
            return ProcedureCall.toForward(new HelloEvent(name, friendName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ProcedureCall preprocessPost(RequestObject request) {
        // validation and translation 
        /*
        Validating request
         */
        ProcedureCall validationResult = validatePost(request);
        if (null != validationResult) {
            return validationResult;
        }

        // the name parameter is required
        String name = (String) request.parameters.getOrDefault("name", "");
        String friend = (String) request.parameters.getOrDefault("friend", "");
        // forwarding dedicated event type to the service
        return ProcedureCall.toForward(new UserEvent(name, friend));
    }

    private ProcedureCall validatePost(RequestObject request) {
        return null;
    }

    private ProcedureCall validateGet(RequestObject request) {
        HashMap<String, Object> err = new HashMap<>();

        /*
        * Validation using the API definition
         */
        // required path params validation
        ArrayList<String> pathParamsList = new ArrayList<>();
        Collections.addAll(pathParamsList, request.pathExt.split("/"));
        if (pathParamsList.get(0).isBlank()) {
            pathParamsList.remove(0);
        }
        ArrayList<Parameter> requiredPathParams = getParams("GET", true, ParameterLocation.path);
        for (int i = pathParamsList.size(); i < requiredPathParams.size(); i++) {
            err.put("code", 400);
            err.put("message" + i, String.format("path parameter '%1s' not found", requiredPathParams.get(i).getName()));
        }
        if (err.size() > 0) {
            return ProcedureCall.toRespond(400, err);
        }

        // required query params validation
        /*
        check 
         */
 /*
        TODO
         */
 /* End of API validation */

 /*
        * Custom validation example
        * The second parameter should be in query and it's optional
        * but we decide that value "world" is not allowed
         */
        //String friend = (String) request.parameters.getOrDefault("friend", "");
        //if ("world".equalsIgnoreCase(friend)) {
        //    err.put("code", 400);
        //    err.put("message", String.format("friend value '%1s' is not allowed", "world"));
        //    return ProcedureCall.respond(400, err);
        //}
        return null;
    }

    /**
     * The method provides API documentation for this adapter.
     */
    @Override
    public void defineApi() {
        // GET request definition
        Operation getOp = new Operation("GET")
                .tag("hello")
                .description("get greetings")
                .summary("example get method")
                .pathModifier("/{name}")
                .parameter(
                        new Parameter(
                                "name",
                                ParameterLocation.path,
                                true,
                                "User name.",
                                new Schema(SchemaType.string)
                        )
                )
                .parameter(
                        new Parameter(
                                "friend",
                                ParameterLocation.query,
                                false,
                                "The name of the friend you want to send greetings to.")
                )
                .response(new Response("200").content("text/plain").description("response"))
                .response(new Response("400").description("Invalid request parameters "))
                .response(new Response("404").description("User name not found"));
        addOperationConfig(getOp);

        // POST request definition
        Operation postOp = new Operation("POST")
                .tag("hello")
                .description("registering user name")
                .summary("example post method")
                .response(new Response("200").content("text/plain").description("user name registered"))
                .response(new Response("400").description("Invalid request parameters"));

        SchemaProperty schemaProperty1 = new SchemaProperty("name", SchemaType.string, null, "");
        Schema schema = new Schema(SchemaType.object).property(schemaProperty1);
        BodyContent bodyContent = new BodyContent("application/x-www-form-urlencoded", schema);
        RequestBody body = new RequestBody(bodyContent, true, "description");
        postOp.body(body);
        addOperationConfig(postOp);
    }

}
