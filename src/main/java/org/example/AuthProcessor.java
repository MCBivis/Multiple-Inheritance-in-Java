package org.example;

import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {BaseProcessor.class})
public class AuthProcessor extends ICommandProcessorRoot {
    public AuthProcessor() {
        super(ICommandProcessor.class, AuthProcessor.class);
    }

    @Override
    public String pipeline() {
        return "Auth(" + nextPipeline() + ")";
    }

    @Override
    public String process(String input) {
        if (input != null && input.startsWith("token:")) {
            return "AUTH_OK(" + input + ")|" + nextProcess(input);
        }
        return "AUTH_DENY(" + input + ")";
    }
}