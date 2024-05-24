import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Login {
    public static void login(ArrayList<Account> accounts, Scanner scanner) {
        System.out.println("==================Login Interface==================");
        if (accounts.isEmpty()) {
            System.out.println("Sorry, there are no accounts in the current system. Please open an account!");
            return;
        }

        while (true) {
            System.out.println("Please enter the card number：");
            String cardID = scanner.next();


            Account acc = ATMSystem.getAccountByCardId(cardID, accounts);

            if (acc != null) {
                //The card number not exist and the user is asked to enter the password
                if (validatePassword(scanner, acc.getPassword())) {
                    loginSuccess(acc);
                    ATMOperationHandler.showUserCommand(acc,scanner,accounts);
                    return; // Return from the login method
                } else {
                    System.out.println("The password you entered is incorrect!");
                }
            } else {
                System.out.println("Sorry, the account does not exist!");
            }
        }
    }

    private static boolean validatePassword(Scanner scanner, String expectedPassword) {
        System.out.println("Please enter your password：");
        String enteredPassword = scanner.next();
        return expectedPassword.equals(enteredPassword);
    }

    private static void loginSuccess(Account acc) {
        System.out.println("Login successful! Hello " + acc.getUserName() + ", your card number is: " + acc.getCardID());
    }
}


//Put the verification password in the private method validatePassword
//Put successful login into the private method successLogin
//Simplified the structure of the conditional statement to make the code more readable.