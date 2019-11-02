import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;

public class GengEllipticCurve
{
    static BigInteger N = null,
            r = null;


    public GengEllipticCurve() throws IOException {
        Scanner in = new Scanner(System.in);

        System.out.print("Введите длину характеристики поля l: ");
        int l = in.nextInt();
        System.out.print("Введите параметр безопасности m: ");
        int m = in.nextInt();

        PrimeNumber prime = null;
        boolean stop = false;
        while(!stop) {
            for (int i = 0; i < 50; i++) {
                prime = new PrimeNumber(l);
                Decomposition decomposition = new Decomposition(-1, prime.getPrimeNumber());
                if (!check3(prime, decomposition, l))
                    continue;
                if (check4(prime, m))
                    break;
                if (i == 49) {
                    System.out.println("Не выполнилось для данных l и m. Введите другие данные");
                    return;
                }
            }
            int repeat = 1000, it = 0;
            while (it < repeat) {
                if (!check5(prime))
                    continue;
                if (!check6(prime)) {
                    stop = true;
                    break;
                }
                it++;
            }
        }

        int j = N.divide(r).intValue();

        Pair Q = new Pair(x0, y0);
        for (int i = 1; i < j; i++)
        {
            Q = sum(Q, new Pair(x0, y0), prime.getPrimeNumber());
        }

        FileWriter out1 = new FileWriter("verifier.txt");
        out1.write(prime.getPrimeNumber() + "\n" + A + "\n" + Q.x + " " + Q.y + "\n" + r + "\n");

        FileWriter out2 = new FileWriter("applicant.txt");
        out2.write(prime.getPrimeNumber() + "\n" + A + "\n" + Q.x + " " + Q.y + "\n" + r + "\n");

        Files.deleteIfExists(new File("round.txt").toPath());
        FileWriter out3 = new FileWriter("round.txt");
        out3.write("0");
        out3.close();

        BigInteger rr = new BigInteger(r.bitLength() - 1, new Random()).mod(r);

        List<Pair> points = new ArrayList<>();
        Pair point = new Pair(Q.x, Q.y);
        points.add(point);
        for (BigInteger i = BigInteger.ONE; !i.equals(rr); i = i.add(BigInteger.ONE))
        {
            point = sum(point, Q, prime.getPrimeNumber());
            points.add(point);
        }

        out1.write(point.x + " " + point.y);

        out2.write(point.x + " " + point.y + "\n" + rr);

        out1.close();
        out2.close();
    }

    static boolean check3(PrimeNumber prime, Decomposition dec, int l)
    {
        BigInteger a = dec.getA(), b = dec.getB(),
                p = prime.getPrimeNumber();

        BigInteger N1 = p.add(BigInteger.ONE).add(a.multiply(BigInteger.valueOf(-2))),
                N2 = p.add(BigInteger.ONE).add(b.multiply(BigInteger.valueOf(-2))),
                N3 = p.add(BigInteger.ONE).add(a.multiply(BigInteger.valueOf(2))),
                N4 = p.add(BigInteger.ONE).add(b.multiply(BigInteger.valueOf(2)));

        if (checkNR(N1, l, prime))
            return true;
        if (checkNR(N2, l, prime))
            return true;
        if (checkNR(N3, l, prime))
            return true;
        if (checkNR(N4, l, prime))
            return true;

        return false;
    }

    static boolean checkNR(BigInteger Ni, int l, PrimeNumber prime)
    {
        if ((Ni.mod(BigInteger.TWO)).equals(BigInteger.ZERO) && prime.checkPrime(Ni.divide(BigInteger.TWO), l))
        {
            N = Ni;
            r = Ni.divide(BigInteger.TWO);
            return true;
        }
        if ((Ni.mod(BigInteger.valueOf(4))).equals(BigInteger.ZERO) && prime.checkPrime(Ni.divide(BigInteger.valueOf(4)), l))
        {
            N = Ni;
            r = Ni.divide(BigInteger.valueOf(4));
            return true;
        }
        return false;
    }

    static boolean check4(PrimeNumber prime, int m)
    {
        BigInteger p = prime.getPrimeNumber();

        if (p.equals(r))
            return false;

        for (int i = 1; i <= m; i++)
        {
            if (PrimeNumber.powMod(p, BigInteger.valueOf(i), r).equals(BigInteger.ONE))
                return false;
        }
        return true;
    }

    static BigInteger x0 = null, y0 = null, A = null;

    static boolean check5(PrimeNumber prim)
    {
        BigInteger p = prim.getPrimeNumber();
        do {
            x0 = prim.getRandomBigInteger(p.bitLength(), p);
        } while(!x0.gcd(p).equals(BigInteger.ONE));

        y0 = prim.getRandomBigInteger(p.bitLength(), p);

        A = ((Decomposition.pow(y0, BigInteger.TWO).subtract(Decomposition.pow(x0, BigInteger.valueOf(3)))).multiply(x0.modInverse(p))).mod(p);
        BigInteger A_ = p.subtract(A),
                N1 = r.multiply(BigInteger.TWO),
                N2 = r.multiply(BigInteger.valueOf(4));

        if (N1.equals(N) && Decomposition.legendreSymbol(A_, p) != 1)
            return true;
        if (N2.equals(N) && Decomposition.legendreSymbol(A_, p) == 1)
            return true;

        return false;
    }

    static boolean check6(PrimeNumber prim)
    {
        BigInteger p = prim.getPrimeNumber();
        Pair point0 = new Pair(x0, y0);
        Set<String> points = new HashSet<>();
        points.add(point0.x + "" + point0.y);
        Pair point = point0;

        for (BigInteger i = BigInteger.ONE; i.compareTo(N.subtract(BigInteger.ONE)) < 0; i =
                i.add(BigInteger.ONE))
        {
            point = sum(point, point0, p);
            if (point == null)
                return true;
            if (points.contains(point.x + "" + point.y))
                return true;
            points.add(point.x + "" + point.y);
        }

        return sum(point, point0, p) != null;
    }

    static Pair sum(Pair x1y1, Pair x2y2, BigInteger p)
    {
        try {
            BigInteger x1 = x1y1.x, y1 = x1y1.y, x2 = x2y2.x, y2 = x2y2.y, alph;
            if (x1y1 == null)
                return null;

            /*    System.out.println(x1 + "   " + x2 + "   " + y1 + "   " + y2);*/
            if (x1.equals(x2) && y1.equals(y2)) {
                if (y1.equals(BigInteger.ZERO))
                    return null;
                else
                    alph = ((x1.multiply(x1).multiply(BigInteger.valueOf(3)).add(A)).multiply((BigInteger.TWO.multiply(y1)).modInverse(p))).mod(p);
            } else
                alph = ((y2.subtract(y1)).multiply((x2.subtract(x1)).modInverse(p))).mod(p);

            BigInteger x3 = (alph.multiply(alph).subtract(x1).subtract(x2)).mod(p),
                    y3 = ((x1.subtract(x3)).multiply(alph).subtract(y1)).mod(p);

            return new Pair(x3, y3);
        }
        catch(ArithmeticException e) {
            return null;
        }
    }

}