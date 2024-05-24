import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

public class ATMSystemTest {

    private ATMSystem atmSystem;
    private ArrayList<Account> accounts;
    private ByteArrayInputStream testInput;
    private Scanner scanner;



    @Before
    public void setUp() {
        atmSystem = new ATMSystem();
        accounts = new ArrayList<>();
        testInput = new ByteArrayInputStream("".getBytes());
        System.setIn(testInput);
        scanner = new Scanner(testInput);
        accounts = new ArrayList<>();
        // 添加一些测试用的账户
    }

    @After
    public void tearDown() {
        System.setIn(System.in);
        scanner.close();
    }

    @Test
    public void testRegister() {
        String input = "王哲正\n123\n123\n100.0\n";
        provideTestInput(input);

        Register.register(accounts, scanner);

        assertEquals(1, accounts.size());
        Account account = accounts.get(0);
        assertEquals("王哲正", account.getUserName());
        assertEquals("123", account.getPassword());
        assertEquals(100.0, account.getQuotaMoney(), 0.001);
        assertNotNull(account.getCardID());
    }

    @Test
    public void testGetRandomCardId() {
        provideTestInput("王哲正\n123\n123\n100.0\n");
        Register.register(accounts, scanner);

        String randomCardId = ATMSystem.getRandomCardId(accounts);

        assertNotNull(randomCardId);
        assertEquals(8, randomCardId.length());
    }

    @Test
    public void testGetAccountByCardId() {
        Account account1 = new Account();
        account1.setCardID("11111111");
        Account account2 = new Account();
        account2.setCardID("22222222");
        accounts.add(account1);
        accounts.add(account2);

        Account result1 = ATMSystem.getAccountByCardId("11111111", accounts);
        Account result2 = ATMSystem.getAccountByCardId("22222222", accounts);
        Account result3 = ATMSystem.getAccountByCardId("99999999", accounts);

        assertEquals(account1, result1);
        assertEquals(account2, result2);
        assertNull(result3);
    }

    @Test
    public void testDeleteAccount() {
        provideTestInput("王哲正\n123\n123\n100.0\n");
        Register.register(accounts, scanner);
        Account account = accounts.get(0);

        provideTestInput("n\n");
        boolean result2 = DeleteAccount.deleteAccount(account, scanner, accounts);
        assertFalse(result2);
        assertEquals(1, accounts.size(), 0.001);


        provideTestInput("y\n");
        boolean result1 = DeleteAccount.deleteAccount(account, scanner, accounts);
        assertTrue(result1);
        assertEquals(0, accounts.size());



    }

    @Test
    public void testUpdatePassword() {
        provideTestInput("王哲正\n123\n123\n100.0\n");
        Register.register(accounts, scanner);
        Account account = accounts.get(0);

        provideTestInput("123\n1234\n1234\n");
        ChangePass.updatePassword(scanner, account);
        assertEquals("1234", account.getPassword());

        provideTestInput("12345\n1234\n1234\n");
        ChangePass.updatePassword(scanner, account);
        assertEquals("1234", account.getPassword());
    }

    @Test
    public void testTransferMoney() {

        provideTestInput("王哲正\n123\n123\n100.0\n");
        Register.register(accounts, scanner);
        Account account0 = accounts.get(0);

        provideTestInput("500\n");
        Deposit.depositMoney(account0, scanner);

        provideTestInput("李强\n456\n456\n100.0\n");
        Register.register(accounts, scanner);
        Account account1 = accounts.get(1);

        provideTestInput("500\n");
        Deposit.depositMoney(account1, scanner);

        String 王哲正cardID = account0.getCardID();
        String 李强cardID = account1.getCardID();

        provideTestInput(李强cardID + "\n李\n100\n");

        TransferMoney.transferMoney(scanner, account0, accounts);
        assertEquals(400.0, account0.getMoneyAmount(),0.001);
        assertEquals(600.0, account1.getMoneyAmount(),0.001);

        provideTestInput(王哲正cardID + "\n王\n200\n");

        TransferMoney.transferMoney(scanner, account1, accounts);
        assertEquals(600.0, account0.getMoneyAmount(),0.001);
        assertEquals(400.0, account1.getMoneyAmount(),0.001);
    }

    @Test
    public void testDrawMoney() {
        provideTestInput("\n123\n123\n123\n100\n");
        Register.register(accounts, scanner);
        Account account = accounts.get(0);

        provideTestInput("500\n");
        Deposit.depositMoney(account, scanner);
        System.out.println(account.getMoneyAmount());


        provideTestInput("100.0\n");
        Draw.drawMoney(account, scanner);
        assertEquals(400, account.getMoneyAmount(),0.001);

        provideTestInput("50.0\n");
        Draw.drawMoney(account, scanner);
        assertEquals(350, account.getMoneyAmount(),0.001);

        provideTestInput("20.0\n");
        Draw.drawMoney(account, scanner);
        assertEquals(330, account.getMoneyAmount(),0.001);
    }

    @Test
    public void testShowAccount() {
        Account account = new Account();
        account.setCardID("11111111");
        account.setUserName("王哲正");
        account.setMoneyAmount(100.0);
        account.setQuotaMoney(200.0);

        provideTestInput("");
        ATMSystem.showAccount(account);
    }

    @Test
    public void testLogin() {


        String input = "王哲正\n123\n123\n100.0\n";
        provideTestInput(input);

        Register.register(accounts, scanner);

        assertEquals(1, accounts.size());
        Account account0 = accounts.get(0);
        String 王哲正cardID = account0.getCardID();

        assertEquals(王哲正cardID, account0.getCardID());
        assertEquals("王哲正", account0.getUserName());
        assertEquals("123", account0.getPassword());
        assertEquals(100.0, account0.getQuotaMoney(), 0.001);
        assertNotNull(account0.getCardID());

    }

    private void provideTestInput(String input) {
        testInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(testInput);
        scanner = new Scanner(testInput);
    }


}
