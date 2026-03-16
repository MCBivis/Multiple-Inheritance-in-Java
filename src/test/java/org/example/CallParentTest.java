package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CallParentTest {

    @Test
    void dogCatSpeakCatUsesCatSpeakViaCallParent() {
        DogCat dogCat = new DogCat();
        assertEquals("DogCat(Cat)", dogCat.getNameCat());
        assertEquals("Meow!", dogCat.speakCat());
    }

    @Test
    void nextSpeakWithoutCallParentFollowsMro() {
        IAnimal dogCat = new DogCat();
        assertEquals("DogCat(Dog+Cat)", dogCat.getName());
        assertEquals("Woof!+Meow!", dogCat.speak());
    }
}

