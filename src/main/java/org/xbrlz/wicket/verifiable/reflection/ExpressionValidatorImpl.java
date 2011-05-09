package org.xbrlz.wicket.verifiable.reflection;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.xbrlz.wicket.verifiable.ExpressionValidator;

import static org.xbrlz.wicket.verifiable.reflection.WicketMockFactory.*;

public class ExpressionValidatorImpl implements ExpressionValidator {

    public void validate(Class sourceClass, Class targetClass, String expression, boolean writable) {
        IModel model = new PropertyModel(mock(sourceClass), expression);
        assertMockType(model.getObject(), targetClass);
        if (writable)
            model.setObject(null);
    }

}
