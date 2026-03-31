package org.example;

import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {})
public class BaseProcessor extends ICommandProcessorRoot {
    public BaseProcessor() {
        super(ICommandProcessor.class, BaseProcessor.class);
    }

    @Override
    public String pipeline() {
        return "Base";
    }

    @Override
    public String process(String input) {
        return "Base(" + input + ")";
    }
}