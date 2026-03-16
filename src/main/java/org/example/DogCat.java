package org.example;

import org.example.mi.CallParent;
import org.example.mi.MultipleInheritance;

@MultipleInheritance(superclasses = {Dog.class, Cat.class})
public class DogCat extends IAnimalRoot {
    public DogCat() {
        super(IAnimal.class, DogCat.class);
    }

    @Override
    public String getName() {
        return "DogCat(" + nextGetName() + ")";
    }

    @Override
    public String speak() {
        return nextSpeak();
    }

    @CallParent(Cat.class)
    public String getNameCat() {
        return "DogCat(" + nextGetName() + ")";
    }

    @CallParent(Cat.class)
    public String speakCat() {
        return nextSpeak();
    }
}