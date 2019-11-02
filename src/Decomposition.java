import java.math.BigInteger;
import java.util.ArrayList;

public class Decomposition
{
    private BigInteger a, b;

    public BigInteger getA()
    {
        return a;
    }

    public BigInteger getB()
    {
        return b;
    }

    Decomposition(int D, BigInteger p)
    {
        int legSymbol = legendreSymbol(BigInteger.valueOf(D), p);
        if (legSymbol == 1)
        {
            BigInteger x1 = sqrt(BigInteger.valueOf(D), p),
                    x2 = p.subtract(x1);
            // System.out.println("Корни из -1 в поле " + p + ": " + x1 + " и " + x2);

            result(x1, x2, p, 1, false);
            //System.out.println("Разложение р на простые множители в кольце: " + a + " и " + b);
        }
    }

    private static BigInteger GCD(BigInteger a, BigInteger b)
    {
        BigInteger g = BigInteger.ONE;
        while (a.mod(BigInteger.TWO).equals(BigInteger.ZERO) &&
                b.mod(BigInteger.TWO).equals(BigInteger.ZERO))
        {
            a = a.divide(BigInteger.TWO);
            b = b.divide(BigInteger.TWO);
            g = g.multiply(BigInteger.TWO);
        }

        BigInteger u = a, v = b;
        while(!u.equals(BigInteger.ZERO))
        {
            while(u.mod(BigInteger.TWO).equals(BigInteger.ZERO))
                u = u.divide(BigInteger.TWO);
            while(v.mod(BigInteger.TWO).equals(BigInteger.ZERO))
                v = v.divide(BigInteger.TWO);
            if (u.compareTo(v) == -1)
                v = v.subtract(u);
            else
                u = u.subtract(v);
        }
        return g.multiply(v);
    }

    public static BigInteger pow(BigInteger a, BigInteger n)
    {
        BigInteger res = BigInteger.ONE;

        while (n.compareTo(BigInteger.ZERO) != 0)
        {
            if (n.mod(BigInteger.TWO).compareTo(BigInteger.ONE) != 0)
            {
                a = a.multiply(a);
                n = n.divide(BigInteger.TWO);
            }
            else
            {
                res = a.multiply(res);
                n = n.subtract(BigInteger.ONE);

            }
        }
        return res;
    }

