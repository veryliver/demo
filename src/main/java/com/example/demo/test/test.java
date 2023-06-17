package com.example.demo.test;

import javax.tools.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;

@Service
public class test {
    // 自定义 JavaSourceFromString 类，用于保存代码字符串作为源文件
    static class JavaSourceFromString extends SimpleJavaFileObject {
        private final String code;

        public JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    // 自定义 JavaByteObject 类，用于保存编译生成的字节码
    static class JavaByteObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream;

        public JavaByteObject(String name) {
            super(URI.create("byte:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
            outputStream = new ByteArrayOutputStream();
        }

        @Override
        public OutputStream openOutputStream() {
            return outputStream;
        }

        public byte[] getBytes() {
            return outputStream.toByteArray();
        }
    }

    // 主要实现方法
    public static void compileAndRunCode(String code, Object... params) {
        // 创建 Java 编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // 创建诊断信息收集器
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

        // 创建一个内存文件管理器，用于保存编译生成的字节码
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Map<String, JavaByteObject> compiledClasses = new HashMap<>();

            JavaFileManager javaFileManager = new ForwardingJavaFileManager<StandardJavaFileManager>(fileManager) {
                @Override
                public JavaFileObject getJavaFileForOutput(Location location, String className,
                        JavaFileObject.Kind kind, FileObject sibling) throws IOException {
                    JavaByteObject compiledObject = new JavaByteObject(className);
                    compiledClasses.put(className, compiledObject);
                    return compiledObject;
                }

                @Override
                public ClassLoader getClassLoader(Location location) {
                    return new ClassLoader() {
                        @Override
                        protected Class<?> findClass(String className) throws ClassNotFoundException {
                            JavaByteObject compiledObject = compiledClasses.get(className);
                            if (compiledObject == null) {
                                return super.findClass(className);
                            }
                            byte[] bytes = compiledObject.getBytes();
                            return defineClass(className, bytes, 0, bytes.length);
                        }
                    };
                }
            };

            // 解析代码字符串
            CompilationUnit compilationUnit = StaticJavaParser.parse(code);

            // 获取已存在的import声明
            NodeList<ImportDeclaration> existingImports = compilationUnit.getImports();

            // 添加需要自动引入的类的导入语句
            List<String> importStatements = new ArrayList<>();
            importStatements.add("import java.util.List;");
            importStatements.add("import java.util.ArrayList;");
            importStatements.add("import java.util.Arrays;");
            importStatements.add("import java.util.Queue;");
            importStatements.add("import java.util.LinkedList;");
            importStatements.add("import java.util.Stack;");
            importStatements.add("import java.util.Map;");
            importStatements.add("import java.util.HashMap;");
            importStatements.add("import java.util.Set;");
            importStatements.add("import java.util.HashSet;");
            importStatements.add("import java.util.PriorityQueue;");
            importStatements.add("import java.util.Comparator;");
            importStatements.add("import java.util.Collections;");

            // 检查并添加缺失的import语句
            for (String importStatement : importStatements) {
                boolean isImportMissing = true;

                // 检查是否已存在相同的import语句
                for (ImportDeclaration existingImport : existingImports) {
                    if (existingImport.getNameAsString().equals(importStatement)) {
                        isImportMissing = false;
                        break;
                    }
                }

                // 如果import语句缺失，则添加到CompilationUnit
                if (isImportMissing) {
                    ImportDeclaration newImport = StaticJavaParser.parseImport(importStatement);
                    compilationUnit.addImport(newImport);
                }
            }

            // 重新生成修改后的代码字符串
            String modifiedCode = compilationUnit.toString();

            // 创建编译任务，并传递诊断信息收集器
            JavaCompiler.CompilationTask task = compiler.getTask(null, javaFileManager, diagnosticCollector, null, null,
                    Collections.singletonList(new JavaSourceFromString("Solution", modifiedCode.toString())));

            // 执行编译任务
            if (task.call()) {
                System.out.println("编译成功。");

                // 获取编译后的类加载器
                ClassLoader classLoader = javaFileManager.getClassLoader(null);

                try {
                    // 加载编译后的类
                    Class<?> compiledClass = classLoader.loadClass("Solution");
                    Object instance = compiledClass.getDeclaredConstructor().newInstance();

                    // 获取所有方法
                    Method[] methods = compiledClass.getDeclaredMethods();

                    // 遍历方法
                    for (Method method : methods) {
                        if (isMethodMatching(method, params)) {
                            // 调用方法，并传递参数
                            Object result = method.invoke(instance, params);

                            // 处理结果
                            if (result != null) {
                                Class<?> resultClass = result.getClass();
                                System.out.println("结果类型：" + resultClass.getName());

                                // 输出结果值
                                System.out.print("结果值：");
                                if (resultClass.isArray()) {
                                    // 处理数组类型结果
                                    int length = Array.getLength(result);
                                    System.out.print("[");
                                    for (int i = 0; i < length; i++) {
                                        Object element = Array.get(result, i);
                                        System.out.print(element);
                                        if (i < length - 1) {
                                            System.out.print(", ");
                                        }
                                    }
                                    System.out.println("]");
                                } else {
                                    System.out.println(result);
                                }
                            } else {
                                System.out.println("结果为 null");
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("编译失败。");
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                    System.out.println(diagnostic.getMessage(null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 检查方法是否与参数匹配
    private static boolean isMethodMatching(Method method, Object[] params) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != params.length) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!isAssignable(parameterTypes[i], params[i])) {
                return false;
            }
        }
        return true;
    }

    // 检查类型是否可以分配给目标类型
    private static boolean isAssignable(Class<?> targetType, Object value) {
        if (targetType.isPrimitive()) {
            return isPrimitiveAssignable(targetType, value);
        } else {
            return targetType.isInstance(value);
        }
    }

    // 检查原始类型是否可以分配给目标原始类型
    private static boolean isPrimitiveAssignable(Class<?> targetType, Object value) {
        if (targetType == boolean.class && value instanceof Boolean) {
            return true;
        } else if (targetType == byte.class && value instanceof Byte) {
            return true;
        } else if (targetType == short.class && value instanceof Short) {
            return true;
        } else if (targetType == int.class && value instanceof Integer) {
            return true;
        } else if (targetType == long.class && value instanceof Long) {
            return true;
        } else if (targetType == float.class && value instanceof Float) {
            return true;
        } else if (targetType == double.class && value instanceof Double) {
            return true;
        } else if (targetType == char.class && value instanceof Character) {
            return true;
        }
        return false;
    }
}
