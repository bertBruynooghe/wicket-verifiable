package org.xbrlz.wicket.verifiable.reflection;

import org.apache.wicket.util.lang.PropertyResolver;
import org.apache.wicket.util.lang.PropertyResolverConverter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.apache.wicket.util.lang.PropertyResolver.*;
import static org.xbrlz.wicket.verifiable.reflection.WicketMockFactory.*;

public class WicketMockFactoryTest {

    private PropertyResolverConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = Mockito.mock(PropertyResolverConverter.class);
    }

    @Test
    public void testInstantiation() throws Exception {
        Object mock = mock(Object.class);
        assertMockType(getValue("", mock), Object.class);
    }

    @Test
    public void testDuplicateInstantiation() throws Exception {
        Object mock = mock(Object.class);
        mock = mock(Object.class);
        assertMockType(getValue("", mock), Object.class);
    }

    @Test
    public void testHasGetter() throws Exception {
        Object mock = mock(GetterContainer.class);
        assertMockType(getValue("test", mock), FieldContainer.class);
    }

    @Test
    public void testHasField() throws Exception {
        Object mock = mock(FieldContainer.class);
        assertMockType(getValue("integer", mock), Integer.class);
    }

    @Test
    public void testNested() throws Exception {
        Object mock = mock(GetterContainer.class);
        assertMockType(getValue("test.integer", mock), Integer.class);
    }

    @Test
    public void testInterface() throws Exception {
        Object mock = mock(GetterContainerInterface.class);
        assertMockType(getValue("test.integer", mock), Integer.class);
    }

    @Test
    public void testArrayFieldContainerItem() throws Exception {
        Object mock = mock(ArrayContainer.class);
        assertMockType(getValue("array.0.test", mock), FieldContainer.class);
    }


    @Test
    public void testList() throws Exception {
        assertTrue("mock didn't return a list", mock(List.class) instanceof List);
    }

    @Test
    public void testListItem() throws Exception {
        Object mock = mock(List.class);
        String expression = "0.toString()";
        assertMockType(getValue(expression, mock), String.class);
    }

    @Test(expected = Test.None.class)
    public void testSetterOnWritableProperty() throws Exception {
        Object mock = mock(Date.class);
        PropertyResolver.setValue("seconds", mock, null, converter);
    }

    @Test
    public void testArrayType() throws Exception {
        Object mock = mock(int[].class);
        assertMockType(getValue("", mock), int[].class);
    }

    @Test
    @Ignore("we will implement this later")
    public void testListItemChild() throws Exception {
        Object mock = mock(TypedList.class);
        assertNotNull(getValue("0.test", mock));
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
