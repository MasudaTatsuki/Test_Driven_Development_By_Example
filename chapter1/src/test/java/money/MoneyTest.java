package money;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.money.Bank;
import org.money.Expression;
import org.money.Money;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class MoneyTest {
    // パッケージプライベートなMethodオブジェクトを格納するリスト
    private static Map<String, Method> packagePrivateMethods;

    // 初回だけ実行されるメソッド
    @BeforeAll
    public static void setUp() throws Exception {

        Class<Money> clazz = Money.class;
        packagePrivateMethods = new HashMap<>();

        Method dollarMethod = clazz.getDeclaredMethod("dollar", int.class);
        dollarMethod.setAccessible(true);
        packagePrivateMethods.put("dollar", dollarMethod);

        Method timesMethod = clazz.getDeclaredMethod("times", int.class);
        timesMethod.setAccessible(true);
        packagePrivateMethods.put("times", timesMethod);

        Method francMethod = clazz.getDeclaredMethod("franc", int.class);
        francMethod.setAccessible(true);
        packagePrivateMethods.put("franc", francMethod);

        Method currencyMethod = clazz.getDeclaredMethod("currency");
        currencyMethod.setAccessible(true);
        packagePrivateMethods.put("currency", currencyMethod);

        Method plusMethod = clazz.getDeclaredMethod("plus", Money.class);
        plusMethod.setAccessible(true);
        packagePrivateMethods.put("plus", plusMethod);

        Method reduceMethod = Bank.class.getDeclaredMethod(
                "reduce",
//                Money.class.getInterfaces()[0].getName().getClass(), // Expressionインターフェイス
                Expression.class,
                String.class
        );
        reduceMethod.setAccessible(true);
        packagePrivateMethods.put("reduce", reduceMethod);
    }
    @Test
    public void testMultiplication() throws InvocationTargetException, IllegalAccessException {

        Money fiveDollar = (Money) packagePrivateMethods.get("dollar").invoke(Money.class,5);
        Money tenDollar = (Money) packagePrivateMethods.get("times").invoke(fiveDollar, 2);
        Money fifteenDollar = (Money) packagePrivateMethods.get("times").invoke(fiveDollar, 3);

        assertEquals(packagePrivateMethods.get("dollar").invoke(Money.class, 10), tenDollar);
        assertEquals(packagePrivateMethods.get("dollar").invoke(Money.class, 15), fifteenDollar);
    }

    @Test
    public void testEquality() throws InvocationTargetException, IllegalAccessException {
        assertEquals(packagePrivateMethods.get("dollar").invoke(
                Money.class, 5
        ), packagePrivateMethods.get("dollar").invoke(
                Money.class, 5
        ));
        assertNotEquals(packagePrivateMethods.get("dollar").invoke(
                Money.class, 5
        ), packagePrivateMethods.get("dollar").invoke(
                Money.class, 6
        ));
        assertNotEquals(packagePrivateMethods.get("franc").invoke(
                Money.class, 5
        ), packagePrivateMethods.get("dollar").invoke(
                Money.class, 5
        ));
    }

    @Test
    public void testCurrency() throws InvocationTargetException, IllegalAccessException {

        Money oneDollar = (Money) packagePrivateMethods.get("dollar").invoke(Money.class, 1);
        Money oneFranc = (Money) packagePrivateMethods.get("franc").invoke(Money.class, 1);

        assertEquals(
                "USD",
                packagePrivateMethods.get("currency").invoke(oneDollar)
        );
        assertEquals(
                "CHF",
                packagePrivateMethods.get("currency").invoke(oneFranc)
        );
    }

    @Test
    public void testSimpleAddition() throws InvocationTargetException, IllegalAccessException {
        Money fiveDollar = (Money) packagePrivateMethods.get("dollar").invoke(Money.class, 1);
        Money sum = (Money) packagePrivateMethods.get("plus").invoke(fiveDollar, fiveDollar);
        Bank bank = new Bank();
        Money reduced = (Money) packagePrivateMethods.get("reduce").invoke(bank, sum, "USD");
        assertEquals(
                packagePrivateMethods.get("dollar").invoke(Money.class, 10),
                reduced
        );
    }
}
