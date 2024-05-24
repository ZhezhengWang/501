import java.util.ArrayList;
import java.util.Scanner;

public class ATMOperationHandler {
    protected static void showUserCommand(Account acc, Scanner sc, ArrayList<Account> accounts) {
        while (true) {
            System.out.println("=======================User operation page=======================");
            System.out.println("1: Query account information：");
            System.out.println("2: deposit：");
            System.out.println("3: Withdraw money：");
            System.out.println("4: transfer money：");
            System.out.println("5: change Password：");
            System.out.println("6: Logout：");
            System.out.println("7: Delete account：");
            System.out.println("Please select： ");
            int command = sc.nextInt();
            switch (command) {
                case 1:
                    ATMSystem.showAccount(acc);
                    break;
                case 2:
                    Deposit.depositMoney(acc, sc);
                    break;
                case 3:
                    Draw.drawMoney(acc, sc);
                    break;
                case 4:
                    TransferMoney.transferMoney(sc, acc, accounts);
                    break;
                case 5:
                    ChangePass.updatePassword(sc, acc);
                    return;
                case 6:
                    System.out.println("Logout successful, welcome to use next time!");
                    return;
                case 7:
                    if (DeleteAccount.deleteAccount(acc, sc, accounts)) {
                        return;
                    }
                    return;
                default:
                    System.out.println("The operation command you entered is not applicable!");
            }
        }
    }




}
