package org.xbrlz.wicket.verifiable.reflection;

import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.*;
import static org.xbrlz.wicket.verifiable.reflection.MockType.*;

public class ReturnTypeSourceTest {

    @Test
    public void testArrayTypeForSubclass() throws Exception {
        CtClass arrayField = ClassPool.getDefault().get("int[]");
        assertEquals("int[]", new ReturnTypeSource(arrayField, SUBCLASSING).toString());
    }

    @Test
    public void testArrayTypeForCloning() throws Exception {
        CtClass arrayField = ClassPool.getDefault().get("int[]");
        assertEquals(List.class.getName(), new ReturnTypeSource(arrayField, CLONING).toString());
    }

}
