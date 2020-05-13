package sample;

import java.time.LocalDate;

public class Payment {

    private LocalDate dateOfPayment;
    private double paymentAmount;

    public Payment(LocalDate dateOfPayment, double payment) {
        this.dateOfPayment = dateOfPayment;
        this.paymentAmount = payment;
    }

    public LocalDate getDateOfPayment() {
        return dateOfPayment;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    @Override
    public String toString() {
        return  dateOfPayment.toString()+"," + paymentAmount;
    }
}
