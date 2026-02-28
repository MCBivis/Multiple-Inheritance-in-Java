package org.example.mi;

import java.lang.annotation.*;

/**
 * Аннотация для класса в иерархии множественного наследования.
 * Указывает фактические суперклассы (с точки зрения MRO); класс при этом
 * в Java наследуется от сгенерированного Root-класса.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MultipleInheritance {
    /**
     * Прямые суперклассы в порядке объявления (участвуют в C3-линеаризации).
     */
    Class<?>[] superclasses() default {};
}
