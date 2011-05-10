package org.xbrlz.wicket.verifiable;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.xbrlz.wicket.verifiable.reflection.WicketMockFactory;

public class VerifiablePropertyModel<SourceType, TargetType> extends PropertyModel<TargetType> {
    private final Object modelObject;
    private final Class<SourceType> sourceClass;
    private final String expression;
    private final Class<TargetType> targetClass;
    private final boolean writable;

    private VerifiablePropertyModel(Object modelObject, Class<SourceType> sourceClass, String expression, Class<TargetType> targetClass, boolean isWritable) {
        super(modelObject, expression);
        this.modelObject = modelObject;
        this.sourceClass = sourceClass;
        this.expression = expression;
        this.targetClass = targetClass;
        writable = isWritable;
        validate(sourceClass, expression, targetClass);
    }

    private void validate(Class<SourceType> sourceClass, String expression, Class<TargetType> targetClass) {
        IModel model = new PropertyModel(WicketMockFactory.mock(sourceClass), expression);
        if (WebApplication.get().getConfigurationType().equals(Application.DEVELOPMENT)) {
            WicketMockFactory.assertMockType(model.getObject(), targetClass);
            if (writable)
                model.setObject(null);
        }
    }

    public static <SourceType> VerifiablePropertyModel<SourceType, SourceType> newPropertyModel(SourceType modelObject, Class<SourceType> sourceClass) {
        return new VerifiablePropertyModel<SourceType, SourceType>(modelObject, sourceClass, "", sourceClass, false);
    }

    public static <SourceType> VerifiablePropertyModel<SourceType, SourceType> newVerifiablePropertyModel(IModel<? extends SourceType> model, Class<SourceType> sourceClass) {
        return new VerifiablePropertyModel<SourceType, SourceType>(model, sourceClass, "", sourceClass, false);
    }

    public <NewTargetType> VerifiablePropertyModel<SourceType, NewTargetType> withExpression(String expression, Class<NewTargetType> targetType) {
        return new VerifiablePropertyModel<SourceType, NewTargetType>(modelObject, sourceClass, expression, targetType, false);
    }

    public VerifiablePropertyModel<SourceType, TargetType> asWritable() {
        return new VerifiablePropertyModel<SourceType, TargetType>(modelObject, sourceClass, expression, targetClass, true);
    }

    @Override
    public void setObject(TargetType object) {
        if (!writable)
            throw new UnsupportedOperationException("Tried to set a value on a PropertyModel of <" + sourceClass.getName() + "> " +
                    "with expression <" + expression + "> that was not considered to be writable");
        super.setObject(object);
    }

}
