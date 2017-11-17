import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

public class AttackRSA {

    public static void main(String[] args) {
        String filename = "input.txt";
        BigInteger[] N = new BigInteger[3];
        BigInteger[] e = new BigInteger[3];
        BigInteger[] c = new BigInteger[3];
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            for (int i = 0; i < 3; i++) {
                String line = br.readLine();
                String[] elem = line.split(",");
                N[i] = new BigInteger(elem[0].split("=")[1]);
                e[i] = new BigInteger(elem[1].split("=")[1]);
                c[i] = new BigInteger(elem[2].split("=")[1]);
            }
            br.close();
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
        }
        BigInteger m = recoverMessage(N, e, c);
        System.out.println("Recovered message: " + m);
        System.out.println("Decoded text: " + decodeMessage(m));
    }

    public static String decodeMessage(BigInteger m) {
        return new String(m.toByteArray());
    }

    /**
     * Tries to recover the message based on the three intercepted cipher texts.
     * In each array the same index refers to same receiver. I.e. receiver 0 has
     * modulus N[0], public key d[0] and received message c[0], etc.
     *
     * @param N
     *            The modulus of each receiver.
     * @param e
     *            The public key of each receiver (should all be 3).
     * @param c
     *            The cipher text received by each receiver.
     * @return The same message that was sent to each receiver.
     */
    private static BigInteger recoverMessage(BigInteger[] N, BigInteger[] e,
                                             BigInteger[] c) {

        BigInteger nProd = BigInteger.ONE;

        //Calculate the product, N1 * N2 * N3
        for(int i = 0; i < N.length; i++) {
            nProd = nProd.multiply(N[i]);
        }

        /*x will be the number that is the congruence
        x = c1 (mod N1)
        x = c2 (mod N2)
        x = c3 (mod N3)
        x can be solved with chinese remainder theorem
        */

        BigInteger x = BigInteger.ZERO;

        //Calculate the sum (c1 * modInv(N/N1, N1) * N/N1) + .... + (c3 * modInv(N/N3, N3) * N/N3)
        for(int i = 0; i < N.length; i++) {

            BigInteger div = nProd.divide(N[i]);
            BigInteger modInv = div.modInverse(N[i]);
            BigInteger add = c[i].multiply(modInv.multiply(div));
            x = x.add(add);
        }

        //m^3 will be congruence with x (mod N)
        BigInteger mCubic = x.mod(nProd);

        //m will be cubic root of mCubic
        return CubeRoot.cbrt(mCubic);
    }

}