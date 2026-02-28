package org.example.mi;

import java.lang.annotation.*;

/**
 * Аннотация для корневого интерфейса гомогенной иерархии множественного наследования.
 * Обработчик сгенерирует вспомогательный класс-корень (например, ISomeInterfaceRoot)
 * с поддержкой call-next-method (методы next*).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MultipleInheritanceRoot {
}
