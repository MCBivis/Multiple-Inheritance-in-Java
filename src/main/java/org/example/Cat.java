package org.example;

import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {})
public class Cat extends IAnimalRoot {
    public Cat() {
        super(IAnimal.class, Cat.class);
    }

    @Override
    public String getName() {
        return "Cat";
    }

    @Override
    public String speak() {
        return "Meow!";
    }
}
