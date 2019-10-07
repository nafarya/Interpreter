package io.github.nafarya.interpreter.util;

import java.util.HashMap;

public class FunctionContext extends VariableContext {
    public FunctionContext() {
        super(new HashMap<>());
    }
}
