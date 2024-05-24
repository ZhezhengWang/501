import java.util.Scanner;

/**
 * Money saving function
 */

public class Deposit {
    static void depositMoney(Account acc, Scanner sc) {
        System.out.println("==================User deposit interface================");
        System.out.println("Please enter the deposit amount： ");
        double money = sc.nextDouble();

        //Update account balance, original deposit plus newly deposited money
        acc.setMoneyAmount(acc.getMoneyAmount() + money);
        System.out.println("Saving money successfully! The current account information is as follows：");
        ATMSystem.showAccount(acc);

    }
}
