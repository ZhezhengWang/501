import java.util.ArrayList;
import java.util.Scanner;

public class DeleteAccount {
    static boolean deleteAccount(Account acc, Scanner scanner, ArrayList<Account> accounts) {
        System.out.println("==================User deletes account==================");

        if (confirmAccountDeletion(scanner)) {
            if (hasBalance(acc)) {
                cannotDelete();
            } else {
                removeAccount(accounts, acc);
                printAccountDeleted();
                return true;
            }
        } else {
            printAccountReserved();
        }

        return false;
    }

    private static boolean confirmAccountDeletion(Scanner scanner) {
        System.out.println("Are you sure you want to delete your account? (y/n)");
        String response = scanner.next().toLowerCase();
        return response.equals("y");
    }

    private static boolean hasBalance(Account acc) {
        return acc.getMoneyAmount() > 0;
    }

    private static void cannotDelete() {
        System.out.println("You still have a balance in your account and cannot delete the account.");
    }

    private static void removeAccount(ArrayList<Account> accounts, Account acc) {
        accounts.remove(acc);
    }

    private static void printAccountDeleted() {
        System.out.println("Your account has been deleted!");
    }

    private static void printAccountReserved() {
        System.out.println("Account has been reserved!");
    }
}


// Decompose the logic of deleteAccount into 6 private methods confirmAccountDeletion,
// hasBalance, cannotDelete, removeAccount, printAccountDeleted, printAccountReserved
// Improve code readability.
// Use more descriptive method and variable names to make your code easier to understand.
// Reduced nested conditional statements to make code clearer.