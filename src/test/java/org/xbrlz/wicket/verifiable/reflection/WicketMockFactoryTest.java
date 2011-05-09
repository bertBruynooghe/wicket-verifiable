package org.xbrlz.wicket.verifiable.reflection;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.xbrlz.wicket.verifiable.reflection.WicketMockFactory.*;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("org.xbrlz.wicket.verifiable.testApplicationContext.xml")
public class WicketMockFactoryTest {
//    @Autowired
//    WebApplication application;

    @Test
    public void testInstantiation() throws Exception {
        IModel model = new PropertyModel<Object>(mock(Object.class), "");
        assertMockType(model.getObject(), Object.class);
    }

    @Test
    public void testDuplicateInstantiation() throws Exception {
        IModel model = new PropertyModel<Object>(mock(Object.class), "");
        model = new PropertyModel<Object>(mock(Object.class), "");
        assertMockType(model.getObject(), Object.class);
    }

    @Test
    public void testHasGetter() throws Exception {
        IModel model = new PropertyModel<Object>(mock(GetterContainer.class), "test");
        assertMockType(model.getObject(), FieldContainer.class);
    }

    @Test
    public void testHasField() throws Exception {
        IModel model = new PropertyModel<Object>(mock(FieldContainer.class), "integer");
        assertMockType(model.getObject(), Integer.class);
    }

    @Test
    public void testNested() throws Exception {
        IModel model = new PropertyModel<Object>(mock(GetterContainer.class), "test.integer");
        assertMockType(model.getObject(), Integer.class);
    }

    @Test
    public void testInterface() throws Exception {
        IModel model = new PropertyModel<Object>(mock(GetterContainerInterface.class), "test.integer");
        assertMockType(model.getObject(), Integer.class);
    }

    @Test
    public void testArrayFieldContainerItem() throws Exception {
        IModel model = new PropertyModel<Object>(mock(ArrayContainer.class), "array.0.test");
        assertMockType(model.getObject(), FieldContainer.class);
    }


    @Test
    public void testList() throws Exception {
        assertTrue("mock didn't return a list", mock(List.class) instanceof List);
    }

    @Test
    public void testListItem() throws Exception {
        IModel model = new PropertyModel<Object>(mock(List.class), "0.toString()");
        assertMockType(model.getObject(), String.class);
    }

//    @Test(expected = Test.None.class)
//    public void testSetter() throws Exception {
//        new WicketTester(application);
//        IModel model = new PropertyModel<Object>(mock(Date.class), "seconds");
//        model.setObject(5);
//    }

    @Test
    public void testArrayType() throws Exception {
        IModel model = new PropertyModel<Object>(mock(int[].class), "");
        assertMockType(model.getObject(), int[].class);
    }

    @Test
    @Ignore("we will implement this later")
    public void testListItemChild() throws Exception {
        IModel model = new PropertyModel<Object>(mock(TypedList.class), "0.test");
        assertNotNull(model.getObject());
    }

    public static interface GetterContainerInterface {
        FieldContainer getTest();
    }

    public static class GetterContainer {
        public FieldContainer getTest() {
            return null;
        }
    }

    public static class FieldContainer {
        public Integer integer = null;
    }

    public static class ArrayContainer {
        public GetterContainer[] array;
    }

    public static class TypedList extends ArrayList<GetterContainer> {
    }

}
