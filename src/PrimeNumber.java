import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class PrimeNumber
{
    private BigInteger primeNumber;

    PrimeNumber(int l)
    {
        primeNumber = bitInNumber(l, true);
        while (!checkPrime(primeNumber, l) || !checkMod(primeNumber))
        {
            primeNumber = bitInNumber(l, true);
        }

        //System.out.println("Простое число, которое удовлетворяет условию - " + primeNumber);
    }

    public BigInteger getPrimeNumber()
    {
        return this.primeNumber;
    }

    public int[] gengPrimeNumberBit(int l, boolean flag)
    {
        Random bit = new Random();

        int primeNumberBit[] = new int[l];
        primeNumberBit[l - 1] = 1;
        if (!flag) {
            primeNumberBit[0] = bit.nextInt(2);
        }
        else
            primeNumberBit[0] = 1;

        for (int i = 1; i < l - 1; i++)
        {
            primeNumberBit[i] = bit.nextInt(2);
        }

        return primeNumberBit;
    }

    public BigInteger bitInNumber(int l, boolean flag)
    {
        BigInteger mod = BigInteger.ONE;
        for (int i = 0; i < l; i++)
            mod = mod.multiply(BigInteger.TWO);

        BigInteger number = BigInteger.valueOf(0),
                deg = powMod(BigInteger.TWO, BigInteger.valueOf(l - 1), mod);
        int primeNumberBit[] = gengPrimeNumberBit(l, flag);

        for (int i = 0; i < l; i++)
        {
            number = number.add(deg.multiply(BigInteger.valueOf(primeNumberBit[i])));
            deg = deg.divide(BigInteger.TWO);
        }

        return number;
    }

    public boolean checkPrime(BigInteger p, int l)
    {
        BigInteger s = BigInteger.valueOf(0), n = p;

        if (p.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0)
            return false;

        BigInteger t = n.subtract(BigInteger.ONE);
        p = p.subtract(BigInteger.ONE);

        while (t.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0)
        {
            t = t.divide(BigInteger.TWO);
            s = s.add(BigInteger.ONE);
        }

        Random random = new Random();
        for (int i = 0; i < 35; i++)
        {
            BigInteger a = bitInNumber((random.nextInt(l - 2 + 1) + 2), false);
            BigInteger x = powMod(a, t, n);

            if (x.compareTo(BigInteger.ONE) == 0 || x.compareTo(n.subtract(BigInteger.ONE)) == 0)
                continue;


            BigInteger j = BigInteger.ZERO;
            while (j.compareTo(s) != 0)
            {
                x = powMod(x, BigInteger.TWO, n);

                if (x.compareTo(BigInteger.ONE) == 0)
                    return false;

                if (x.compareTo(p) == 0)
                    break;

                if (j.compareTo(s.subtract(BigInteger.ONE)) == 0)
                    return false;

                j = j.add(BigInteger.ONE);
            }
        }
        return true;
    }

    private boolean checkMod(BigInteger p)
    {
        if (p.mod(BigInteger.valueOf(4)).compareTo(BigInteger.ONE) == 0)
            return true;
        else
            return false;
    }

    public static BigInteger powMod(BigInteger a, BigInteger n, BigInteger m)
    {
        a = a.mod(m);
        BigInteger res = BigInteger.ONE;

        while (n.compareTo(BigInteger.ZERO) != 0)
        {
            if (n.mod(BigInteger.TWO).compareTo(BigInteger.ONE) != 0)
            {
                a = a.multiply(a);
                n = n.divide(BigInteger.TWO);
                a = a.mod(m);
            }
            else
            {
                res = a.multiply(res);
                n = n.subtract(BigInteger.ONE);
                res = res.mod(m);
            }
        }

        res = res.mod(m);
        return res;
    }

    BigInteger getRandomBigInteger(int lengthOfP, BigInteger p) {
        return new BigInteger(new Random().nextInt(lengthOfP + 1) + lengthOfP / 2, 200, new
                SecureRandom()).mod(p);
    }
}
