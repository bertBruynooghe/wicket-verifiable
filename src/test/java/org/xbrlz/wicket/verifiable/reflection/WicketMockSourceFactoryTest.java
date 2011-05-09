package org.xbrlz.wicket.verifiable.reflection;

import javassist.CannotCompileException;
import javassist.CtField;
import javassist.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static javassist.CtClass.*;
import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class WicketMockSourceFactoryTest {
    private WicketMockSourceFactory srcFactory;

    @Before
    public void setUp() throws Exception {
        srcFactory = new WicketMockSourceFactory();
    }

    @Test
    public void testCreateGetterSourceForField() throws Exception {
        String expectedSrc = new SourceFormatter("public java.lang.Object getField(){return %s.mock(int.class);}")
                .format(WicketMockFactory.class.getName());
        assertEquals(expectedSrc, srcFactory.createGetterSourceForField(getTestField()));
    }

    @Test
    public void testCreateSetterSourceForField() throws Exception {
        String expectedSrc = new SourceFormatter("public void setField(int p0){return ;}")
                .format(WicketMockFactory.class.getName());
        assertEquals(expectedSrc, srcFactory.createSetterSourceForField(getTestField()));
    }

    private CtField getTestField() throws NotFoundException, CannotCompileException {
        CtField field = Mockito.mock(CtField.class);
        when(field.getType()).thenReturn(intType);
        when(field.getName()).thenReturn("field");
        return field;
    }


}
