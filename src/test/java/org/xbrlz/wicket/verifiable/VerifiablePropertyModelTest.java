package org.xbrlz.wicket.verifiable;

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.*;
import static org.xbrlz.wicket.verifiable.VerifiablePropertyModel.*;

public class VerifiablePropertyModelTest {


    @Before
    public void setUp() throws Exception {
        new WicketTester();
    }

    @After
    public void tearDown() throws Exception {
        System.setProperty("wicket." + Application.CONFIGURATION, Application.DEVELOPMENT);
    }

    @Test
    public void testDefaultPropertyModel() throws Exception {
        IModel<String> model = newPropertyModel("test", String.class);
        assertEquals("test", model.getObject());
    }

    @Test(expected = WicketRuntimeException.class)
    public void testValidateInDevelopmentMode() throws Exception {
        System.setProperty("wicket." + Application.CONFIGURATION, Application.DEVELOPMENT);
        newPropertyModel(new Object(), Object.class).withExpression("test", Object.class);
    }

    @Test(expected = Test.None.class)
    public void testDoesNotValidateInDeploymentMode() throws Exception {
        System.setProperty("wicket." + Application.CONFIGURATION, Application.DEPLOYMENT);
        newPropertyModel(new Object(), Object.class).withExpression("test", Object.class);
    }

    @Test
    public void testNestedPropertyModel() throws Exception {
        IModel<String> model = newPropertyModel(5, Integer.class)
                .withExpression("toString()", String.class);
        assertEquals("5", model.getObject());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNonWritablePropertyModel() throws Exception {
        IModel<String> model = newPropertyModel(5, Integer.class)
                .withExpression("toString()", String.class);
        model.setObject("4");
    }

    @Test
    public void testWritablePropertyModel() throws Exception {
        IModel<Integer> model = newPropertyModel(new Date(0), Date.class)
                .withExpression("seconds", Integer.class)
                .asWritable();
        model.setObject(5);
        assertEquals(new Integer(5), model.getObject());
    }

    @Test
    public void testArrayPropertyModel() throws Exception {
        int[] expectedValue = new int[10];
        IModel<int[]> model = newPropertyModel(expectedValue, int[].class);
        assertEquals(expectedValue, model.getObject());
    }

    @Test
    public void testArrayItemPropertyModel() throws Exception {
        int[] expectedValue = new int[10];
        IModel<Integer> model = newPropertyModel(expectedValue, int[].class)
                .withExpression("0", Integer.class).asWritable();
        model.setObject(5);
        assertEquals(new Integer(5), model.getObject());
    }

}