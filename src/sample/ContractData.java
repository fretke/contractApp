package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ContractData {

    private static ContractData instance = new ContractData();
    private DateTimeFormatter formatter;

    private List<Contracts> contractList;

    public ObservableList<Contracts> getContractList() {
        if (this.contractList != null){
            return FXCollections.observableList(this.contractList);
        }
        return null;
    }

    private ContractData() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public static ContractData getInstance() {
        return instance;
    }

    public void loadContracts(String fileName) throws IOException {
        contractList = FXCollections.observableArrayList();
        String input;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            while ((input = br.readLine()) != null) {

                String[] items = input.split(",");

                if (items.length == 0) {
                    break;
                }
                Contracts validContract = new Contracts();
                validContract.setContractNumber(items[0]);
                validContract.setStartDate(LocalDate.parse(items[1], formatter));
                validContract.setContractValue(Double.parseDouble(items[2]));
                validContract.setEndDate(LocalDate.parse(items[3], formatter));
                validContract.setContractMargin(Double.parseDouble(items[4]));
                validContract.setType(Contracts.ContractType.valueOf(items[5]));
                if (!items[6].equals("null")) {
                    validContract.setReturnDate(LocalDate.parse(items[6], formatter));
                }
                if (items.length > 7) {
                    if (!items[7].equals("null") && !items[7].isEmpty()) {
                        validContract.setContractComment(items[7]);
                    }
                }
                if (items.length > 8) {
                    int i = 8;
                    while (!items[i].equals("null") && !items[i].isEmpty()) {
                        Payment payment = new Payment(LocalDate.parse(items[i], formatter), Double.parseDouble(items[i + 1]));
                        validContract.addPayment(payment);
                        i += 2;
                        if (i >= items.length) {
                            break;
                        }
                    }
                }
                validContract.calculateProfit();
                contractList.add(validContract);
            }
        }
    }

    public void saveContracts(String fileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
            Iterator<Contracts> iterator = contractList.iterator();
            while (iterator.hasNext()) {
                Contracts contract = iterator.next();
                bw.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s", contract.getContractNumber(),
                        contract.getStartDate(), contract.getContractValue(), contract.getEndDate(),
                        contract.getContractMargin(), contract.getType(), contract.getReturnDate(), contract.getContractComment()));
                for (Payment payment : contract.getListOfPayments()) {
                    bw.write(String.format(",%s,%s", payment.getDateOfPayment(), payment.getPaymentAmount()));
                }
                bw.newLine();
            }
        }
    }

    public boolean addContracts(Contracts contract) {
        if (!checkIfExists(contract)) {
            contractList.add(contract);
            contract.calculateProfit();
            return true;
        }
        return false;
    }

    public void removeContract(Contracts contract) {
        contractList.remove(contract);
    }

    public boolean checkIfExists(Contracts current){
        return false;
//        for (Contracts contract : contractList) {
//            if (contract.getContractNumber().equals(current.getContractNumber()) &&
//                    contract.getType().equals(current.getType())) {
//                return true;
//            }
//        }
//        return false;
    }

    public boolean checkIfExist(String contractNumber) {
        return false;
//        for (Contracts contract : contractList) {
//            if (contract.getContractNumber().equals(contractNumber))
//                return true;
//        }
//        return false;
    }

    public ObservableList<Contracts> searchContract(String contractNumber, boolean checkButtonState, boolean checkButtonAlmostLate) {
        ObservableList<Contracts> searchList = FXCollections.observableArrayList();
       for (Contracts contract : contractList) {
            if (contract.getContractNumber().startsWith(contractNumber) && !checkButtonState && !checkButtonAlmostLate) {
                searchList.add(contract);
            } else if (contract.getContractNumber().startsWith(contractNumber) && checkButtonState && !checkButtonAlmostLate &&
                    contract.getEndDate().isBefore(LocalDate.now()) && contract.getReturnDate() == null) {
                searchList.add(contract);
            }
            else if (contract.getContractNumber().startsWith(contractNumber) && !checkButtonState && checkButtonAlmostLate && contract.getReturnDate() == null){
                if ((contract.getEndDate().isBefore(LocalDate.now().plusDays(3)) || contract.getEndDate().isEqual(LocalDate.now().plusDays(3)))
                && ( contract.getEndDate().isAfter(LocalDate.now()) || contract.getEndDate().isEqual(LocalDate.now()))){
                    searchList.add(contract);
                }
            } else if (contract.getContractNumber().startsWith(contractNumber) && checkButtonState && checkButtonAlmostLate &&
                    contract.getReturnDate() == null){
                if ((contract.getEndDate().isBefore(LocalDate.now().plusDays(3)) || contract.getEndDate().isEqual(LocalDate.now().plusDays(3))) && (contract.getEndDate().isAfter(LocalDate.now()) ||
                        contract.getEndDate().isEqual(LocalDate.now())) || contract.getEndDate().isBefore(LocalDate.now())){
                    searchList.add(contract);
                }
            }
        }
        return searchList;
    }

    public ObservableList<Contracts> searchContract(String contractNumber) {
        ObservableList<Contracts> searchList = FXCollections.observableArrayList();

        for (Contracts contract: contractList){
            if (contract.getReturnDate()!=null && contract.getContractNumber().startsWith(contractNumber)){
                searchList.add(contract);
            }
        }
        return searchList;
    }

    public int getNumberOfContracts(Predicate<Contracts> condition) {
        int qty = 0;
        for (Contracts contract: contractList){
            if (condition.test(contract) && contract.getReturnDate()==null){
                qty++;
            }
        }
        return qty;
    }

    public double getTotalValueOfContracts(Predicate<Contracts> condition) {
        double value = 0;
        for (Contracts contract : contractList) {
            if (condition.test(contract) && contract.getReturnDate() == null) {
                value += contract.getContractValue();
            }
        }
        value = Math.round(value * 100.00) / 100.00;
        return value;
    }

    public double profitForPeriod(LocalDate start, LocalDate end, Predicate<Contracts> condition) {
        double profit = 0;
        for (Contracts contract : contractList) {
            if (condition.test(contract)){
                profit += contract.calculateProfitBetween(start, end);
            }
        }
        return profit;
    }

    public double realPaymentsForPeriod(LocalDate start, LocalDate end, Predicate<Contracts> condition) {
        double sumOfPayments = 0;
        for (Contracts contract : contractList) {
            if (condition.test(contract)){
                for (Payment payment : contract.getListOfPayments()) {
                    if ((payment.getDateOfPayment().isAfter(start) || payment.getDateOfPayment().isEqual(start)) && (payment.getDateOfPayment().isBefore(end) || payment.getDateOfPayment().isEqual(end))) {
                        sumOfPayments += payment.getPaymentAmount();
                    }
                }
            }

        }
        return sumOfPayments;
    }
}
