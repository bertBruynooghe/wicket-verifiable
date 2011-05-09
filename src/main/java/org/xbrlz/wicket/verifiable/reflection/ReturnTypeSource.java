package org.xbrlz.wicket.verifiable.reflection;

import javassist.CtClass;

import java.util.List;

import static org.xbrlz.wicket.verifiable.reflection.MockType.*;

class ReturnTypeSource {

    private final CtClass returnType;
    private final MockType mockType;

    public ReturnTypeSource(CtClass returnType, MockType mockType) {
        this.returnType = returnType;
        this.mockType = mockType;
    }

    @Override
    public String toString() {
        //in case of an array, return a list, because we want to be able to override the get(i)
        //unless we are not cloning, in which case we return the correct returnTypeName
        if (mockType == CLONING) {
            if (returnType.isArray())
                return List.class.getName();
            return Object.class.getName();
        }
        return returnType.getName();
    }

}
