package org.xbrlz.wicket.verifiable.reflection;

import com.google.common.primitives.Primitives;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class WicketMockFactory {
    /**
     * This class is meant to help validating expressions as used in Wicket's propertyModels
     * e.g.
     * IModel model = new PropertyModel( mock(Person.class), “addressBook.addresses.0.zip”);
     * assertMockType(model.getObject(), Zip.class);
     * If we want to test the set Object, we simply call:
     * model.setObject()
     * If the object did not allow writing, we'll get a nice exception
     * the mocking will replicate the class, getters, setters and field structure of the Person class,
     * and getters and fields never return a null value.
     */


    private static Map<String, Class> map = new HashMap<String, Class>();

    private final Object mock;

    public static Object mock(Class classToMock) {
        try {
            return new WicketMockFactory(classToMock).getMock();
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private WicketMockFactory(Class classToMock) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {
        String newClassName = WicketMockClassFactory.WICKETMOCK_PACKAGE_PREFIX + classToMock.getName();
        Class clazz = map.get(newClassName);
        if (clazz == null) {
            CtClass ctClass = new WicketMockClassFactory(classToMock).getMock();
            clazz = ctClass.toClass();
            map.put(newClassName, clazz);
        }
        mock = clazz.newInstance();
    }

    private Object getMock() {
        return mock;
    }

    //TODO: rename?
    public static void assertMockType(Object mock, Class clazz) {
        try {
            CtClass referenceClass = ClassPool.getDefault().get(clazz.getName());
            String className = referenceClass.getName();
            if (mock instanceof WicketMock) {
                if (className.equals(((WicketMock) mock).getClassToMock().getName()))
                    return;
                else if (Primitives.unwrap(clazz).getName().equals(((WicketMock) mock).getClassToMock().getName()))
                    return;
                assertEquals(clazz.getName(), ((WicketMock) mock).getClassToMock().getName());
            } else
                assertEquals("Object <" + mock.getClass().getName() + "> is not a mock nor <" + className + ">!", className, mock.getClass().getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static interface WicketMock {
        CtClass getClassToMock();
    }
}
