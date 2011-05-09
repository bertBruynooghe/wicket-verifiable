package org.xbrlz.wicket.verifiable.reflection;

import javassist.CtClass;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import static com.google.common.collect.Lists.*;

public class ParameterDeclarationSource {
    private final CtClass[] parameterTypes;

    public ParameterDeclarationSource(CtClass[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String toString() {
        List<String> parameterTypeNames = newArrayList();

        for (int i = 0; i < parameterTypes.length; i++) {
            CtClass parameterType = parameterTypes[i];
            String parameterTypeName = parameterType.getName();
            parameterTypeNames.add(parameterTypeName + " p" + i);
        }

        return StringUtils.join(parameterTypeNames, ",");
    }
}
