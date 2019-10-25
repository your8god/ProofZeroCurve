import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;

public class ProofZeroCurve
{//*****************************************************************************************************************************************************************
//вспомогательный блок и блок инициализации

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

    public static void main(String[] args) throws IOException
    {
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
                    step1();
                    break;
                case 2:
                    step2();
                    break;
                case 3:
                    step3();
                    break;
                case 4:
                    step4();
                    break;
            }

        } while (command != 0);

        Files.deleteIfExists(new File("round.txt").toPath());
    }

    public static Pair sum(Pair x1y1, Pair x2y2, BigInteger p, BigInteger A) {
        try {
            if (x1y1 == null) {
                return null;
            }

            BigInteger x1 = x1y1.x, y1 = x1y1.y, x2 = x2y2.x, y2 = x2y2.y, alph;

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

    public static Pair mult(Pair point, BigInteger n, BigInteger A, BigInteger p)
    {
        Pair res = point;
        for (int i = 0; i < n.intValue() - 1; i++)
            res = sum(res, point, p, A);
        return res;
    }

    public static boolean checkMult(Pair point, BigInteger n, BigInteger A, BigInteger p)
    {
        Pair res = point;
        res = mult(point, n.subtract(BigInteger.ONE), A, p);

        if (res == null)
            return true;
        res = sum(res, point, p, A);
        if (res == null)
            return false;
        return true;
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
        if (l.compareTo(r2) > 0)
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
            reader.close();
        }
        catch (Exception e)
        {
        }
    }

    public static void deleteAll() throws IOException
    {
        Files.deleteIfExists(new File("k_.txt").toPath());
        Files.deleteIfExists(new File("k.txt").toPath());
        Files.deleteIfExists(new File("R.txt").toPath());
        Files.deleteIfExists(new File("bit.txt").toPath());
        Files.deleteIfExists(new File("KK.txt").toPath());
        //Files.deleteIfExists(new File("round.txt").toPath());
    }

//*******************************************************************************************************************************************************************
//первый шаг: "Претендент: Сгенерировать точку R и отправить верификатору"

    public static void step1() throws IOException
    {
        BigInteger k = new BigInteger(r2.bitLength() - 1, new Random()).mod(r2);
        BigInteger k_ = k.multiply(l).mod(r2);
        Pair R = mult(P2, k, A2, p2);

        FileWriter out = new FileWriter("k.txt");
        out.write(k + "");
        out.close();

        out = new FileWriter("k_.txt");
        out.write(k_ + "");
        out.close();

        out = new FileWriter("R.txt");
        out.write(R.x + " " + R.y);
        out.close();
    }

//*******************************************************************************************************************************************************************
//второй шаг: "Верификатор: Проверить точку R и послать претенденту случайный бит"

    public static void step2() throws IOException
    {
        try
        {
            FileReader reader = new FileReader("R.txt");
            Scanner scan = new Scanner(reader);


            String[] help = scan.nextLine().split(" ");
            Pair R = new Pair(new BigInteger(help[0]), new BigInteger(help[1]));
            reader.close();


            if (R.x == null || R.y == null || R == null) {
                System.out.println("Некорректая точка R");
                return;
            }

            if (checkMult(R, r1, A1, p1)) {
                System.out.println("Некорректая точка R");
                reader.close();
                deleteAll();
                Files.deleteIfExists(new File("round.txt").toPath());
                return;
            }

            FileWriter out = new FileWriter("bit.txt");
            out.write((Math.random() > 0.5 ? 1 : 0) + "");
            out.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Ошибка: неверные параметры или не найден файл");
            return;
        }
    }

//*******************************************************************************************************************************************************************
//третий шаг: "Претендент: Предьявление показателя k или k' на основе полученного бита"

    public static void step3() throws IOException
    {
        try
        {
            FileReader reader = new FileReader("bit.txt");
            Scanner scan = new Scanner(reader);

            int bit = scan.nextInt();
            reader.close();

            FileWriter out = new FileWriter("KK.txt");
            if (bit == 0) {
                reader = new FileReader("k.txt");
                scan = new Scanner(reader);
                BigInteger help = scan.nextBigInteger();
                out.write(help + "");
                out.close();
                reader.close();
            } else {
                reader = new FileReader("k_.txt");
                scan = new Scanner(reader);
                BigInteger help = scan.nextBigInteger();
                out.write(help + "");
                out.close();
                reader.close();
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Ошибка: неверные параметры или не найден файл");
            return;
        }
    }

//*******************************************************************************************************************************************************************
//четвертый шаг: "Верификатор: Проверка знания l претендента"

    public static void step4() throws IOException
    {
        try
        {
            FileReader reader = new FileReader("bit.txt");
            Scanner scan = new Scanner(reader);
            int bit = scan.nextInt();
            reader.close();

            reader = new FileReader("R.txt");
            scan = new Scanner(reader);
            String[] help = scan.nextLine().split(" ");
            Pair R = new Pair(new BigInteger(help[0]), new BigInteger(help[1]));
            reader.close();

            reader = new FileReader("KK.txt");
            scan = new Scanner(reader);
            BigInteger k = scan.nextBigInteger();
            reader.close();

            int round = 0;

            try {
                reader = new FileReader("round.txt");
                scan = new Scanner(reader);
                round = scan.nextInt();
                reader.close();
            } catch (FileNotFoundException e) {
                FileWriter out = new FileWriter("round.txt");
                out.write(round + "");
                out.close();
            }

            if (bit == 0) {
                Pair check = mult(P1, k, A1, p1);
                if (check.x.equals(R.x) && check.y.equals(R.y)) {
                    round++;
                    System.out.println("Проверка пройдена. Пользователь знает l с вероятностью " + (1 - 1 / Math.pow(2, round)) + "!");
                    deleteAll();
                } else {
                    System.out.println("Проверка не пройдена. Пользователь не знает l!");
                    deleteAll();
                    Files.deleteIfExists(new File("round.txt").toPath());
                    return;
                }
            } else {
                Pair check = mult(Q1, k, A1, p1);
                if (check.x.equals(R.x) && check.y.equals(R.y)) {
                    round++;
                    System.out.println("Проверка пройдена. Пользователь знает l с вероятностью " + (1 - 1 / Math.pow(2, round)) + "!");
                    deleteAll();
                } else {
                    System.out.println("Проверка не пройдена. Пользователь не знает l!");
                    deleteAll();
                    Files.deleteIfExists(new File("round.txt").toPath());
                    return;
                }
            }

            FileWriter out = new FileWriter("round.txt");
            out.write(round + "");
            out.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Ошибка: неверные параметры или не найден файл");
            return;
        }
    }
}