//This is a collection of all account information, including getters and setters.


public class Account {

    private String cardID;
    private String userName;
    private String password;
    private double moneyAmount;
    private double quotaMoney;

    protected String getCardID() {
        return cardID;
    }

    protected void setCardID(String cardID) {
        this.cardID = cardID;
    }

    protected String getUserName() {
        return userName;
    }

    protected void setUserName(String userName) {
        this.userName = userName;
    }

    protected String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected double getMoneyAmount() {
        return moneyAmount;
    }

    protected void setMoneyAmount(double moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    protected double getQuotaMoney() {
        return quotaMoney;
    }

    protected void setQuotaMoney(double quotaMoney) {
        this.quotaMoney = quotaMoney;
    }
}
