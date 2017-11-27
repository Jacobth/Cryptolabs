import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

public class FiatShamir {

    public static class ProtocolRun {
        public final BigInteger R;
        public final int c;
        public final BigInteger s;

        public ProtocolRun(BigInteger R, int c, BigInteger s) {
            this.R = R;
            this.c = c;
            this.s = s;
        }
    }

    public static void main(String[] args) {
        String filename = "input.txt";
        BigInteger N = BigInteger.ZERO;
        BigInteger X = BigInteger.ZERO;
        ProtocolRun[] runs = new ProtocolRun[10];
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            N = new BigInteger(br.readLine().split("=")[1]);
            X = new BigInteger(br.readLine().split("=")[1]);
            for (int i = 0; i < 10; i++) {
                String line = br.readLine();
                String[] elem = line.split(",");
                runs[i] = new ProtocolRun(
                        new BigInteger(elem[0].split("=")[1]),
                        Integer.parseInt(elem[1].split("=")[1]),
                        new BigInteger(elem[2].split("=")[1]));
            }
            br.close();
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
            System.exit(1);
        }
        BigInteger m = recoverSecret(N, X, runs);
        System.out.println("Recovered message: " + m);
        System.out.println("Decoded text: " + decodeMessage(m));
    }

    public static String decodeMessage(BigInteger m) {
        return new String(m.toByteArray());
    }

    /**
     * Recovers the secret used in this collection of Fiat-Shamir protocol runs.
     *
     * @param N
     *            The modulus
     * @param X
     *            The public component
     * @param runs
     *            Ten runs of the protocol.
     * @return
     */
    private static BigInteger recoverSecret(BigInteger N, BigInteger X,
                                            ProtocolRun[] runs) {

        ProtocolRun r1 = new ProtocolRun(BigInteger.ZERO,0,BigInteger.ZERO);
        ProtocolRun r2 = new ProtocolRun(BigInteger.ZERO,0,BigInteger.ZERO);

        //Find the two protocols with the same R
        for(int i = 0; i < runs.length; i++) {

            for(int j = 0; j < runs.length; j++) {

                if(j != i) {

                    if(runs[i].R.equals(runs[j].R)) {
                        r1 = runs[i];
                        r2 = runs[j];
                    }
                }
            }
        }

        /*s1 * s2 (mod N) = R * (s^1) * (s^0) (mod N) ->
        (R^-1) * s1 * s2 (mod N) = s (mod N).*/
        BigInteger modInv = r1.R.modInverse(N);
        BigInteger x = r1.s.multiply(r2.s).multiply(modInv).mod(N);

        return x;
    }
}