import java.util.Scanner;

public class Draw {
    static void drawMoney(Account acc, Scanner scanner) {
        System.out.println("=================User withdrawal function=================");

        if (acc.getMoneyAmount() < 100) {
            //Determine whether 100 dollars is enough. If not, you cannot withdraw money.
            System.out.println("Sorry, the deposit in the account is less than $100, so you cannot withdraw money.");
            return;
        }

        while (true) {
            //Prompt user to withdraw amount
            System.out.println("Please enter the withdrawal amount: ");
            double amountToWithdraw = scanner.nextDouble();
            //Determine whether the amount is greater than the limit
            if (amountToWithdraw > acc.getQuotaMoney()) {
                System.out.println("Sorry, the withdrawal limit is: " + acc.getQuotaMoney());
            } else if (amountToWithdraw > acc.getMoneyAmount()) {
                System.out.println("Insufficient balance. Your current account balance is: " + acc.getMoneyAmount());
            } else {
                successWithdrawal(acc, amountToWithdraw);
                return;
            }
        }
    }

    private static void successWithdrawal(Account acc, double amount) {
        System.out.println("Withdrawal successful! You have withdrawn: $" + amount);
        acc.setMoneyAmount(acc.getMoneyAmount() - amount);
        ATMSystem.showAccount(acc);
    }
}

//Reorganize blocks of code into a more readable structure and use more descriptive variable names.
//Encapsulate the logic of executing withdrawals by extracting the performWithdrawal method to reduce code duplication.
//Combine else and if
//Use more descriptive prompt messages to improve user experience.