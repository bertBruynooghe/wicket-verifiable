package org.xbrlz.wicket.verifiable.reflection;

import java.util.Formatter;
import java.util.Locale;

public class SourceFormatter {
    private final String pattern;

    public SourceFormatter(String pattern) {
        this.pattern = pattern;
    }

    public String format(Object... objects) {
        Formatter methodSource = new Formatter(Locale.US);
        methodSource.format(pattern, objects);
        return methodSource.toString();
    }
}
