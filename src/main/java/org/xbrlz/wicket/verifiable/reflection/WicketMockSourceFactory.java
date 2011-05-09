package org.xbrlz.wicket.verifiable.reflection;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

import static javassist.CtClass.*;
import static org.xbrlz.wicket.verifiable.reflection.MockType.*;

public class WicketMockSourceFactory {
    static final String METHOD_PATTERN = "public %s %s(%s){return %s;}";
    private static final String FIELD_PATTERN = "public Object %s = %s;";

    String createFieldSource(CtField ctField) throws NotFoundException {
        return new SourceFormatter(FIELD_PATTERN).format(ctField.getName(), new ValueSource(ctField.getType(), CLONING).toString());
    }

    String createMethodSource(CtMethod method, MockType mockType) throws NotFoundException {
        CtClass returnType = method.getReturnType();
        CtClass[] parameterTypes = method.getParameterTypes();

        String parameterDeclaration = new ParameterDeclarationSource(parameterTypes).toString();
        String returnValueSource = new ValueSource(returnType, mockType).toString();

        String returnTypeName = new ReturnTypeSource(returnType, mockType).toString();
        return new SourceFormatter(METHOD_PATTERN).format(returnTypeName, method.getName(), parameterDeclaration, returnValueSource);
    }

    String createMethodSource(CtMethod method, CtClass returnType, MockType mockType) throws NotFoundException {
        CtClass[] parameterTypes = method.getParameterTypes();

        String parameterDeclaration = new ParameterDeclarationSource(parameterTypes).toString();
        String returnValueSource = new ValueSource(returnType, mockType).toString();

        String returnTypeName = new ReturnTypeSource(returnType, mockType).toString();
        return new SourceFormatter(METHOD_PATTERN).format(returnTypeName, method.getName(), parameterDeclaration, returnValueSource);
    }

    public String createGetterSourceForField(CtField field) throws NotFoundException {
        String returnTypeName = new ReturnTypeSource(field.getType(), CLONING).toString();
        String returnValueSource = new ValueSource(field.getType(), CLONING).toString();
        String fieldName = field.getName();
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return new SourceFormatter(METHOD_PATTERN).format(returnTypeName, methodName, "", returnValueSource);
    }

    public String createSetterSourceForField(CtField field) throws NotFoundException {
        String fieldName = field.getName();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String parameterDeclaration = new ParameterDeclarationSource(new CtClass[]{field.getType()}).toString();
        return new SourceFormatter(METHOD_PATTERN).format(voidType.getName(), methodName, parameterDeclaration, "");
    }
}
