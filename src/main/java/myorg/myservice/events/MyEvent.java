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
package myorg.myservice.events;

import org.cricketmsf.Event;
import org.cricketmsf.event.EventDecorator;
import org.cricketmsf.event.EventIface;

public class MyEvent extends EventDecorator implements EventIface {
    
    private String data=null;

    public MyEvent() {
        super();
    }
    
    public MyEvent data(String newData){
        this.data=newData;
        return this;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }
    
}
