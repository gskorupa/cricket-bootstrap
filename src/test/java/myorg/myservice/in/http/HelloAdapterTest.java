package myorg.myservice.in.http;

import java.util.HashMap;
import org.cricketmsf.RequestObject;
import org.cricketmsf.event.ProcedureCall;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author greg
 */
public class HelloAdapterTest {
    
    public HelloAdapterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of loadProperties method, of class HelloAdapter.
     */
    //@Test
    public void testLoadProperties() {
        System.out.println("loadProperties");
        HashMap<String, String> properties = new HashMap<>();
        String adapterName = "";
        HelloAdapter instance = new HelloAdapter();
        instance.loadProperties(properties, adapterName);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of preprocess method, of class HelloAdapter.
     */
    //@Test
    public void testPreprocess() {
        System.out.println("preprocess");
        RequestObject request;
        ProcedureCall expResult;
        HashMap<String, String> properties = new HashMap<>();
        long rootEventId = 0L;
        
        // configure adapter instance
        properties.put("context", "/");
        HelloAdapter instance = new HelloAdapter();
        instance.loadProperties(properties, "HelloApi");
        
        // when GET request is invalid
        request = new RequestObject();
        request.method="GET";
        request.pathExt="";
        expResult = new ProcedureCall();
        ProcedureCall result = instance.preprocess(request, rootEventId);
        assertEquals(400, result.responseCode);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("TODO: walidacja parametrów w path nie działa");
    }

    /**
     * Test of defineApi method, of class HelloAdapter.
     */
    //@Test
    public void testDefineApi() {
        System.out.println("defineApi");
        HelloAdapter instance = new HelloAdapter();
        instance.defineApi();
        // If we require api to be defined then we can check it.
        // assert....
    }
    
}
