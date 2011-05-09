package org.xbrlz.wicket.verifiable.reflection;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.bytecode.DuplicateMemberException;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.xbrlz.wicket.verifiable.reflection.MockType.*;

class WicketMockClassFactory {
    public static final String WICKETMOCK_PACKAGE_PREFIX = "wicketmock.";
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    private static final CtClass LIST_CLASS;
    private static final CtClass OBJECT_CLASS;
    private static final CtClass WICKET_MOCK_CLASS;
    private static final String LIST_GETTER_SOURCE_PATTERN = "public javassist.CtClass getClassToMock(){" +
            "return javassist.ClassPool.getDefault().get(\"%s\");" +
            "}";
    private static final String LIST_SIZE_METHOD_SRC = "public int size(){return java.lang.Integer.MAX_VALUE;}";
    private static final String GETTER_PREFIX = "get";

    final WicketMockSourceFactory srcFactory = new WicketMockSourceFactory();
    final private CtClass classToMock;
    private CtClass createdClass;
    final private boolean isList;

    static {
        try {
            LIST_CLASS = CLASS_POOL.get(List.class.getName());
            OBJECT_CLASS = CLASS_POOL.get(Object.class.getName());
            WICKET_MOCK_CLASS = CLASS_POOL.get(WicketMockFactory.WicketMock.class.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public WicketMockClassFactory(Class classToMock) throws NotFoundException, CannotCompileException {
        this.classToMock = CLASS_POOL.get(classToMock.getName());
        String newClassName = WICKETMOCK_PACKAGE_PREFIX + classToMock.getName();
        try {
            createdClass = CLASS_POOL.get(newClassName);
            createdClass.detach();
        } catch (NotFoundException e) {
            //ignore
        }

        createdClass = CLASS_POOL.makeClass(newClassName);
        isList = List.class.isAssignableFrom(classToMock) || classToMock.isArray();
        implementMockClass();
        if (isList)
            createdClass.addInterface(LIST_CLASS);
        if (classToMock.isArray())
            createdClass.addField(CtField.make("public int length = Integer.MAX_VALUE;", createdClass));

        for (CtMethod ctMethod : createMethods())
            addMethod(createdClass, ctMethod);
        for (CtMethod ctMethod : createAccessorMethodsForFields())
            addMethod(createdClass, ctMethod);
    }

    public CtClass getMock() {
        return createdClass;
    }

    private void implementMockClass() throws CannotCompileException {
        createdClass.addInterface(WICKET_MOCK_CLASS);
        SourceFormatter getterSrc = new SourceFormatter(LIST_GETTER_SOURCE_PATTERN);
        addMethod(createdClass, CtMethod.make(getterSrc.format(classToMock.getName()), createdClass));
    }

    private List<CtMethod> createAccessorMethodsForFields() throws NotFoundException, CannotCompileException {
        List<CtMethod> result = newArrayList();
        for (CtField ctField : classToMock.getFields()) {
            int modifiers = ctField.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                String src = srcFactory.createGetterSourceForField(ctField);
                result.add(CtMethod.make(src, createdClass));
                if (!Modifier.isFinal(modifiers)) {
                    src = srcFactory.createSetterSourceForField(ctField);
                    result.add(CtMethod.make(src, createdClass));
                }
            }
        }
        return result;
    }

    private Collection<CtMethod> createMethods() throws CannotCompileException, NotFoundException {
        Set<CtMethod> result = newHashSet();
        if (isList) {
            if (classToMock.isArray()) {
                CtClass componentType = classToMock.getComponentType();
                String methodDescriptor = Descriptor.ofMethod(OBJECT_CLASS, new CtClass[]{CtClass.intType});
                CtMethod get = LIST_CLASS.getMethod(GETTER_PREFIX, methodDescriptor);
                String src = srcFactory.createMethodSource(get, componentType, CLONING);
                result.add(CtMethod.make(src, createdClass));
            }
            result.add(CtMethod.make(LIST_SIZE_METHOD_SRC, createdClass));
            result.addAll(createMethods(LIST_CLASS, SUBCLASSING));
        }
        result.addAll(createMethods(classToMock, CLONING));
        return result;
    }

    private Collection<? extends CtMethod> createMethods(CtClass templateClass, MockType type) throws NotFoundException, CannotCompileException {
        List<CtMethod> result = newArrayList();
        CtClass ctObjectClass = CLASS_POOL.get(Object.class.getName());

        for (CtMethod method : templateClass.getMethods()) {
            if (type == SUBCLASSING || method.getParameterTypes().length == 0)
                try {
                    //if the method already exist in Object, then don't add it again
                    if (type == CLONING && method.getName().startsWith(GETTER_PREFIX)) {
                        String setterSrc = getCorrespondingSetterSrc(method, templateClass);
                        if (setterSrc != null)
                            result.add(CtMethod.make(setterSrc, createdClass));
                    }
                    String methodDescriptor = Descriptor.ofMethod(method.getReturnType(), method.getParameterTypes());
                    ctObjectClass.getMethod(method.getName(), methodDescriptor);
                } catch (NotFoundException e) {
                    String src = srcFactory.createMethodSource(method, type);
                    result.add(CtMethod.make(src, createdClass));
                }
        }
        return result;
    }

    private String getCorrespondingSetterSrc(CtMethod method, CtClass templateClass) {
        String fieldName = method.getName().substring(GETTER_PREFIX.length());
        try {
            templateClass.getMethod("set" + fieldName, Descriptor.ofMethod(CtClass.voidType, new CtClass[]{method.getReturnType()}));
            return "public void set" + fieldName + "(Object p){}";
        } catch (NotFoundException e) {
            //ignore; it means the setter was not found, so we should not add it to the mock
        }
        return null;
    }

    private void addMethod(CtClass ctClass, CtMethod ctMethod) throws CannotCompileException {
        try {
            ctClass.addMethod(ctMethod);
        } catch (CannotCompileException e) {
            if (!(e.initCause(e) instanceof DuplicateMemberException))
                throw (e);
            //else simply ignore this.
        }
    }

}
