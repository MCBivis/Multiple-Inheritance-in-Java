package org.example.mi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Рантайм для множественного наследования: C3-линеаризация и построение цепочки
 * call-next-method (обход «соседей» в духе CLOS).
 */
public final class MultipleInheritanceRuntime {

    private static final Map<Class<?>, List<Class<?>>> mroCache = new ConcurrentHashMap<>();

    private MultipleInheritanceRuntime() {}

    /**
     * C3-линеаризация: возвращает порядок разрешения методов для класса.
     * Возвращаемый список неизменяемый.
     */
    public static List<Class<?>> linearize(Class<?> clazz) {
        return mroCache.computeIfAbsent(clazz, c -> Collections.unmodifiableList(computeMRO(c)));
    }

    private static List<Class<?>> computeMRO(Class<?> clazz) {
        MultipleInheritance ann = clazz.getAnnotation(MultipleInheritance.class);
        Class<?>[] directSupers = ann != null ? ann.superclasses() : new Class<?>[0];

        if (directSupers.length == 0) {
            return Collections.singletonList(clazz);
        }

        List<List<Class<?>>> parentLinearizations = new ArrayList<>();
        for (Class<?> parent : directSupers) {
            parentLinearizations.add(new ArrayList<>(linearize(parent)));
        }

        List<Class<?>> result = new ArrayList<>();
        result.add(clazz);

        List<List<Class<?>>> allLists = new ArrayList<>(parentLinearizations);
        allLists.add(new ArrayList<>(Arrays.asList(directSupers)));
        merge(result, allLists);
        return result;
    }

    private static void merge(List<Class<?>> result, List<List<Class<?>>> linearizations) {
        while (true) {
            Class<?> candidate = null;

            for (List<Class<?>> list : linearizations) {
                if (list.isEmpty()) continue;
                Class<?> head = list.get(0);

                boolean headNotInAnyTail = true;
                for (List<Class<?>> other : linearizations) {
                    if (other.size() <= 1) continue;
                    List<Class<?>> tail = other.subList(1, other.size());
                    if (tail.contains(head)) {
                        headNotInAnyTail = false;
                        break;
                    }
                }
                if (headNotInAnyTail) {
                    candidate = head;
                    break;
                }
            }

            if (candidate == null) {
                throw new IllegalStateException("C3 linearization failed: inconsistent hierarchy");
            }

            result.add(candidate);

            for (List<Class<?>> list : linearizations) {
                if (!list.isEmpty() && list.get(0).equals(candidate)) {
                    list.remove(0);
                }
            }

            boolean allEmpty = linearizations.stream().allMatch(List::isEmpty);
            if (allEmpty) break;
        }
    }

    public static Object buildNextChain(Class<?> rootInterface, Class<?> currentClass) {
        List<Class<?>> mro = linearize(currentClass);
        if (mro.size() <= 1) return null;
        List<Class<?>> rest = mro.subList(1, mro.size());
        return buildChain(rootInterface, rest);
    }

    private static Object buildChain(Class<?> rootInterface, List<Class<?>> mroClasses) {
        if (mroClasses.isEmpty()) return null;
        try {
            Class<?> first = mroClasses.get(0);
            Constructor<?> ctor = first.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object firstInstance = ctor.newInstance();
            if (!rootInterface.isInstance(firstInstance)) {
                throw new IllegalArgumentException("Class " + first + " does not implement " + rootInterface);
            }
            Object restChain = mroClasses.size() == 1
                ? null
                : buildChain(rootInterface, mroClasses.subList(1, mroClasses.size()));
            setNext(rootInterface, firstInstance, restChain);
            return firstInstance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Cannot build next chain for " + mroClasses.get(0), e);
        }
    }

    public static void setNext(Class<?> rootInterface, Object node, Object next) {
        if (node == null) return;
        try {
            java.lang.reflect.Method setter = node.getClass().getMethod("setNext", rootInterface);
            setter.invoke(node, next);
        } catch (Exception e) {
            throw new IllegalStateException("setNext(" + rootInterface.getSimpleName() + ") not found on " + node.getClass(), e);
        }
    }
}
