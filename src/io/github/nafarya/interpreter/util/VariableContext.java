package io.github.nafarya.interpreter.util;

import java.util.Map;

public abstract class VariableContext {
    private Map<String, Integer> context;

    public VariableContext(Map<String, Integer> context) {
        this.context = context;
    }

    public Map<String, Integer> getContext() {
        return context;
    }
}
