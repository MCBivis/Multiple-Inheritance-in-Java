package org.example;

import org.example.mi.MultipleInheritanceRoot;

@MultipleInheritanceRoot
public interface ICommandProcessor {
    String pipeline();
    String process(String input);
}