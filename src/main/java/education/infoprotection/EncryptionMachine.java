package education.infoprotection;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class EncryptionMachine {

    CongruentialGenerator congruentialGenerator;
    BlumBlumShubMachine blumBlumShubMachine;
    List<byte[]> messageBlocks;
    List<byte[]> gammaBlocks;
    List<byte[]> encryptedBlocks;
    // в итоге надо не переводить зашифрованные блоки в стринг, а записать биты в файл, при
    // дешифровки вытаскивать биты и гаммировать

    public byte[] encrypt(byte[] message, BigInteger start) {

        messageBlocks = new ArrayList<>();
        gammaBlocks = new ArrayList<>();
        splitBytesToBlocks(message);
        congruentialGenerator = new CongruentialGenerator(start);
        blumBlumShubMachine = new BlumBlumShubMachine(new BigInteger("4000000007"),
                new BigInteger("5000000009"));

        while (gammaBlocks.size() < messageBlocks.size()) {
            makeGamma();
        }

        byte[] messageBlock;
        byte[] gammaBlock;
        byte[] encryptedBlock;
        encryptedBlocks = new ArrayList<>();
        for (int i = 0; i < messageBlocks.size(); i++) {
            messageBlock = messageBlocks.get(i);
            gammaBlock = gammaBlocks.get(i);

            encryptedBlock = new byte[messageBlock.length];

            for (int j = 0; j < messageBlock.length; j++) {
                encryptedBlock[j] = (byte) (messageBlock[j] ^ gammaBlock[j]);
            }

            encryptedBlocks.add(encryptedBlock);
        }

        byte[] res = new byte[encryptedBlocks.size() * encryptedBlocks.get(0).length];
        for (int i = 0, k = 0; i < encryptedBlocks.size(); i++) {
            for (int j = 0; j < encryptedBlocks.get(0).length; j++) {
                res[k++] = encryptedBlocks.get(i)[j];
            }
        }

        printEncrypted();
        return res;
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

    private void splitBytesToBlocks(byte[] message) {
        int blockSize = 8;
        int end;
        byte[] block;
        for (int i = 0; i < message.length; i += blockSize) {
            end = Math.min(message.length, i + blockSize);
            block = new byte[blockSize];

            System.arraycopy(message, i, block, 0, end - i); // копируем блок байт из стринга

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

    //TODO
    private void printEncrypted() {
        System.out.println("ENCRYPTED BINARY");
        for (byte[] bytes : encryptedBlocks) {
            System.out.println(bytes.toString());
        }
    }

    private void printGamma() {

    }

    private void printGammaToFile() {

    }

    private void printEncryptedToFile() {

    }
}