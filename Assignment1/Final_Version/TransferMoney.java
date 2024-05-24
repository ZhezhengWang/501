import java.util.ArrayList;
import java.util.Scanner;

/**
 *  transferMoney
 */
public class TransferMoney {
    static void transferMoney(Scanner sc, Account acc, ArrayList<Account> accounts) {
        //Determine whether the account number in the account set is less than two.
        // If there are less than two, the transfer function will not be established.
        if(accounts.size() < 2){
            System.out.println("There are less than two accounts in the bank, " +
                    "transfer operations cannot be performed!");
            return;
        }
        //Determine whether there is a deposit in the current account.
        // If there is no deposit, the transfer cannot be made.
        if(acc.getMoneyAmount() == 0){
            System.out.println("Sorry, there is no balance in this account.。");
            return;
        }
        //Enter the other party’s account
        while (true) {
            //开始转账
            System.out.println("Please enter the other party's account card number： ");
            String cardId = sc.next();
            //Determine whether the card number to be transferred is your own card number
            if(cardId.equals(acc.getCardID())){
                System.out.println("Sorry, you can't transfer money to yourself!");
                continue;
            }
            Account account = ATMSystem.getAccountByCardId(cardId, accounts);
            if(account == null){
                System.out.println("Sorry, the card number you entered does not exist!");
            }else {
                //The account exists and is not your own account
                String userName = account.getUserName();
                //Verify the last name of the other party's card number and give a prompt
                String tips = "*" + userName.substring(1);
                System.out.println("Please enter[" + tips + "]surname: ");
                String lastName = sc.next();
                //Last name entered correctly
                if(userName.startsWith(lastName)){
                    System.out.println("Certification passed！");
                    //Verification completed, enter the transfer amount
                    while (true) {
                        System.out.println("Please enter the transfer amount：");
                        double money = sc.nextDouble();
                        //If the transfer amount is greater than the deposit,
                        // it will prompt that the balance is insufficient and the maximum balance that can be transferred will be prompted.
                        if(money > acc.getMoneyAmount()){
                            System.out.println("The account balance is insufficient," +
                                    " you can transfer up to：" + acc.getMoneyAmount());
                        }else {
                            //Subtract the transfer balance from the current user balance
                            acc.setMoneyAmount(acc.getMoneyAmount() - money);
                            //Add the deposit to the other party's account plus the transfer amount
                            account.setMoneyAmount(account.getMoneyAmount() + money);
                            System.out.println("Transfer successful! Your account has remaining：" + acc.getMoneyAmount());
                            return;
                        }
                    }
                }else {
                    System.out.println("Sorry, the information you entered is incorrect!");
                }

            }
        }

    }
}
