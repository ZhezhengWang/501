import java.util.ArrayList;
import java.util.Scanner;


/**
 * Implementation of user account opening function
 */
public class Register {
    static void register(ArrayList<Account> accounts, Scanner sc) {
        System.out.println("====================Account Opening Operation====================");
        Account account = new Account();
        System.out.println("Please enter your account username： ");
        String userName = sc.next();
        // Determine whether there is an account object in the username collection
        account.setUserName(userName);

        String passWord = null;
        while (true) {
            System.out.println("Please enter your account password：  ");
            passWord = sc.next();
            System.out.println("Please enter confirmation password：  ");
            String confirmPass = sc.next();
            if(passWord.equals(confirmPass)){
                //Confirm that the password is passed and inject the password into the account object.
                System.out.println("Password registration successful！");
                account.setPassword(passWord);
                break;
            }else {
                System.out.println("Sorry, your two passwords are different, please re-enter！");
            }
        }

        System.out.println("Please enter the account daily limit：");
        double quotaMoney = sc.nextDouble();
        account.setQuotaMoney(quotaMoney);

        //Give the account a random eight-digit number that is not the same as the existing account.
        String cardId = ATMSystem.getRandomCardId(accounts);
        account.setCardID(cardId);

        //Add the account object to the account collection
        accounts.add(account);
        System.out.println("Congratulations " + userName + " Sir/Madam, your card number is： " + cardId);


    }
}

//Write a private method createAccount to generate the account
//Write a private method getPassword to generate a password
//Put the successful registration message in printRegistrationSuccess to reduce code duplication.