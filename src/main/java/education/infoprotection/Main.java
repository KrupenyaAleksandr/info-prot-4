package education.infoprotection;

/*    Реализовать двухступенчатый генератор псевдослучайных чисел. Первая ступень – конгруэнтный генератор
    с модулем 2^20 генерирует последовательность из 7 чисел, сумма которых является стартовым значением
    для второй ступени. Вторая ступень – генератор BBS с 64-битным модулем. Генератор BBS на основе значения,
    полученного из первой ступени, генерирует 5 чисел гаммы шифра, а затем передает старшие 20 бит последнего
    числа в первую ступень (в качестве порождающего значения), и процесс циклически повторяется. Реализовать
    гаммирование текста, состоящего из символов 128-символьной кодовой таблицы.*/


import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        EncryptionMachine encryptionMachine = new EncryptionMachine();
        String msg = "The advancement of technology has significantly transformed the way we live, " +
                "communicate, and work. In the modern world, the integration of artificial intelligence, machine learning, " +
                "and automation into various industries has led to increased efficiency and productivity. As these technologies " +
                "continue to evolve, they are not only reshaping business operations but also influencing our daily lives. " +
                "From smart home devices that simplify household tasks to sophisticated algorithms that enhance decision-making " +
                "processes, technology is becoming an integral part of our existence.";
        String msg2 = getMessage();
        byte[] originalBytes = msg.getBytes(StandardCharsets.UTF_8);
        encryptionMachine.encrypt(msg.getBytes(StandardCharsets.UTF_8),BigInteger.ONE);
/*        try {
            encryptionMachine.encrypt(readBytesFromFile(), BigInteger.ONE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
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