package education.infoprotection;

/*    Реализовать двухступенчатый генератор псевдослучайных чисел. Первая ступень – конгруэнтный генератор
    с модулем 2^20 генерирует последовательность из 7 чисел, сумма которых является стартовым значением
    для второй ступени. Вторая ступень – генератор BBS с 64-битным модулем. Генератор BBS на основе значения,
    полученного из первой ступени, генерирует 5 чисел гаммы шифра, а затем передает старшие 20 бит последнего
    числа в первую ступень (в качестве порождающего значения), и процесс циклически повторяется. Реализовать
    гаммирование текста, состоящего из символов 128-символьной кодовой таблицы.*/

//6 (DES) 112 (хеш SHA256) 113 (хеш гост р 34.11-94) 23 (cast-256)

// выводить каждые 7 чисел, и каждые 5 чисел гаммы в двоичном виде
// взять текст на пару строк, чтобы на выводе можно было посмотреть
// двоичный вид открытого текста, гамму, должно помещаться примерно на 2-3 строки
// желательно выводить каждое число гаммы в двоичном виде при генерации чтобы
// было проще

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        BigInteger bg = new BigInteger("123456");
        System.out.println("CONGRUENTIAL START INPUT: " + bg.toString());
        EncryptionMachine encryptionMachine = new EncryptionMachine();
        String msg = "The advancement of technology has significantly transformed the way Swe live, " +
                "communicate, and work.";
        String msg2 = getMessage();
        byte[] originalBytes = msg.getBytes(StandardCharsets.UTF_8);
        encryptionMachine.encrypt(msg.getBytes(StandardCharsets.UTF_8), bg);
    }

    public static String getMessage() {
        Path filePath = Paths.get("original-text.txt");
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readBytesFromFile() throws IOException {
        return Files.readAllBytes(Paths.get("encrypted-binary.bin"));
    }
}