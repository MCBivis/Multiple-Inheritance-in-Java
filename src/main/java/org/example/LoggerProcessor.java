package org.example;

import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {BaseProcessor.class})
public class LoggerProcessor extends ICommandProcessorRoot {
    public LoggerProcessor() {
        super(ICommandProcessor.class, LoggerProcessor.class);
    }

    @Override
    public String pipeline() {
        return "Logger(" + nextPipeline() + ")";
    }

    @Override
    public String process(String input) {
        return "LOG[" + input + "]|" + nextProcess(input);
    }
}