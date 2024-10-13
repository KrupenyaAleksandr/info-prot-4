package education.infoprotection;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EncryptionMachine {

    CongruentialGenerator congruentialGenerator;
    BlumBlumShubMachine blumBlumShubMachine;
    List<byte[]> messageBlocks = new ArrayList<>();
    List<byte[]> gammaBlocks = new ArrayList<>();
    // в итоге надо не переводить зашифрованные блоки в стринг, а записать биты в файл, при
    // дешифровки вытаскивать биты и гаммировать

    public String encrypt(String message, BigInteger start) {
        splitStringToBlocks(message);
        congruentialGenerator = new CongruentialGenerator(start);
        blumBlumShubMachine = new BlumBlumShubMachine(new BigInteger("4000000007"),
                new BigInteger("5000000009"));

        while (gammaBlocks.size() < messageBlocks.size()) {
            makeGamma();
        }

        byte[] messageBlock;
        byte[] gammaBlock;
        byte[] encryptedBlock;
        List<byte[]> encryptedBlocks = new ArrayList<>();
        for (int i = 0; i < messageBlocks.size(); i++) {
            messageBlock = messageBlocks.get(i);
            gammaBlock = gammaBlocks.get(i);

            encryptedBlock = new byte[messageBlock.length];

            for (int j = 0; j < messageBlock.length; j++) {
                encryptedBlock[j] = (byte) (messageBlock[j] ^ gammaBlock[j]);
            }

            encryptedBlocks.add(encryptedBlock);
        }

        encrypt(encryptedBlocks);
        return convertToString(encryptedBlocks);
    }

    private void encrypt(List<byte[]> list) {
        byte[] messageBlock;
        byte[] gammaBlock;
        byte[] encryptedBlock;
        List<byte[]> encryptedBlocks = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            messageBlock = list.get(i);
            gammaBlock = gammaBlocks.get(i);

            encryptedBlock = new byte[messageBlock.length];

            for (int j = 0; j < messageBlock.length; j++) {
                encryptedBlock[j] = (byte) (messageBlock[j] ^ gammaBlock[j]);
            }

            encryptedBlocks.add(encryptedBlock);
        }

        System.out.println(convertToString(encryptedBlocks));
    }

    public String convertToString(List<byte[]> byteBlocks) {
        StringBuilder result = new StringBuilder();

        // Цикл по каждому блоку байтов
        for (byte[] block : byteBlocks) {
            // Преобразуем байты в строку, используя кодировку UTF-8
            result.append(new String(block, StandardCharsets.UTF_8));
        }

        return result.toString(); // Возвращаем объединенную строку
    }

    private void makeGamma() {
        BigInteger sum = new BigInteger("0");
        for (int i = 0; i < 7; i++) { // собираем сумму
            sum = sum.add(congruentialGenerator.next());
        }

        blumBlumShubMachine.setState(sum);

        BigInteger bigInteger = new BigInteger("0");
        for (int i = 0; i < 5; i++) { //собираем гамму
            bigInteger = blumBlumShubMachine.next();
            gammaBlocks.add(toByteArray64(bigInteger));
        }

        // берём 20 бит и вычитаем один
        BigInteger mask = BigInteger.valueOf((1L << 20) - 1);
        // инвертируем
        BigInteger highBits = bigInteger.shiftRight(bigInteger.bitLength() - 20).and(mask);
        congruentialGenerator.setState(highBits);
    }

    private void splitStringToBlocks(String message) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        int blockSize = 8;
        int end;
        byte[] block;
        for (int i = 0; i < messageBytes.length; i += blockSize) {
            end = Math.min(messageBytes.length, i + blockSize);
            block = new byte[blockSize];

            System.arraycopy(messageBytes, i, block, 0, end - i); // копируем блок байт из стринга

            if (end - i < blockSize) { // если не хватило, добавляем нули
                for (int j = end - 1; j < blockSize; j++) {
                    block[j] = 0;
                }
            }

            messageBlocks.add(block);
        }
    }

    private byte[] toByteArray64(BigInteger value) {
        byte[] byteArray = value.toByteArray();
        byte[] result = new byte[8];

        // если длина массива меньше 8, дополняем нулями спереди
        System.arraycopy(byteArray, Math.max(0, byteArray.length - 8), result,
                Math.max(0, 8 - byteArray.length), Math.min(8, byteArray.length));

        return result;
    }
}