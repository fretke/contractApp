package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Contracts{
    private String contractNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate returnDate;
    private double contractValue;
    private double contractMargin;
    private double profit;
    private String contractComment;
    private ContractType type;

    enum ContractType{
        AUKSAS,
        DAIKTAI,
        VERSLAS,
    }

    private ObservableList<Payment> listOfPayments;

    public Contracts() {
        this.contractMargin = 0.01;
        this.listOfPayments = FXCollections.observableArrayList();
    }

    public Contracts(String contractNumber, LocalDate startDate, LocalDate endDate, double contractValue, ContractType type) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractNumber = contractNumber;
        this.contractValue = contractValue;
        this.contractMargin = 0.01;
        this.listOfPayments = FXCollections.observableArrayList();
        this.type = type;
        calculateProfit();

    }

    public ContractType getType() {
        return type;
    }

    public void setType(ContractType type) {
        this.type = type;
    }

    public double getContractMargin() {
        return contractMargin;
    }

    public ObservableList<Payment> getListOfPayments() {
        return listOfPayments;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getProfit() {
        return profit;
    }

    public String getContractComment() {
        return contractComment;
    }

    public void calculateProfit(){
        double profit;
        if (this.returnDate == null || this.returnDate.isBefore(this.startDate)){
            profit = calculateProfit(LocalDate.now());
        } else {
            profit = calculateProfit(this.returnDate);
        }
        if (profit < 1.5){
            profit = 1.5;
        }
        this.profit = profit;
    }

    public double calculateProfitBetween(LocalDate start, LocalDate end){
        double profit = 0;
        if (start.isBefore(this.startDate) && end.isBefore(this.startDate)){
//            System.out.println("1");
            return 0;
        }
        if (this.returnDate != null){
            if (start.isBefore(this.startDate) && (end.isBefore(this.returnDate) || end.isEqual(this.returnDate))){
//                System.out.println("2");
                profit = calculateProfit(end);
            } else if (start.isAfter(this.startDate) && (end.isBefore(this.returnDate) || end.isEqual(this.returnDate))){
//                System.out.println("3");
                profit = calculateProfit(end) - calculateProfit(start);
            } else if ((start.isAfter(this.startDate) || start.isEqual(this.startDate)) && (start.isBefore(this.returnDate) || start.isEqual(this.returnDate)) && end.isAfter(this.returnDate)){
//                System.out.println("4");
                profit = calculateProfit(this.returnDate) - calculateProfit(start);
            } else if (start.isBefore(this.startDate) && end.isAfter(this.returnDate)){
//                System.out.println("5");
                profit = calculateProfit(this.returnDate);
            } else if (start.isAfter(this.returnDate) && end.isAfter(this.returnDate)){
//                System.out.println("8");
                profit = 0;
            }
        } else if (start.isAfter(this.startDate)){
//            System.out.println("6");
            profit = calculateProfit(end) - calculateProfit(start);
        } else {
//            System.out.println("7");
            profit = calculateProfit(end);
        }
        return profit;
    }

    public double calculateProfit(LocalDate date){
        double profit;
        long daysBetween;
        long fullPeriod;
        long shortPeriod;

        daysBetween = ChronoUnit.DAYS.between(startDate.minusDays(1), date);
        fullPeriod = daysBetween / 30;
        shortPeriod = daysBetween % 30;
        if (shortPeriod > 20) {
            shortPeriod = 20;
        }
        profit = ((20 * fullPeriod * contractMargin) + (shortPeriod * contractMargin)) * this.contractValue;
        profit = Math.round(profit * 100.0) / 100.0;
        if (profit == 0){
            return 0;
        } else if (profit < 1.5){
            return 1.5;
        }

        return profit;
    }

    public void setContractComment(String contractComment) {
        this.contractComment = contractComment;
    }

    public void setContractMargin(double contractMargin) {
        this.contractMargin = contractMargin;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getContractValue() {
        return contractValue;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setContractValue(double contractValue) {
        this.contractValue = contractValue;
    }

    public void addPayment(Payment payment){
        listOfPayments.add(payment);
    }

    public void removePayment (Payment payment){
        listOfPayments.remove(payment);
    }

    public double totalAmountPayed(){
        double amount = 0;
        for (Payment item: listOfPayments){
            amount+=item.getPaymentAmount();
        }
        return amount;
    }



    @Override
    public boolean equals(Object obj) {

        if (this == obj){
            return true;
        }
        if (obj==null || obj.getClass() != this.getClass()){
            return false;
        }

        Contracts current = (Contracts)obj;
        if (this == current){
            return true;
        } else {
            return false;
        }
//        if (this.getContractNumber().equals(current.contractNumber) && this.getType().equals(current.getType())){
//            return true;
//        } else {
//            return false;
//        }

//        return this.getContractNumber().equals(current.contractNumber);
    }

    @Override
    public int hashCode() {
        return getContractNumber().hashCode() +51;
    }

    @Override
    public String toString() {
        return contractNumber + " " + startDate + " " + endDate + " " + contractValue;
    }
}
