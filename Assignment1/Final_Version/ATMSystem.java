
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ATMSystem {

    public static void main(String[] args) {
        // The function of class Account is to deposit account objects
        ArrayList<Account> accounts = new ArrayList<>();

        Scanner sc = new Scanner(System.in);

        //System welcome interface
        while (true) {
            mainMenu();
            int command = sc.nextInt();
            switch (command) {
                case 1:
                    //login interface
                    Login.login(accounts, sc);
                    break;
                case 2:
                    //register interface
                    Register.register(accounts, sc);
                    break;

                default:
                    //default
                    invalidCommand();
            }
        }

    }
    private static void mainMenu() {
        System.out.println("==================ATM SYSTEM==================");
        System.out.println("1: Account login");
        System.out.println("2: Open an account");
        System.out.println("Please select your option： ");
    }
    private static void invalidCommand() {
        System.out.println("The operation command you entered does not exist. Please enter your command correctly.");
    }


    /**
     *  Randomly generate eight-digit account number
     * Generate an eight-digit number for the account that is different from other accounts
     */

    static String getRandomCardId(ArrayList<Account> accounts) {
        Random r = new Random();
        while (true) {
            //Generate an eight-digit number
            String cardId = "";
            for(int i = 0; i < 8; i++){
                cardId += r.nextInt(10);
            }

            //Determine whether this card number is the same as that of other accounts.

            Account acc = getAccountByCardId(cardId, accounts);
            if(acc == null){
                //Indicates that this card number does not exist in the collection. This is a new card number.
                return cardId;
            }
        }

    }

    /**
     * Check the account based on the card number
     * @param cardId card number
     * @param accounts  Account collection
     * @return Account object / null
     */

    static Account getAccountByCardId(String cardId, ArrayList<Account> accounts){
        for (Account acc : accounts) {
            if (acc.getCardID().equals(cardId)) {
                return acc;
            }
        }
        return null; //No such account found
    }



    /**
     * Show account
     * @param acc current account object
     */

    static void showAccount(Account acc) {
        System.out.println("===================The current account information is as follows===================");
        System.out.println("card number：" + acc.getCardID());
        System.out.println("card username：" + acc.getUserName());
        System.out.println("balance：" + acc.getMoneyAmount());
        System.out.println("limit：" + acc.getQuotaMoney());
    }



}


//Change main to public.
//Encapsulate invalidCommandHandler and mainMenu,



