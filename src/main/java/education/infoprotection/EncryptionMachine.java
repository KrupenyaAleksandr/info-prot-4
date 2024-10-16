package education.infoprotection;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class EncryptionMachine {

    CongruentialGenerator congruentialGenerator;
    BlumBlumShubMachine blumBlumShubMachine;
    List<byte[]> messageBlocks;
    List<byte[]> gammaBlocks;
    List<byte[]> encryptedBlocks;
    int counter = 0;
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

        printGammaBinary();
        printGammaString();
        printGammaToFile();

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

        printEncryptedBinary();
        printEncryptedString();
        printEncryptedToFile();
        return res;
    }

    private void makeGamma() {
        System.out.println("ITERATION: " + counter);
        int congrCount = 0;
        BigInteger sum = new BigInteger("0");
        BigInteger nextCongr = new BigInteger("0");
        for (int i = 0; i < 7; i++) { // собираем сумму
            nextCongr = congruentialGenerator.next();
            sum = sum.add(nextCongr);
            System.out.println("CONGRUENTIAL STEP " + (congrCount + 1) + ", NUMBER: " + nextCongr);
            congrCount++;
        }

        System.out.println("BLUMBLUMSHUB INPUT: " + sum.toString());
        blumBlumShubMachine.setState(sum);

        BigInteger bigInteger = new BigInteger("0");
        int bbsCount = 0;
        for (int i = 0; i < 5; i++) { //собираем гамму
            bigInteger = blumBlumShubMachine.next();
            System.out.println("BLUMBLUMSHUB STEP " + (bbsCount + 1) + ", NUMBER: " + bigInteger);
            gammaBlocks.add(toByteArray64(bigInteger));
            bbsCount++;
        }

        // берём 20 бит и вычитаем один
        BigInteger mask = BigInteger.valueOf((1L << 20) - 1);
        // инвертируем
        BigInteger highBits = bigInteger.shiftRight(bigInteger.bitLength() - 20).and(mask);
        System.out.println("CONGRUENTIAL ITERATION: "
                + counter + ", NEW INPUT: " + highBits.toString());
        congruentialGenerator.setState(highBits);
        counter++;
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

        for (byte b : result) {
            System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        System.out.println();
        return result;
    }

    private void printEncryptedBinary() {
        System.out.println("\nENCRYPTED BINARY\n");
        for (byte[] bytes : encryptedBlocks) {
            for (byte b : bytes) {
                System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            }
            System.out.println();
        }
    }

    private void printEncryptedString() {
        System.out.println("\nENCRYPTED STRING\n");
        for (byte[] bytes : encryptedBlocks) {
                System.out.print(new String(bytes));
        }
        System.out.println();
    }

    private void printGammaBinary() {
        System.out.println("\nGAMMA BINARY\n");
        for (byte[] bytes : gammaBlocks) {
            for (byte b : bytes) {
                System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            }
            System.out.println();
        }
    }

    private void printGammaString() {
        System.out.println("\nGAMMA STRING\n");
        for (byte[] bytes : gammaBlocks) {
            System.out.print(new String(bytes));
        }
        System.out.println();
    }

    private void printGammaToFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream("gamma-binary.bin")){
            for (byte[] bytes : gammaBlocks) {
                fileOutputStream.write(bytes);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fileWriter = new FileWriter("gamma-text.txt")) {
            for (byte[] bytes : gammaBlocks) {
                fileWriter.write(new String(bytes));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printEncryptedToFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream("encrypted-binary.bin")){
            for (byte[] bytes : encryptedBlocks) {
                fileOutputStream.write(bytes);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fileWriter = new FileWriter("encrypted-text.txt")) {
            for (byte[] bytes : encryptedBlocks) {
                fileWriter.write(new String(bytes));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}