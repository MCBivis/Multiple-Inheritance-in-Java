package org.example;

import org.example.mi.CallParent;
import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {AuthProcessor.class, LoggerCachingProcessor.class})
public class SecureCacheApi extends ICommandProcessorRoot {
    public SecureCacheApi() {
        super(ICommandProcessor.class, SecureCacheApi.class);
    }

    @Override
    public String pipeline() {
        return "API(" + nextPipeline() + ")";
    }

    @Override
    public String process(String input) {
        return "API->" + nextProcess(input);
    }

    // Обойти Auth: в рамках вызова nextProcess() вы принудительно стартуете с LoggerCachingProcessor
    @CallParent(LoggerCachingProcessor.class)
    public String processAdmin(String input) {
        return "ADMIN->" + nextProcess(input);
    }

    @CallParent(LoggerCachingProcessor.class)
    public String pipelineAdmin() {
        return "ADMIN_PIPE(" + nextPipeline() + ")";
    }
}