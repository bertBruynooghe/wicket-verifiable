package org.xbrlz.wicket.verifiable.reflection;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import org.junit.Test;

import java.util.Date;

import static javassist.CtClass.*;
import static junit.framework.Assert.*;

public class WicketMockClassFactoryTest {
    private static Class selfReflectingContainerMockClass = null;
    private static final CtClass OBJECT_CLASS;

    static {
        try {
            OBJECT_CLASS = ClassPool.getDefault().get(Object.class.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testSetter() throws Exception {
        CtClass mock = new WicketMockClassFactory(Date.class).getMock();
        assertNotNull(mock.getMethod("setSeconds", Descriptor.ofMethod(voidType, new CtClass[]{ClassPool.getDefault().get(Object.class.getName())})));
    }

    @Test
    public void testArrayHasLengthField() throws Exception {
        CtClass mock = new WicketMockClassFactory(int[].class).getMock();
        assertEquals(CtClass.intType, mock.getField("length").getType());
    }

    @Test(expected = Test.None.class)
    public void testField() throws Exception {
        CtClass mock = new WicketMockClassFactory(FieldContainer.class).getMock();
        mock.getMethod("getNonFinalField", Descriptor.ofMethod(OBJECT_CLASS, new CtClass[]{}));
        mock.getMethod("setNonFinalField", Descriptor.ofMethod(voidType, new CtClass[]{intType}));
    }


    @Test(expected = NotFoundException.class)
    public void testFinalField() throws Exception {
        CtClass mock = new WicketMockClassFactory(FieldContainer.class).getMock();
        mock.getMethod("setField", Descriptor.ofMethod(voidType, new CtClass[]{intType}));
    }

    @Test(expected = Test.None.class)
    public void testSelfReferencingField() throws Exception {
        CtClass mock = new WicketMockClassFactory(SelfReferencingContainer.class).getMock();
        if (selfReflectingContainerMockClass == null)
            selfReflectingContainerMockClass = mock.toClass();
        selfReflectingContainerMockClass.newInstance();
    }

    private static class FieldContainer {
        public int nonFinalField;
        final public int field = 0;
    }

    public static class SelfReferencingContainer {
        final public SelfReferencingContainer field = new SelfReferencingContainer();
    }
}
