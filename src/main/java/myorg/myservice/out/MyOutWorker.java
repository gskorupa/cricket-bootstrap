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
package myorg.myservice.out;

import java.util.HashMap;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author greg
 */
public class MyOutWorker extends OutboundAdapter implements Adapter, MyOutIface {
    private static final Logger logger = LoggerFactory.getLogger(MyOutWorker.class);
    private String myId = "";

    @Override
    public void printOut(String name) {
        logger.info(name+" handled by "+myId);
    }

    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
        myId=properties.getOrDefault("id", "");
    }
    
}
