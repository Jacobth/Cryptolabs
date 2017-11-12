// Compilation (CryptoLibTest contains the main-method):
//   javac CryptoLibTest.java
// Running:
//   java CryptoLibTest

public class CryptoLib {

    /**
     * Returns an array "result" with the values "result[0] = gcd",
     * "result[1] = s" and "result[2] = t" such that "gcd" is the greatest
     * common divisor of "a" and "b", and "gcd = a * s + b * t".
     **/
    public static int[] EEA(int a, int b) {
        // Note: as you can see in the test suite,
        // your function should work for any (positive) value of a and b.
        int gcd = gcd(a, b);
        int s = -1;
        int t = -1;

        int x = 0;
        int y = 1;
        int prevX = 1;
        int prevY = 0;

        if(a == b) {
            s = 1;
            t = 0;
        }

        else {

            do {
                int q = a / b;
                int r = a % b;
                int tmp = x;

                a = b;
                b = r;

                x = prevX - q * x;
                prevX = tmp;

                tmp = y;
                y = prevY - q * y;
                prevY = tmp;

                s = prevX;
                t = prevY;
            }while((b != 0));

        }

        int[] result = new int[3];
        result[0] = gcd;
        result[1] = s;
        result[2] = t;

        return result;
    }

    /**
     * Returns Euler's Totient for value "n".
     **/
    public static int EulerPhi(int n) {

        int sum = 0;

        for(int i = 1; i <= n; i++) {
            if(gcd(n, i) == 1) {
                sum++;
            }
        }

        return sum;
    }

    /**
     * Returns the value "v" such that "n*v = 1 (mod m)". Returns 0 if the
     * modular inverse does not exist.
     **/
    public static int ModInv(int n, int m) {

        if(n < 0) {
            while(n < 0) {
                n += m;
            }
        }

        int[] eea = EEA(n, m);
        int s = eea[1];

        int v = (s % m + m) % m;

        return v;
    }

    /**
     * Returns 0 if "n" is a Fermat Prime, otherwise it returns the lowest
     * Fermat Witness. Tests values from 2 (inclusive) to "n/3" (exclusive).
     **/
    public static int FermatPT(int n) {

        for(int i = 2; i < n / 3; i++) {

            int a = i % (n - 1);

            if(gcd(n, i) == 1) {

                if(modPow(a, n - 1, n) != 1) {
                    return i;
                }
            }

            else {
                return i;
            }

        }

        return 0;
    }

    /**
     * Returns the probability that calling a perfect hash function with
     * "n_samples" (uniformly distributed) will give one collision (i.e. that
     * two samples result in the same hash) -- where "size" is the number of
     * different output values the hash function can produce.
     **/
    public static double HashCP(double n_samples, double size) {
        double pow = -Math.pow(n_samples, 2);
        double h = size * 2;
        double exp = pow / h;
        double e = Math.pow(Math.E, exp);
        double p = 1 - e;

        return p;
    }

    private static int gcd(int a, int b) {
        if (b == 0)
            return a;

        return gcd(b,a%b);
    }

    private static int modPow(int a , int p, int mod){
        int e = 1;

        for (int i = 0; i < p; i++) {
            e = ((e * a) % mod);

        }

        return e;
    }

}
