package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;


public class AddModifyController {
    @FXML private TextField contractNumber;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextField contractValue;
    @FXML private TextField contractInterest;
    @FXML private TextField comments;
    @FXML private ChoiceBox<String> choiceBox;

    private ObservableList<String> listOfChoices = FXCollections.observableArrayList();

    public Contracts contract;

    public void initialize(){

        String gold = "AUKSAS";
        String items = "DAIKTAI";
        String business = "VERSLAS";
        listOfChoices.addAll(gold, items, business);
        choiceBox.getItems().setAll(listOfChoices);
        choiceBox.getSelectionModel().select(items);
    }

    @FXML
    public int processResult(){
        int error;
        if ((error = checkUserInput()) < 0){
            return error;
        }

        Contracts contract = new Contracts(contractNumber.getText(), startDate.getValue(), endDate.getValue(), Double.parseDouble(contractValue.getText()),
                Contracts.ContractType.valueOf(choiceBox.getSelectionModel().getSelectedItem()));
        if (!comments.getText().isEmpty()){
            contract.setContractComment(comments.getText().replaceAll(",", " "));
        }

        if (!contractInterest.getText().equals("20")){
            contract.setContractMargin(Double.parseDouble(contractInterest.getText())/100/20);
        }

        if (ContractData.getInstance().addContracts(contract)){
            return 0;
        } else {
            return -3;
        }
    }
    @FXML
    public void populateFields(){
        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());
        endDate.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(startDate.getValue()));
            }
        });
        contractInterest.setText("20");
    }
    @FXML
    public void populateFields(Contracts contract){
        this.contract = contract;
        contractNumber.setText(contract.getContractNumber());
        startDate.setValue(contract.getStartDate());
        endDate.setValue(contract.getEndDate());
        endDate.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(startDate.getValue()));
            }
        });
        contractValue.setText(String.valueOf(contract.getContractValue()));
        contractInterest.setText(String.valueOf(contract.getContractMargin() * 100 * 20));
        comments.setText(contract.getContractComment());
        choiceBox.getSelectionModel().select(contract.getType().toString());


    }

    public static boolean isNumeric (String str){
        try{
            Double.parseDouble(str);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    @FXML
    public int modifyContract(Contracts contract){

        if (checkUserInput() < 0){
            return checkUserInput();
        }

//        Contracts current = new Contracts(contractNumber.getText(), startDate.getValue(), endDate.getValue(), Double.parseDouble(contractValue.getText()),
//                Contracts.ContractType.valueOf(choiceBox.getSelectionModel().getSelectedItem()));
//
//        current.setContractComment(comments.getText().replaceAll(",", " "));
//        if (!contractInterest.getText().equals(contract.getContractMargin())){
//            current.setContractMargin(Double.parseDouble(contractInterest.getText())/100/20);
//        }
//        ContractData.getInstance().removeContract(contract);
//        if (!ContractData.getInstance().checkIfExists(current)){
//            ContractData.getInstance().addContracts(current);
//        } else {
//            ContractData.getInstance().addContracts(contract);
//            return -3;
//        }
//        ContractData.getInstance().removeContract(contract);

        if (!contractNumber.getText().equals(contract.getContractNumber())){
            if (!ContractData.getInstance().checkIfExist(contractNumber.getText())){
                contract.setContractNumber(contractNumber.getText());
            } else {
                return -3;
            }
        }
        if (!choiceBox.getSelectionModel().getSelectedItem().equals(contract.getType().toString())){
            contract.setType(Contracts.ContractType.valueOf(choiceBox.getSelectionModel().getSelectedItem()));
        }

        if (!startDate.getValue().equals(contract.getStartDate())){
            contract.setStartDate(startDate.getValue());
        }
        if (!endDate.getValue().equals(contract.getEndDate())){
            contract.setEndDate(endDate.getValue());
        }
        if (!contractValue.getText().equals(String.valueOf(contract.getContractValue()))){
            contract.setContractValue(Double.parseDouble(contractValue.getText()));
        }
        if (!contractInterest.getText().equals(String.valueOf(contract.getContractMargin()*100*20))){
            contract.setContractMargin(Double.parseDouble(contractInterest.getText())/100/20);
            contract.calculateProfit();
        }
        if (contract.getContractComment() == null && !comments.getText().isEmpty()){
            contract.setContractComment(comments.getText().replaceAll(",", " "));
        } else if (!comments.getText().equals(contract.getContractComment())){
            contract.setContractComment(comments.getText().replaceAll(",", " "));
        }
        return 0;
    }

    @FXML
    private int checkUserInput(){
        if (contractNumber.getText().isEmpty() || contractValue.getText().isEmpty() || contractInterest.getText().isEmpty()){
            return -1;
        } else if (!isNumeric(contractValue.getText()) || !isNumeric(contractInterest.getText())){
            return -2;
        }
        return 0;
    }

}
