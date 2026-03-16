package org.example.mi.processor;

import org.example.mi.MultipleInheritanceRoot;
import org.example.mi.CallParent;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedAnnotationTypes("org.example.mi.MultipleInheritanceRoot")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class RootProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getKind() != ElementKind.INTERFACE) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "@MultipleInheritanceRoot can only be applied to interfaces", element);
                    continue;
                }
                TypeElement interfaceElement = (TypeElement) element;
                try {
                    generateRootClass(interfaceElement);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Failed to generate Root class: " + e.getMessage(), element);
                }
            }
        }
        return true;
    }

    private void generateRootClass(TypeElement interfaceElement) throws IOException {
        String packageName = getPackageName(interfaceElement);
        String interfaceName = interfaceElement.getSimpleName().toString();
        String rootClassName = interfaceName + "Root";
        String fullRootName = packageName.isEmpty() ? rootClassName : packageName + "." + rootClassName;

        JavaFileObject file = processingEnv.getFiler().createSourceFile(fullRootName, interfaceElement);
        try (Writer w = file.openWriter()) {
            w.write("package " + packageName + ";\n\n");
            w.write("import org.example.mi.MultipleInheritanceRuntime;\n");
            w.write("import org.example.mi.CallParent;\n\n"); // импорт аннотации

            w.write("/** Generated root for multiple inheritance hierarchy. Do not edit. */\n");
            w.write("public abstract class " + rootClassName + " implements " + interfaceName + " {\n\n");
            w.write("    protected " + interfaceName + " next;\n\n");

            w.write("    protected " + rootClassName + "() {\n        this.next = null;\n    }\n\n");

            w.write("    protected " + rootClassName + "(Class<?> rootInterface, Class<?> self) {\n");
            w.write("        this.next = (" + interfaceName + ") MultipleInheritanceRuntime.buildNextChain(rootInterface, self);\n");
            w.write("    }\n\n");

            w.write("    public void setNext(" + interfaceName + " next) {\n        this.next = next;\n    }\n\n");

            List<ExecutableElement> methods = getInterfaceMethods(interfaceElement);
            for (ExecutableElement method : methods) {
                if (method.getSimpleName().toString().startsWith("next")
                        || method.getModifiers().contains(Modifier.STATIC)
                        || method.getModifiers().contains(Modifier.DEFAULT)) {
                    continue;
                }
                String methodName = method.getSimpleName().toString();
                String nextMethodName = "next" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
                String sig = methodSignature(method);
                String args = methodArgs(method);
                String returnType = method.getReturnType().toString();
                boolean isVoid = "void".equals(returnType);

                // 1️⃣ Сгенерированный метод с обычным поведением
                w.write("    @Override\n");
                w.write("    public " + sig + " {\n");
                if (isVoid) {
                    w.write("        if (next != null) next." + methodName + "(" + args + ");\n");
                } else {
                    w.write("        return next != null ? next." + methodName + "(" + args + ") : " + defaultReturn(returnType) + ";\n");
                }
                w.write("    }\n\n");

                // 2️⃣ Сгенерированный call-next-method с поддержкой @CallParent
                String nextSig = sig.replace(" " + methodName + "(", " " + nextMethodName + "(");
                w.write("    /** call-next-method: следующая реализация по MRO или указанному родителю. */\n");
                w.write("    public " + nextSig + " {\n");
                w.write("        try {\n");
                w.write("            Class<?>[] paramTypes = new Class<?>[] {" + methodParamTypes(method) + "};\n");
                w.write("            String callerName = java.lang.StackWalker.getInstance(java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE)\n");
                w.write("                .walk(s -> s.skip(1)\n");
                w.write("                    .filter(f -> !f.getMethodName().startsWith(\"next\"))\n");
                w.write("                    .findFirst()\n");
                w.write("                    .map(java.lang.StackWalker.StackFrame::getMethodName)\n");
                w.write("                    .orElse(null));\n");
                w.write("\n");
                w.write("            CallParent ann = null;\n");
                w.write("            if (callerName != null) {\n");
                w.write("                for (java.lang.reflect.Method cm : this.getClass().getMethods()) {\n");
                w.write("                    if (cm.getName().equals(callerName)) {\n");
                w.write("                        ann = cm.getAnnotation(CallParent.class);\n");
                w.write("                        if (ann != null) break;\n");
                w.write("                    }\n");
                w.write("                }\n");
                w.write("            }\n");
                w.write("\n");
                w.write("            if (ann != null) {\n");
                w.write("                Class<?> parent = ann.value();\n");
                w.write("                java.lang.reflect.Method pm = parent.getMethod(\"" + methodName + "\", paramTypes);\n");
                w.write("                Object inst = parent.getDeclaredConstructor().newInstance();\n");
                if (isVoid) {
                    w.write("                pm.invoke(inst" + (args.isEmpty() ? "" : ", " + args) + ");\n");
                } else {
                    w.write("                return (" + returnType + ") pm.invoke(inst" + (args.isEmpty() ? "" : ", " + args) + ");\n");
                }
                w.write("            }\n");
                w.write("        } catch (Exception e) {\n");
                w.write("            throw new RuntimeException(e);\n");
                w.write("        }\n");

                // обычный вызов next по MRO
                if (isVoid) {
                    w.write("        if (next != null) next." + methodName + "(" + args + ");\n");
                } else {
                    w.write("        return next != null ? next." + methodName + "(" + args + ") : " + defaultReturn(returnType) + ";\n");
                }
                w.write("    }\n\n");
            }

            w.write("}\n");
        }
    }

    private List<ExecutableElement> getInterfaceMethods(TypeElement interfaceElement) {
        List<ExecutableElement> result = new ArrayList<>();
        for (Element member : interfaceElement.getEnclosedElements()) {
            if (member.getKind() == ElementKind.METHOD) {
                result.add((ExecutableElement) member);
            }
        }
        for (TypeMirror superInterface : interfaceElement.getInterfaces()) {
            if (superInterface instanceof DeclaredType) {
                Element superEl = ((DeclaredType) superInterface).asElement();
                if (superEl instanceof TypeElement) {
                    result.addAll(getInterfaceMethods((TypeElement) superEl));
                }
            }
        }
        return result;
    }

    private String getPackageName(TypeElement type) {
        Element enclosing = type.getEnclosingElement();
        while (enclosing != null && enclosing.getKind() != ElementKind.PACKAGE) {
            enclosing = enclosing.getEnclosingElement();
        }
        return enclosing != null ? ((PackageElement) enclosing).getQualifiedName().toString() : "";
    }

    private String methodSignature(ExecutableElement method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType()).append(" ").append(method.getSimpleName()).append("(");
        List<? extends VariableElement> params = method.getParameters();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(params.get(i).asType()).append(" ").append(params.get(i).getSimpleName());
        }
        sb.append(")");
        return sb.toString();
    }

    private String methodArgs(ExecutableElement method) {
        List<String> names = new ArrayList<>();
        for (VariableElement p : method.getParameters()) {
            names.add(p.getSimpleName().toString());
        }
        return String.join(", ", names);
    }

    private String methodParamTypes(ExecutableElement method) {
        List<? extends VariableElement> params = method.getParameters();
        if (params.isEmpty()) return "";
        List<String> parts = new ArrayList<>();
        for (VariableElement p : params) {
            TypeMirror t = p.asType();
            // For reflection we need the raw runtime type; toString() is OK for primitives and declared types.
            parts.add(t.toString() + ".class");
        }
        return String.join(", ", parts);
    }

    private String defaultReturn(String returnType) {
        switch (returnType) {
            case "boolean": return "false";
            case "int": case "long": case "short": case "byte": case "char": return "0";
            case "double": case "float": return "0.0";
            case "void": return "";
            default: return "null";
        }
    }
}