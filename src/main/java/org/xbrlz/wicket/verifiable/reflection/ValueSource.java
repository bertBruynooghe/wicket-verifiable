package org.xbrlz.wicket.verifiable.reflection;

import javassist.CtClass;

import static org.xbrlz.wicket.verifiable.reflection.MockType.*;

class ValueSource {

    static final String MOCKED_RETURN_VALUE = "%s.mock(%s.class)";
    private static final String MOCK_CLASS_NAME = WicketMockFactory.class.getName();

    private final CtClass valueType;
    private final MockType mockType;

    public ValueSource(CtClass valueType, MockType mockType) {
        this.valueType = valueType;
        this.mockType = mockType;
    }

    @Override
    public String toString() {
        if (mockType == SUBCLASSING) {
            if (valueType.isPrimitive())
                return "0";
            if (valueType.isArray())
                return "null";
        }
        return new SourceFormatter(MOCKED_RETURN_VALUE).format(MOCK_CLASS_NAME, valueType.getName());
    }

}
