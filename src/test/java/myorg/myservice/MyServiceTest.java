/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myorg.myservice;

import myorg.myservice.out.MyOutIface;
import myorg.myservice.out.UserManagerIface;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author greg
 */
public class MyServiceTest {
    
    public MyServiceTest() {
    }

    @Test
    public void testGetAdapters() {
        System.out.println("getAdapters");
        MyService instance = new MyService();
        instance.getAdapters();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    @Test
    public void testRunInitTasks() {
        System.out.println("runInitTasks");
        MyService instance = new MyService();
        instance.getAdapters();
        //instance.runInitTasks();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    @Test
    public void testGetWorker() {
        System.out.println("getWorker");
        MyService instance = new MyService();
        MyOutIface expResult = null;
        MyOutIface result = instance.getWorker();
        //assertEquals(expResult, result);
        //assertNotNull(result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    @Test
    public void testGetUserManager() {
        System.out.println("getUserManager");
        MyService instance = new MyService();
        UserManagerIface expResult = null;
        UserManagerIface result = instance.getUserManager();
        //assertEquals(expResult, result);
        //assertNotNull(result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