    public static int legendreSymbol(BigInteger a, BigInteger b)
    {
        BigInteger aa = BigInteger.ONE;
        if (a.compareTo(BigInteger.ZERO) == -1)
            aa = a.add(b);

        if (GCD(aa, b).compareTo(BigInteger.ONE) != 0)
            return 0;

        BigInteger r = BigInteger.ONE;
        if (a.compareTo(BigInteger.ZERO) == -1)
        {
            a = a.multiply(BigInteger.valueOf(-1));
            if (b.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3)) == 0)
                r = r.multiply(BigInteger.valueOf(-1));
        }

        while(a.compareTo(BigInteger.ZERO) != 0)
        {
            BigInteger t = BigInteger.ZERO;
            while(a.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0)
            {
                t = t.add(BigInteger.ONE);
                a = a.divide(BigInteger.TWO);
            }

            if (t.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) != 0)
                if ((b.mod(BigInteger.valueOf(8)).compareTo(BigInteger.valueOf(3)) == 0) ||
                        b.mod(BigInteger.valueOf(8)).compareTo(BigInteger.valueOf(5)) == 0)
                {
                    r = r.multiply(BigInteger.valueOf(-1));
                }

            if (a.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3)) == 0 &&
                    b.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3)) == 0)
            {
                r = r.multiply(BigInteger.valueOf(-1));
            }

            BigInteger c = a;
            a = b.mod(c);
            b = c;

            if (a.compareTo(BigInteger.ZERO) == 0)
                return r.intValue();
        }

        return r.intValue();
    }

    private BigInteger sqrt(BigInteger a, BigInteger p)
    {
        a = a.add(p);
        if (p.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)))
        {
            return PrimeNumber.powMod(a, (p.add(BigInteger.ONE)).divide(BigInteger.valueOf(4)), p);
        }
        if (p.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(5)))
        {
            if ((PrimeNumber.powMod(a, (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(4)), p)).equals(BigInteger.ONE))
            {
                return PrimeNumber.powMod(a, (p.add(BigInteger.valueOf(3))).divide(BigInteger.valueOf(8)), p);
            }
            if ((PrimeNumber.powMod(a, (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(4)), p)).equals(p.subtract(BigInteger.ONE)))
            {
                BigInteger aa = a.multiply(BigInteger.TWO);
                a = a.multiply(BigInteger.valueOf(4));
                a = PrimeNumber.powMod(a, (p.subtract(BigInteger.valueOf(5))).divide(BigInteger.valueOf(8)), p);
                return aa.multiply(a).mod(p);
            }
        }

        BigInteger q = p.subtract(BigInteger.ONE);
        BigInteger s = BigInteger.ZERO;
        while (q.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            q = q.divide(BigInteger.TWO);
            s = s.add(BigInteger.ONE);
        }

        BigInteger c = BigInteger.ONE;
        for (BigInteger z = BigInteger.TWO; !z.equals(p.subtract(BigInteger.ONE)); z = z.add(BigInteger.ONE))
            if (legendreSymbol(z, p) == -1)
            {
                c = PrimeNumber.powMod(z, q, p);
                break;
            }

        BigInteger r = PrimeNumber.powMod(a, (q.add(BigInteger.ONE)).divide(BigInteger.TWO), p),
                t = PrimeNumber.powMod(a, q, p), M = s;

        while(!t.mod(p).equals(BigInteger.ONE))
        {
            BigInteger S_ = BigInteger.ONE;
            while(!PrimeNumber.powMod(t, pow(BigInteger.TWO, S_), p).equals(BigInteger.ONE))
                S_ = S_.add(BigInteger.ONE);
            BigInteger w = PrimeNumber.powMod(c, pow(BigInteger.TWO, s.subtract(S_).subtract(BigInteger.ONE)), p);
            r = r.multiply(w).mod(p);
            t = t.multiply(pow(w, BigInteger.TWO)).mod(p);
            s = S_;
        }

        return r;
    }

    void result(BigInteger x1, BigInteger x2, BigInteger p, int step, boolean used)
    {
        int index = 0;
        BigInteger u = step == 1 ? x1 : x2;

        ArrayList<BigInteger> valuesForU = new ArrayList<>();
        valuesForU.add(u);
        ArrayList<BigInteger> valuesForM = new ArrayList<>();
        valuesForM.add(p);

        do
        {
            BigInteger m1 = valuesForU.get(index).pow(2).add(BigInteger.ONE);
            boolean ok1 = m1.mod(valuesForM.get(index)).equals(BigInteger.ZERO);
            m1 = m1.divide(valuesForM.get(index));
            boolean ok2 = m1.equals(BigInteger.ZERO);

            if (!ok1 || ok2)
            {
                if (!used)
                {
                    result(x1, x2, p, 0, true);
                }
                else
                {
                    this.a = null;
                    this.b = null;
                    return;
                }
            }

            BigInteger min1 = valuesForU.get(index).mod(m1);
            BigInteger min2 = m1.subtract(valuesForU.get(index)).mod(m1);
            valuesForU.add(min1.min(min2));
            valuesForM.add(m1);
            index++;
        }
        while (!valuesForM.get(index).equals(BigInteger.ONE));

        BigInteger a = valuesForU.get(index - 1);
        BigInteger b = BigInteger.ONE;
        index--;

        while (index != 0)
        {
            BigInteger top = valuesForU.get(index - 1).multiply(a);
            BigInteger topNegative = top.negate();
            top = top.add(BigInteger.ONE.multiply(b));
            topNegative = topNegative.add(BigInteger.ONE.multiply(b));
            BigInteger top2 = a.negate();
            BigInteger top2Negate = top2;
            top2 = top2.add(valuesForU.get(index - 1).multiply(b));
            top2Negate = top2Negate.subtract(valuesForU.get(index - 1).multiply(b));
            BigInteger bottom = a.pow(2).add(BigInteger.ONE.multiply(b.pow(2)));

            if (top.mod(bottom).equals(BigInteger.ZERO))
                a = top.divide(bottom);
            else
                a = topNegative.divide(bottom);
            if (top2.mod(bottom).equals(BigInteger.ZERO))
                b = top2.divide(bottom);
            else
                b = top2Negate.divide(bottom);

            index--;
        }
        this.a = a;
        this.b = b;
    }
}
