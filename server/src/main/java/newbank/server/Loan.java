package newbank.server;

public class Loan {

    private int amount;
    private Customer customerRequestingLoan;
    /**
     * The loan provide will be null if no customer has provided the loan yet.
     */
    private Customer loanProvider;

    public Loan(int amount, Customer customerRequestingLoan ){
        this.amount = amount;
        this.customerRequestingLoan = customerRequestingLoan;
        this.loanProvider = null;
    }

}
