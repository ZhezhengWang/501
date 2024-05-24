import java.util.Scanner;

/**
 *  Change password function
 */
public class ChangePass {
    static void updatePassword(Scanner sc, Account acc) {
        System.out.println("====================User password modification====================");
        System.out.println("Please enter your current password： ");
        String passWord = sc.next();
        //Confirm that the entered password matches the account password
        if(acc.getPassword().equals(passWord)){
            while (true) {
                System.out.println("Please enter a new password：");
                //Enter a new password
                String newPassword = sc.next();
                System.out.println("Please confirm your new password：");
                //Repeat the new password
                String ndNewPassword = sc.next();
                //Determine whether the newly entered password and the confirmed password are the same
                if(newPassword.equals(ndNewPassword)){
                    acc.setPassword(newPassword);
                    System.out.println("Password reset complete！");
                    return;
                    //The two passwords are not the same, please re-enter the password.
                }else {
                    System.out.println("The password you entered twice does not match, please re-enter it：");
                }
            }
            //password has been updated
        }else {
            System.out.println("the password entered is incorrect。");
        }
    }
}
