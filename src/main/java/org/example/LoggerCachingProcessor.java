package org.example;

import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {LoggerProcessor.class, CachingProcessor.class})
public class LoggerCachingProcessor extends ICommandProcessorRoot {
    public LoggerCachingProcessor() {
        super(ICommandProcessor.class, LoggerCachingProcessor.class);
    }
    // можно ничего не переопределять: root-реализация сама делегирует в next*
}