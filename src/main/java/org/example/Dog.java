package org.example;

import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {})
public class Dog extends IAnimalRoot {
    public Dog() {
        super(IAnimal.class, Dog.class);
    }

    @Override
    public String getName() {
        String base = (next != null) ? nextGetName() : "";
        return "Dog" + (base.isEmpty() ? "" : "+" + base);
    }

    @Override
    public String speak() {
        String base = (next != null) ? nextSpeak() : "";
        return "Woof!" + (base.isEmpty() ? "" : "+" + base);
    }
}
