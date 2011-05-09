package org.xbrlz.wicket.verifiable;

import org.apache.wicket.injection.ConfigurableInjector;
import org.apache.wicket.injection.IFieldValueFactory;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static junit.framework.Assert.*;
import static org.xbrlz.wicket.verifiable.VerifiablePropertyModel.*;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/org/xbrlz/wicket/verifiable/testApplicationContext.xml")
public class VerifiablePropertyModelTest {

    @Test
    public void testDefaultPropertyModel() throws Exception {
        new WicketTester();
        InjectorHolder.setInjector(new ConfigurableInjector() {
            @Override
            protected IFieldValueFactory getFieldValueFactory() {
                return null;
            }
        });
        IModel<String> model = newPropertyModel("test", String.class);
        assertEquals("test", model.getObject());
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