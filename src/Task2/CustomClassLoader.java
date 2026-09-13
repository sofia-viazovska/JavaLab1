package Task2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Власний ClassLoader, який підвантажує байт-код класу {@link #RELOADABLE_CLASS}
 * напряму з файлової системи, не делегуючи це батьківському завантажувачу.
 * <p>
 * Завдяки цьому щоразу, коли створюється НОВИЙ екземпляр {@code CustomClassLoader},
 * JVM отримує окремий, "свіжий" клас із щойно перекомпільованим байт-кодом —
 * навіть якщо ім'я класу лишається тим самим.
 */
public class CustomClassLoader extends ClassLoader {

    /** Повне (з пакетом) ім'я класу, який ми хочемо "гаряче" перезавантажувати. */
    private static final String RELOADABLE_CLASS = "Task2.TestModule";

    /** Каталог, у якому лежать скомпільовані .class файли (з підпапками пакетів). */
    private final File classesDir;

    public CustomClassLoader(File classesDir) {
        super(CustomClassLoader.class.getClassLoader());
        this.classesDir = classesDir;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.equals(RELOADABLE_CLASS)) {
            // Не делегуємо батьківському завантажувачу - інакше JVM віддала б
            // раніше закешовану версію класу замість нової.
            return findClass(name);
        }
        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File classFile = new File(classesDir, name.replace('.', File.separatorChar) + ".class");
        if (!classFile.exists()) {
            throw new ClassNotFoundException("Файл байт-коду не знайдено: " + classFile.getAbsolutePath());
        }
        try {
            byte[] bytes = Files.readAllBytes(classFile.toPath());
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Не вдалося прочитати байт-код класу " + name, e);
        }
    }
}
