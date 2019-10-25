import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.Scanner;

public class ProofZeroCurve {
    public static int command = -1;
    public static BigInteger p1, A1, r1, p2, A2, r2, l;
    public static Pair Q1 = new Pair(), P1 = new Pair(), Q2 = new Pair(), P2 = new Pair();

    static void print() {
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

    public static void main(String[] args) throws FileNotFoundException {
        do {
            print();
            init();
            if (check())
            {
                System.out.println("Ошибка: неверные параметры или не найден файл");
                return;
            }
            switch (command) {
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

        } while (command != 0);
    }

    public static Pair sum(Pair x1y1, Pair x2y2, BigInteger p, BigInteger A) {
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
        } catch (ArithmeticException e) {
            return null;
        }
    }

    public static void init() throws FileNotFoundException
    {
        initParam("verifier.txt");
        p1 = p2;
        A1 = A2;
        Q1 = Q2;
        P1 = P2;
        r1 = r2;
        initParam("applicant.txt");
    }

    public static boolean check()
    {
        if (A1 == null || A2 == null || p1 == null || p2 == null || r1 == null || r2 == null || Q1.x == null || Q1.y == null
        || Q2.x == null || Q2.y == null || P1.x == null || P1.y == null || P2.x == null || P2.y == null || l == null)
            return true;
        return false;
    }

    public static void initParam(String path) throws FileNotFoundException
    {
        try
        {
            FileReader reader = new FileReader(path);
            Scanner scan = new Scanner(reader);
            p2 = new BigInteger(scan.nextLine());
            A2 = new BigInteger(scan.nextLine());
            String[] help = scan.nextLine().split(" ");
            Q2 = new Pair(new BigInteger(help[0]), new BigInteger(help[1]));
            r2 = new BigInteger(scan.nextLine());
            help = scan.nextLine().split(" ");
            P2 = new Pair(new BigInteger(help[0]), new BigInteger(help[1]));
            l = new BigInteger(scan.nextLine());
        }
        catch (Exception e)
        {
        }
    }
}