package org.example;

import org.example.mi.MultipleInheritanceRuntime;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultipleInheritanceTest {

    @Test
    void mroDogCatIsDogThenCat() {
        List<Class<?>> mro = MultipleInheritanceRuntime.linearize(DogCat.class);
        assertEquals(DogCat.class, mro.get(0));
        assertEquals(Dog.class, mro.get(1));
        assertEquals(Cat.class, mro.get(2));
    }

    @Test
    void dogCatGetNameUsesCallNextMethod() {
        IAnimal dogCat = new DogCat();
        assertTrue(dogCat.getName().startsWith("DogCat(Dog+Cat)"));
    }

    @Test
    void dogCatSpeakDelegatesToNext() {
        IAnimal dogCat = new DogCat();
        assertEquals("Woof!+Meow!", dogCat.speak());
    }

    @Test
    void dogAndCatWorkAsIAnimal() {
        IAnimal dog = new Dog();
        IAnimal cat = new Cat();
        assertEquals("Dog", dog.getName());
        assertEquals("Woof!", dog.speak());
        assertEquals("Cat", cat.getName());
        assertEquals("Meow!", cat.speak());
    }
}
