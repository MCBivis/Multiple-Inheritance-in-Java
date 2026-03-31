package org.example;

import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {BaseProcessor.class})
public class CachingProcessor extends ICommandProcessorRoot {
    public CachingProcessor() {
        super(ICommandProcessor.class, CachingProcessor.class);
    }

    @Override
    public String pipeline() {
        return "Cache(" + nextPipeline() + ")";
    }

    @Override
    public String process(String input) {
        String v = (input == null) ? "null" : input;
        boolean hit =
                "ping".equalsIgnoreCase(v) ||
                        v.toLowerCase().endsWith(":ping");

        if (hit) {
            // демонстрация call-next-method: при hit дальше не зовём nextProcess
            return "CACHE_HIT(" + v + ")";
        }
        return "CACHE_MISS(" + v + ")|" + nextProcess(input);
    }
}