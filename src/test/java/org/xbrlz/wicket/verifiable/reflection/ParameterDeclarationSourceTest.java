package org.xbrlz.wicket.verifiable.reflection;

import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import static junit.framework.Assert.*;

public class ParameterDeclarationSourceTest {
    @Test
    public void testArrayParameter() throws Exception {
        CtClass type = ClassPool.getDefault().get("int[]");
        ParameterDeclarationSource parameterDeclarationSource = new ParameterDeclarationSource(new CtClass[]{type});
        assertEquals("int[] p0", parameterDeclarationSource.toString());
    }
}
