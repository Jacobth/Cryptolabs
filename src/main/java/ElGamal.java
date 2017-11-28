import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;


public class ElGamal {

    public static String decodeMessage(BigInteger m) {
        return new String(m.toByteArray());
    }

    public static void main(String[] arg) {
        String filename = "input.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            BigInteger p = new BigInteger(br.readLine().split("=")[1]);
            BigInteger g = new BigInteger(br.readLine().split("=")[1]);
            BigInteger y = new BigInteger(br.readLine().split("=")[1]);
            String line = br.readLine().split("=")[1];
            String date = line.split(" ")[0];
            String time = line.split(" ")[1];
            int year  = Integer.parseInt(date.split("-")[0]);
            int month = Integer.parseInt(date.split("-")[1]);
            int day   = Integer.parseInt(date.split("-")[2]);
            int hour   = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            int second = Integer.parseInt(time.split(":")[2]);
            BigInteger c1 = new BigInteger(br.readLine().split("=")[1]);
            BigInteger c2 = new BigInteger(br.readLine().split("=")[1]);
            br.close();
            BigInteger m = recoverSecret(p, g, y, year, month, day, hour, minute,
                    second, c1, c2);
            System.out.println("Recovered message: " + m);
            System.out.println("Decoded text: " + decodeMessage(m));
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
            System.exit(1);
        }
    }

    private static final int MILLI_MAX = 1000;

    public static BigInteger recoverSecret(BigInteger p, BigInteger g,
                                           BigInteger y, int year, int month, int day, int hour, int minute,
                                           int second, BigInteger c1, BigInteger c2) {

        BigInteger m;
        long r = 0;

        //From the give time we don't know milliseconds, a value between 0 and 999.
        for(int i = 0; i < MILLI_MAX; i++) {

            r = getRandom(year, month, day, hour, minute, second, i);

            //(g^r)(mod p)
            BigInteger gPow = pow(g, r, p).mod(p);

            //We know we found the right r if g^r = c1
            if(gPow.equals(c1)) {
                break;
            }
        }

        //c2 = m * (h^r) -> m = c2 * ((h^r)^-1)
        BigInteger yPow = pow(y, r, p);
        BigInteger modInv = yPow.modInverse(p);
        m = c2.multiply(modInv).mod(p);

        return m;
    }

    private static long getRandom(int year, int month, int day, int hour, int minute, int second, int millisec) {

        BigInteger y = BigInteger.valueOf(10).pow(10).multiply(BigInteger.valueOf(year));
        BigInteger m = BigInteger.valueOf(10).pow(8).multiply(BigInteger.valueOf(month));
        BigInteger d = BigInteger.valueOf(10).pow(6).multiply(BigInteger.valueOf(day));
        BigInteger h = BigInteger.valueOf(10).pow(4).multiply(BigInteger.valueOf(hour));
        BigInteger mi = BigInteger.valueOf(10).pow(2).multiply(BigInteger.valueOf(minute));
        BigInteger s = BigInteger.valueOf(second);
        BigInteger ms = BigInteger.valueOf(millisec);

        BigInteger x = y.add(m).add(d).add(h).add(mi).add(s).add(ms);

        Long tmp = new Long(x.toString());

        return tmp;
    }

    private static BigInteger pow (BigInteger a, long b, BigInteger p) {

        a = a.mod(p);

        if (b == 0)
            return BigInteger.ONE;

        if (b == 1)
            return a;

        if (isEven(b))
            return pow(a.multiply(a), b/2, p);

        else
            return a.multiply(pow(a.multiply(a), b/2, p));

    }

    private static boolean isEven(long b) {
        return (b % 2) == 0;
    }

}