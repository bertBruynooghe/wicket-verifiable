package org.xbrlz.wicket.verifiable.reflection;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.xbrlz.wicket.verifiable.reflection.MockType.*;
import static org.xbrlz.wicket.verifiable.reflection.ValueSource.*;

public class ValueSourceTest {

    private static final String OBJECT_CLASS_NAME = Object.class.getName();
    private static final String WICKET_DATA_STUB_FACTORY_NAME = WicketMockFactory.class.getName();
    private static final String FINAL_CLASS_NAME = Integer.class.getName();
    private SourceFormatter sourceFormatter;

    @Before
    public void setUp() throws Exception {
        sourceFormatter = new SourceFormatter(MOCKED_RETURN_VALUE);
    }

    @Test
    public void testSubclassRegularObject() throws Exception {
        CtClass type = getType(OBJECT_CLASS_NAME);
        String expectedValue = sourceFormatter.format(WICKET_DATA_STUB_FACTORY_NAME, OBJECT_CLASS_NAME);
        assertEquals(expectedValue, new ValueSource(type, SUBCLASSING).toString());
    }

    @Test
    public void testSubclassFinalObject() throws Exception {
        CtClass type = getType(FINAL_CLASS_NAME);
        String expectedValue = sourceFormatter.format(WICKET_DATA_STUB_FACTORY_NAME, FINAL_CLASS_NAME);
        assertEquals(expectedValue, new ValueSource(type, SUBCLASSING).toString());
    }

    @Test
    public void testClonedArray() throws Exception {
        CtClass typeToMock = getType(new Object() {
            public int[] intArrayField;
        }.getClass().getField("intArrayField").getType().getName());
        String expectedValue = sourceFormatter.format(WICKET_DATA_STUB_FACTORY_NAME, typeToMock.getName());
        assertEquals(expectedValue, new ValueSource(typeToMock, CLONING).toString());
    }

    private CtClass getType(String className) throws NotFoundException {
        return ClassPool.getDefault().get(className);
    }
}
