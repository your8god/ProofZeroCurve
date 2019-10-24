import java.math.BigInteger;
import java.util.Scanner;

public class ProofZeroCurve
{
    public static int command = -1;

    static void print()
    {
        System.out.println("Введите команду:");
        System.out.println("1: Претендент: Сгенерировать точку R и отправить верификатору");
        System.out.println("2: Верификатор: Проверить точку R и послать претенденту случайный бит");
        System.out.println("3: Претендент: Предьявление показателя k или k' на основе полученного бита");
        System.out.println("4: Верификатор: Проверка знания l претендента");
        System.out.print("0: Выход\nКоманда: ");
        Scanner scan = new Scanner(System.in);
        command = scan.nextInt();
        if (command > 5 || command < 0)
            System.out.println("Неверный ввод");
    }

    public static void main(String[] args)
    {
        do {
            print();
            switch (command)
            {
                case 0:
                    break;
                case 1:
                    //GengBlum BlumNumber = new GengBlum();
                    break;
                case 2:
                    //GengBit bit = new GengBit();
                    break;
                case 3:
                    //TransX x = new TransX();
                    break;
                case 4:
                    //Check check = new Check();
                    break;
            }

        } while(command != 0);
    }
    }

    static Pair sum(Pair x1y1, Pair x2y2, BigInteger p)
    {
        try {
            BigInteger x1 = x1y1.x, y1 = x1y1.y, x2 = x2y2.x, y2 = x2y2.y, alph;
            if (x1y1 == null)
                return null;

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
