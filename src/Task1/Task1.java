package Task1;

// Знайти ті слова, які містять тільки символи латинського алфавіту.
// Серед них знайти ті слова, які містять рівну кількість голосних
// та приголосних. На вхід поступає рядок із словами.
// На виході - масив String.

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Task1 {

    private static final String VOWELS = "AEIOUaeiou";

    // Довший тестовий рядок - використовується, якщо нічого не ввести (Enter одразу).
    private static final String DEFAULT_INPUT =
            "Java hello test123 привіт av lava sun moon dog cat friend java123 " +
            "tab lit sit bit code data Kyiv AI robot map key box fox cow now wow yes set";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть рядок зі словами (або натисніть Enter, щоб узяти приклад):");
        String line = scanner.nextLine();

        if (line.isBlank()) {
            line = DEFAULT_INPUT;
            System.out.println("Використовую приклад:\n" + line);
        }

        String[] latinOnly = findLatinOnlyWords(line);
        String[] result = findWordsWithEqualVowelsAndConsonants(latinOnly);

        System.out.println("\nСлова лише з латинських літер (" + latinOnly.length + "):");
        printArray(latinOnly);

        System.out.println("\nЗ них - слова з рівною кількістю голосних та приголосних (" + result.length + "):");
        printArray(result);
    }

    /** Повертає слова, які складаються виключно з літер латинського алфавіту (a-z, A-Z). */
    public static String[] findLatinOnlyWords(String input) {
        List<String> result = new ArrayList<>();

        if (input == null || input.isBlank()) {
            return result.toArray(new String[0]);
        }

        for (String word : input.trim().split("\\s+")) {
            if (isLatinOnly(word)) {
                result.add(word);
            }
        }

        return result.toArray(new String[0]);
    }

    /** З уже відфільтрованих латинських слів залишає ті, де голосних стільки ж, скільки приголосних. */
    public static String[] findWordsWithEqualVowelsAndConsonants(String[] latinWords) {
        List<String> result = new ArrayList<>();

        for (String word : latinWords) {
            if (hasEqualVowelsAndConsonants(word)) {
                result.add(word);
            }
        }

        return result.toArray(new String[0]);
    }

    /** Зручний виклик "рядок -> одразу результат", без проміжного масиву. */
    public static String[] findLatinWordsWithEqualVowelsAndConsonants(String input) {
        return findWordsWithEqualVowelsAndConsonants(findLatinOnlyWords(input));
    }

    /** Перевіряє, що слово складається лише з літер латинського алфавіту. */
    private static boolean isLatinOnly(String word) {
        if (word.isEmpty()) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            boolean isLatinLetter = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            if (!isLatinLetter) {
                return false;
            }
        }
        return true;
    }

    /** Перевіряє, що кількість голосних дорівнює кількості приголосних у слові. */
    private static boolean hasEqualVowelsAndConsonants(String word) {
        int vowels = 0;
        int consonants = 0;

        for (int i = 0; i < word.length(); i++) {
            if (VOWELS.indexOf(word.charAt(i)) >= 0) {
                vowels++;
            } else {
                consonants++;
            }
        }

        return vowels == consonants;
    }

    private static void printArray(String[] words) {
        if (words.length == 0) {
            System.out.println("(немає)");
            return;
        }
        for (String word : words) {
            System.out.println("  " + word);
        }
    }
}
