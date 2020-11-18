package pl.net.nmg.datain.events;

import org.cricketmsf.event.Event;


public class DataReceived extends Event {
    
    private String data=null;

    public DataReceived() {
        super();
    }
    
    public DataReceived data(String newData){
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
