import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 零依赖测试运行器：
 * 编译输出目录（-cp 第一个条目）中所有以 Test 结尾的类，
 * 运行其中所有以 test 开头、无参、返回 void 的 public 实例方法。
 * 用例失败（抛异常）即计入失败，最终以退出码 0/1 表示整体结果。
 *
 * 新增测试类/方法无需改本文件：命名符合约定即被发现。
 */
public final class TestRunner {

    private TestRunner() {
    }

    public static void main(String[] args) throws Exception {
        Path outDir = resolveOutDir();
        List<String> classNames = discoverTestClasses(outDir);
        if (classNames.isEmpty()) {
            System.out.println("没有发现任何测试类（类名需以 Test 结尾）");
            System.exit(1);
        }
        int pass = 0;
        int fail = 0;
        List<String> failures = new ArrayList<>();
        for (String className : classNames) {
            Class<?> testClass = Class.forName(className);
            Object instance = testClass.getDeclaredConstructor().newInstance();
            List<Method> methods = new ArrayList<>();
            for (Method method : testClass.getDeclaredMethods()) {
                if (method.getName().startsWith("test")
                        && method.getParameterCount() == 0
                        && method.getReturnType() == void.class
                        && !Modifier.isStatic(method.getModifiers())) {
                    methods.add(method);
                }
            }
            methods.sort(Comparator.comparing(Method::getName));
            for (Method method : methods) {
                String label = className + "." + method.getName();
                try {
                    method.setAccessible(true);
                    method.invoke(instance);
                    pass++;
                    System.out.println("✔ " + label);
                } catch (InvocationTargetException e) {
                    fail++;
                    Throwable cause = e.getCause();
                    String detail = cause == null ? "未知错误" : cause.toString();
                    failures.add(label + " -> " + detail);
                    System.out.println("✖ " + label + " -> " + detail);
                } catch (Exception e) {
                    fail++;
                    failures.add(label + " -> " + e);
                    System.out.println("✖ " + label + " -> " + e);
                }
            }
        }
        System.out.println();
        System.out.println("通过 " + pass + " / 失败 " + fail);
        if (!failures.isEmpty()) {
            System.out.println("失败明细：");
            failures.forEach(f -> System.out.println("  - " + f));
        }
        System.exit(fail == 0 ? 0 : 1);
    }

    private static Path resolveOutDir() {
        String classpath = System.getProperty("java.class.path", ".");
        String first = classpath.split(java.io.File.pathSeparator)[0];
        return Paths.get(first);
    }

    private static List<String> discoverTestClasses(Path outDir) throws IOException {
        List<String> names = new ArrayList<>();
        if (!Files.isDirectory(outDir)) {
            return names;
        }
        try (var stream = Files.list(outDir)) {
            stream.filter(p -> p.getFileName().toString().endsWith("Test.class"))
                    .map(p -> p.getFileName().toString().replace(".class", ""))
                    .filter(n -> !n.contains("$"))
                    .sorted()
                    .forEach(names::add);
        }
        return names;
    }
}
