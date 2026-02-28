package org.example;

public class Main {
    public static void main(String[] args) {
        IAnimal dog = new Dog();
        IAnimal cat = new Cat();
        IAnimal dogCat = new DogCat();

        System.out.println(dog.getName() + ": " + dog.speak());
        System.out.println(cat.getName() + ": " + cat.speak());
        System.out.println(dogCat.getName() + ": " + dogCat.speak());
        System.out.println("MRO(DogCat) = " + org.example.mi.MultipleInheritanceRuntime.linearize(DogCat.class));
    }
}
