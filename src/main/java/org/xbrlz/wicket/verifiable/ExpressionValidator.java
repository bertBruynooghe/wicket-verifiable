package org.xbrlz.wicket.verifiable;

public interface ExpressionValidator {
    void validate(Class sourceClass, Class targetClass, String expression, boolean writable);
}
