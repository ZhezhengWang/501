

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ATMSystem {

    public static void main(String[] args) {

        ArrayList<Account> accounts = new ArrayList<>();

        Scanner sc = new Scanner(System.in);


        while (true) {
            System.out.println("==================ATM SYSTEM==================");
            System.out.println("1: Account login");
            System.out.println("2: Open an account");
            System.out.println("Please select your option： ");
            int command = sc.nextInt();
            switch (command){
                case 1:
                    login(accounts, sc);
                    break;

                case 2:
                    register(accounts, sc);

                    break;

                default:
                    System.out.println("The operation command you entered does not exist. " +
                            "Please enter your command correctly.！");
            }
        }
    }

    /**
     * @param accounts
     * @param sc
     */
    private static void register(ArrayList<Account> accounts, Scanner sc) {
        System.out.println("====================Account Opening Operation====================");
        Account account = new Account();
        System.out.println("Please enter your account username： ");
        String userName = sc.next();
        account.setUserName(userName);

        String passWord = null;
        while (true) {
            System.out.println("Please enter your account password：  ");
            passWord = sc.next();
            System.out.println("Please enter confirmation password：  ");
            String confirmPass = sc.next();
            if(passWord.equals(confirmPass)){
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

        String cardId = getRandomCardId(accounts);
        account.setCardID(cardId);

        accounts.add(account);
        System.out.println("Congratulations" + userName + "Sir/Madam, your card number is： " + cardId);


    }

    private static String getRandomCardId(ArrayList<Account> accounts) {
        Random r = new Random();
        while (true) {
            String cardId = "";
            for(int i = 0; i < 8; i++){
                cardId += r.nextInt(10);
            }
            Account acc = getAccountByCardId(cardId, accounts);
            if(acc == null){
                return cardId;
            }
        }

    }

    private static Account getAccountByCardId(String cardId, ArrayList<Account> accounts){
        for(int i = 0; i < accounts.size(); i++){
            Account acc = accounts.get(i);
            if(acc.getCardID().equals(cardId)){
                return acc;
            }
        }
        return null;
    }

    private static void login(ArrayList<Account> accounts, Scanner sc){
        System.out.println("==================Login Interface==================");
        if(accounts.isEmpty()){
            System.out.println("Sorry, there is no account in the current system, " +
                    "please open an account！");
            return;
        }

        while (true) {
            System.out.println("Please enter the card number：");
            String cardID = sc.next();
            Account acc = getAccountByCardId(cardID, accounts);
            if(acc != null){
                while (true) {
                    System.out.println("Please enter your password： ");
                    String passWord = sc.next();
                    if(acc.getPassword().equals(passWord)){
                        System.out.println("login successful！" + acc.getUserName() + "Hello Sir/Madam! , " +
                                "your card number is：" + acc.getCardID());
                        showUserCommand(sc, acc, accounts);
                        return;
                    }else{
                        System.out.println("The password you entered is incorrect!");
                    }
                }
            }else{
                System.out.println("Sorry, the account does not exist!");
            }
        }
    }

    /**
     *
     * @param sc
     * @param acc
     * @param accounts
     */
    private static void showUserCommand(Scanner sc, Account acc, ArrayList<Account> accounts) {
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
                    showAccount(acc);
                    break;
                case 2:
                    depositMoney(acc, sc);
                    break;
                case 3:
                    drawMoney(acc, sc);
                    break;
                case 4:
                    transferMoney(sc, acc, accounts);
                    break;
                case 5:
                    updatePassword(sc, acc);
                    return;
                case 6:
                    System.out.println("Logout successful, welcome to use next time!");
                    return;
                case 7:
                    if (deleteAccount(acc, sc, accounts)) {
                        return;
                    }
                default:
                    System.out.println("The operation command you entered is not applicable!");
            }
        }
    }

    /**
     *
     * @param acc
     * @param sc
     * @param accounts
     */
    private static boolean deleteAccount(Account acc, Scanner sc, ArrayList<Account> accounts) {
        System.out.println("==================User deletes account==================");
        System.out.println("Are you sure you need to delete your account? y/n");
        String rs = sc.next();
        if (rs.equals("y")) {
            if (acc.getMoneyAmount() > 0) {
                System.out.println("You still have a balance in your account and cannot delete account.");
            } else {
                accounts.remove(acc);
                System.out.println("Your account has been deleted!");
                return true;
            }
        } else {
            System.out.println("Account has been reserved!");
        }
        return false;
    }

    /**
     *
     * @param sc
     * @param acc
     */
    private static void updatePassword(Scanner sc, Account acc) {
        System.out.println("====================User password modification====================");
        System.out.println("Please enter your current password： ");
        String passWord = sc.next();
        if(acc.getPassword().equals(passWord)){
            while (true) {
                System.out.println("Please enter a new password：");
                String newPassword = sc.next();

                System.out.println("Please confirm your new password：");
                String ndNewPassword = sc.next();

                if(newPassword.equals(ndNewPassword)){
                    acc.setPassword(newPassword);
                    System.out.println("Password reset complete！");
                    return;
                }else {
                    System.out.println("The password you entered twice does not match, please re-enter it：");
                }
            }
        }else {
            System.out.println("the password entered is incorrect。");
        }
    }

    /**
     *
     * @param sc
     * @param acc
     * @param accounts
     */
    private static void transferMoney(Scanner sc, Account acc, ArrayList<Account> accounts) {
        if(accounts.size() < 2){
            System.out.println("There are less than two accounts in the bank, " +
                    "transfer operations cannot be performed!");
            return;
        }
        if(acc.getMoneyAmount() == 0){
            System.out.println("Sorry, there is no balance in this account.。");
            return;
        }
        while (true) {
            System.out.println("Please enter the other party's account card number： ");
            String cardId = sc.next();

            if(cardId.equals(acc.getCardID())){
                System.out.println("Sorry, you can't transfer money to yourself!");
                continue;
            }

            Account account = getAccountByCardId(cardId, accounts);
            if(account == null){
                System.out.println("Sorry, the card number you entered does not exist!");
            }else {
                String userName = account.getUserName();
                String tips = "*" + userName.substring(1);
                System.out.println("Please enter[" + tips + "]surname: ");
                String lastName = sc.next();
                if(userName.startsWith(lastName)){
                    System.out.println("Certification passed！");
                    while (true) {
                        System.out.println("Please enter the transfer amount：");
                        double money = sc.nextDouble();
                        if(money > acc.getMoneyAmount()){
                            System.out.println("The account balance is insufficient," +
                                    " you can transfer up to：" + acc.getMoneyAmount());
                        }else {
                            acc.setMoneyAmount(acc.getMoneyAmount() - money);
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


    /**
     *
     * @param acc
     * @param sc
     */
    private static void drawMoney(Account acc, Scanner sc) {
        System.out.println("=================User withdrawal function=================");
        if(acc.getMoneyAmount() < 100){
            System.out.println("Sorry, the deposit in the account is less than 100 dollar" +
                    ", so you cannot withdraw money.");
            return;
        }
        while (true) {
            System.out.println("Please enter the withdrawal amount: ");
            double money = sc.nextDouble();

            if(money > acc.getQuotaMoney()){
                System.out.println("Sorry, the limit for each withdrawal is： " + acc.getQuotaMoney());
            }else {

                if(money > acc.getMoneyAmount()){
                    System.out.println("Insufficient balance. Your current account balance is： " + acc.getMoneyAmount());
                }else {
                    System.out.println("Deposit successful! , you successfully withdraw money： " + money + "dollar.");
                    acc.setMoneyAmount(acc.getMoneyAmount() - money);
                    showAccount(acc);
                    return;
                }
            }
        }

    }

    private static void depositMoney(Account acc, Scanner sc) {
        System.out.println("==================User deposit interface================");
        System.out.println("Please enter the deposit amount： ");
        double money = sc.nextDouble();

        acc.setMoneyAmount(acc.getMoneyAmount() + money);
        System.out.println("Saving money successfully! The current account information is as follows：");
        showAccount(acc);

    }

    private static void showAccount(Account acc) {
        System.out.println("===================The current account information is as follows===================");
        System.out.println("card number：" + acc.getCardID());
        System.out.println("card username：" + acc.getUserName());
        System.out.println("balance：" + acc.getMoneyAmount());
        System.out.println("limit：" + acc.getQuotaMoney());
    }


}

//I study this project very long time ago but i find these code on from www.itheime.com , they upload around 1 year ago.
//The cource code is on: 
//https://www.youtube.com/watch?v=gM-ntuNx7AY&list=PLFbd8KZNbe-_Qe0FQXinSoSBDDPUofQ34&index=134&ab_channel=%E9%BB%91%E9%A9%AC%E7%A8%8B%E5%BA%8F%E5%91%98
//https://www.youtube.com/watch?v=D3_wQRwvUks&list=PLFbd8KZNbe-_Qe0FQXinSoSBDDPUofQ34&index=135&ab_channel=%E9%BB%91%E9%A9%AC%E7%A8%8B%E5%BA%8F%E5%91%98
//https://www.youtube.com/watch?v=zWySmfx7GF8&list=PLFbd8KZNbe-_Qe0FQXinSoSBDDPUofQ34&index=136&ab_channel=%E9%BB%91%E9%A9%AC%E7%A8%8B%E5%BA%8F%E5%91%98
//https://www.youtube.com/watch?v=XbdCdxnXMa8&list=PLFbd8KZNbe-_Qe0FQXinSoSBDDPUofQ34&index=137&ab_channel=%E9%BB%91%E9%A9%AC%E7%A8%8B%E5%BA%8F%E5%91%98
//https://www.youtube.com/watch?v=AOrJCxEC6g4&list=PLFbd8KZNbe-_Qe0FQXinSoSBDDPUofQ34&index=138&ab_channel=%E9%BB%91%E9%A9%AC%E7%A8%8B%E5%BA%8F%E5%91%98