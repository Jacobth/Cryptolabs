import java.io.BufferedReader;
import java.io.FileReader;

import javax.xml.bind.DatatypeConverter;

public class CBCXor {

    public static void main(String[] args) {
        String filename = "input.txt";
        byte[] first_block = null;
        byte[] encrypted = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            first_block = br.readLine().getBytes();
            encrypted = DatatypeConverter.parseHexBinary(br.readLine());
            br.close();
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
            System.exit(1);
        }
        String m = recoverMessage(first_block, encrypted);
        System.out.println("Recovered message: " + m);
    }

    private static final int BLOCK_SIZE = 12;

    /**
     * Recover the encrypted message (CBC encrypted with XOR, block size = 12).
     *
     * @param first_block
     *            We know that this is the value of the first block of plain
     *            text.
     * @param encrypted
     *            The encrypted text, of the form IV | C0 | C1 | ... where each
     *            block is 12 bytes long.
     */
    private static String recoverMessage(byte[] first_block, byte[] encrypted) {

        byte[] key = new byte[BLOCK_SIZE];
        byte[] decrypted = new byte[encrypted.length - BLOCK_SIZE];
        byte[] prev = new byte[BLOCK_SIZE];

        //Calculate the key from what we know
        for(int i = 0, j = BLOCK_SIZE; i < BLOCK_SIZE; i++, j++) {

            prev[i] = encrypted[i];

            //The encryption E is given by C0 = K + (M0 + IV)
            //K = C0 + (M0 + IV), from this we get the 12 byte key

            byte ivByte = encrypted[i];
            byte c0Byte = encrypted[j];
            byte m0Byte = first_block[i];

            //M0 + IV
            byte xor = (byte)(m0Byte ^ ivByte);

            //Get the key K
            key[i] = (byte)(xor ^ c0Byte);
        }

        //Loop through the encryption, skip the IV bytes
        for(int i = BLOCK_SIZE; i < encrypted.length; i+= BLOCK_SIZE) {

            int tmp = i;

            for(int j = 0; j < BLOCK_SIZE; j++) {

                //XOR the key and current cipher byte
                byte xor = (byte)(key[j] ^ encrypted[tmp]);
                // decrypt the message block with the previous cipher
                byte m = (byte)(xor ^ prev[j]);

                //replace ci-1 with ci
                prev[j] = encrypted[tmp];

                //Add the decrypted message block
                decrypted[tmp - BLOCK_SIZE] = m;
                tmp++;
            }

        }

        return new String(decrypted);
    }
}