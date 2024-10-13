package education.infoprotection;

/*    Реализовать двухступенчатый генератор псевдослучайных чисел. Первая ступень – конгруэнтный генератор
    с модулем 2^20 генерирует последовательность из 7 чисел, сумма которых является стартовым значением
    для второй ступени. Вторая ступень – генератор BBS с 64-битным модулем. Генератор BBS на основе значения,
    полученного из первой ступени, генерирует 5 чисел гаммы шифра, а затем передает старшие 20 бит последнего
    числа в первую ступень (в качестве порождающего значения), и процесс циклически повторяется. Реализовать
    гаммирование текста, состоящего из символов 128-символьной кодовой таблицы.*/


import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        EncryptionMachine encryptionMachine = new EncryptionMachine();
        String msg = encryptionMachine.encrypt("The advancement of technology has significantly transformed the way we live, " +
                "communicate, and work. In the modern world, the integration of artificial intelligence, machine learning, " +
                "and automation into various industries has led to increased efficiency and productivity. As these technologies " +
                "continue to evolve, they are not only reshaping business operations but also influencing our daily lives. " +
                "From smart home devices that simplify household tasks to sophisticated algorithms that enhance decision-making " +
                "processes, technology is becoming an integral part of our existence.", new BigInteger("1"));
        return;
    }
}