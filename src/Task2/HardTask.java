package Task2;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.time.Instant;

/**
 * Тема: Засвоєння структури JVM. Написання власного завантажувача класів.
 * <p>
 * Стежить за файлом {@code TestModule.java}. Щойно файл змінюється,
 * перекомпільовує його та підвантажує новий байт-код у JVM через
 * свіжий екземпляр {@link CustomClassLoader}, після чого одразу
 * друкує новий результат {@code toString()} класу {@code TestModule}
 * — без перезапуску самої програми.
 */
public class HardTask {

    private static final String CLASS_NAME = "Task2.TestModule";
    private static final File SOURCE_FILE = new File("src" + File.separator + "Task2" + File.separator + "TestModule.java");
    private static final File OUTPUT_DIR = new File("out" + File.separator + "production" + File.separator + "JavaLab1");
    private static final long POLL_INTERVAL_MS = 1000;

    public static void main(String[] args) {
        if (!OUTPUT_DIR.exists() && !OUTPUT_DIR.mkdirs()) {
            System.err.println("Не вдалося створити каталог для скомпільованих класів: " + OUTPUT_DIR.getAbsolutePath());
            return;
        }

        System.out.println("Запуск моніторингу " + CLASS_NAME + "...");
        System.out.println("Джерело: " + SOURCE_FILE.getAbsolutePath());
        System.out.println("Класи:   " + OUTPUT_DIR.getAbsolutePath());
        System.out.println("PID процесу:    " + ProcessHandle.current().pid());
        System.out.println("Старт JVM:      " + Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime()));
        System.out.println("(PID і час старту НЕ повинні змінюватись між перезавантаженнями - це доказ, що процес не перезапускався)\n");

        long lastModified = 0;

        while (true) {
            if (SOURCE_FILE.exists()) {
                long currentModified = SOURCE_FILE.lastModified();
                if (currentModified > lastModified) {
                    lastModified = currentModified;
                    reloadModule();
                }
            } else {
                System.err.println("Файл " + SOURCE_FILE.getAbsolutePath() + " не знайдено!");
            }

            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                System.out.println("Роботу програми зупинено.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void reloadModule() {
        System.out.println("\n[Подія] Виявлено зміни у " + SOURCE_FILE.getName() + ". Перекомпільовуємо...");

        if (!compile(SOURCE_FILE, OUTPUT_DIR)) {
            System.err.println("Помилка компіляції файлу " + SOURCE_FILE.getName());
            return;
        }

        try {
            // Новий CustomClassLoader на кожну зміну - саме це і дає "гаряче" перезавантаження.
            CustomClassLoader classLoader = new CustomClassLoader(OUTPUT_DIR);
            Class<?> testModuleClass = classLoader.loadClass(CLASS_NAME);
            Object instance = testModuleClass.getDeclaredConstructor().newInstance();
            System.out.println(instance);
        } catch (Exception e) {
            System.err.println("Помилка завантаження або створення екземпляра: " + e.getMessage());
        }
    }

    private static boolean compile(File sourceFile, File outputDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler != null) {
            int result = compiler.run(null, null, null, "-d", outputDir.getAbsolutePath(), sourceFile.getAbsolutePath());
            return result == 0;
        }
        try {
            Process process = new ProcessBuilder(
                    "javac", "-d", outputDir.getAbsolutePath(), sourceFile.getAbsolutePath()
            ).inheritIO().start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            System.err.println("Помилка виклику javac: " + e.getMessage());
            return false;
        }
    }
}
