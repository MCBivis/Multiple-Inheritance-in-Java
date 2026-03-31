package org.example;

import org.example.mi.MultipleInheritanceRuntime;

public class Main {
    public static void main(String[] args) {
        IAnimal dog = new Dog();
        IAnimal cat = new Cat();
        DogCat dogCat = new DogCat();

        System.out.println(dog.getName() + ": " + dog.speak());
        System.out.println(cat.getName() + ": " + cat.speak());
        System.out.println(dogCat.getName() + ": " + dogCat.speak());
        System.out.println(dogCat.getNameCat() + ": " + dogCat.speakCat());
        System.out.println("MRO(DogCat) = " + org.example.mi.MultipleInheritanceRuntime.linearize(DogCat.class));

        SecureCacheApi api = new SecureCacheApi();
        System.out.println("\nMRO(SecureCacheApi) = " +
                MultipleInheritanceRuntime.linearize(SecureCacheApi.class));
        System.out.println("pipeline = " + api.pipeline());
        System.out.println("process(token:abc:ping) = " +
                api.process("token:abc:ping"));
        System.out.println("process(hello) = " +
                api.process("hello"));
        System.out.println("pipelineAdmin = " + api.pipelineAdmin());
        System.out.println("processAdmin(token:abc:ping) = " +
                api.processAdmin("token:abc:ping"));
    }
}
