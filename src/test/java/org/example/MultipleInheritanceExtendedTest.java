package org.example;

import org.example.mi.MultipleInheritanceRuntime;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultipleInheritanceExtendedTest {

    //
    @Test
    void linearizeWorksForClassWithoutParents() {
        class Solo extends IAnimalRoot {
            public Solo() { super(IAnimal.class, Solo.class); }
            @Override public String getName() { return "Solo"; }
            @Override public String speak() { return "Silent"; }
        }

        List<Class<?>> mro = MultipleInheritanceRuntime.linearize(Solo.class);
        assertEquals(1, mro.size());
        assertEquals(Solo.class, mro.get(0));
    }

    //
    @Test
    void callParentOnWrongMethodThrows() {
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            MultipleInheritanceRuntime.callParentMethod(
                    DogCat.class,
                    Dog.class,
                    "nonexistentMethod",
                    IAnimal.class
            );
        });
        assertTrue(ex.getMessage().contains("Cannot call parent method"));
    }

    //
    @Test
    void mroConsistencyForExtendedHierarchy() {
        @org.example.mi.MultipleInheritance(superclasses = {DogCat.class})
        class DogCatPlus extends IAnimalRoot {
            public DogCatPlus() { super(IAnimal.class, DogCatPlus.class); }
            @Override public String getName() { return nextGetName(); }
            @Override public String speak() { return nextSpeak(); }
        }

        List<Class<?>> mro = MultipleInheritanceRuntime.linearize(DogCatPlus.class);
        assertEquals(DogCatPlus.class, mro.get(0));
        assertEquals(DogCat.class, mro.get(1));
        assertEquals(Dog.class, mro.get(2));
        assertEquals(Cat.class, mro.get(3));
    }

    //
    @Test
    void nextChainHandlesNullSafely() {
        Dog dog = new Dog();
        assertNull(dog.next);
    }

    //
    @Test
    void chainObjectsCorrectlySetNext() {
        DogCat dogCat = new DogCat();
        Object next = MultipleInheritanceRuntime.buildNextChain(IAnimal.class, DogCat.class);
        assertNotNull(next);
        assertTrue(next instanceof Dog);
    }

    //
    @Test
    void callParentWorksForDogCat() {
        DogCat dogCat = new DogCat();
        assertEquals("DogCat(Cat)", dogCat.getNameCat());
        assertEquals("Meow!", dogCat.speakCat());
    }

    @Test
    void callParentThrowsIfWrongClass() {
        DogCat dogCat = new DogCat();
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            MultipleInheritanceRuntime.callParentMethod(DogCat.class, IAnimalRoot.class, "getNameCat", IAnimal.class);
        });
        assertTrue(ex.getMessage().contains("Cannot call parent method"));
    }

    //
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