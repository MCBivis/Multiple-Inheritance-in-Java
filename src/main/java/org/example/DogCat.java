package org.example;

import org.example.mi.MultipleInheritance;

/**
 * Множественное наследование: Dog + Cat.
 * MRO: DogCat, Dog, Cat (при порядке superclasses = {Dog.class, Cat.class}).
 */
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
}
