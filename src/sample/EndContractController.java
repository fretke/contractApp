package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EndContractController {
    @FXML private DatePicker returnDate;
    @FXML private CheckBox checkBox;
    @FXML private TextField payedAmount;
    private Contracts contract;

    public void initialize(){
        returnDate.setDisable(true);
    }
    @FXML
    public void populateDatePicker(Contracts contract){
        this.contract = contract;
        if (contract.getReturnDate() != null){
            returnDate.setDisable(false);
            checkBox.setSelected(true);
            returnDate.setValue(contract.getReturnDate());
            payedAmount.setDisable(false);
        } else {
            returnDate.setDisable(true);
            checkBox.setSelected(false);
            returnDate.setValue(LocalDate.now());
            payedAmount.setDisable(true);
            if (contract.getProfit() - contract.totalAmountPayed() != 0) {
                payedAmount.setText(String.valueOf(contract.getProfit() - contract.totalAmountPayed()));
            }
        }
        returnDate.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(contract.getStartDate()) || item.isAfter(LocalDate.now()));
            }
        });
    }
    @FXML
    public void processResults(Contracts contract){
        if (!returnDate.isDisabled()){
            if (payedAmount.getText().isEmpty()){
                contract.setReturnDate(returnDate.getValue());
                contract.setEndDate(returnDate.getValue());
            } else if (AddModifyController.isNumeric(payedAmount.getText())){
                contract.getListOfPayments().add(new Payment(returnDate.getValue(), Double.parseDouble(payedAmount.getText())));
                contract.setReturnDate(returnDate.getValue());
                contract.setEndDate(returnDate.getValue());
            } else {
                alert();
            }
        } else {
            contract.setReturnDate(null);
        }
    }
    @FXML
    public void handleCheckBox(){
        if (checkBox.isSelected()){
            returnDate.setDisable(false);
            payedAmount.setDisable(false);
        } else {
            returnDate.setDisable(true);
            payedAmount.setDisable(true);
        }
    }
    private void alert(){
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Klaida");
        alert.setHeaderText(null);
        Stage alertStage =  (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("file:errorIcon.png"));
        alert.getButtonTypes().removeAll();
        alert.getButtonTypes().add(new ButtonType("Grįžti"));
        alert.setContentText("Skaičiai turi būti tokiu formatu, pvz: 21.1; 0.7451");
        alert.showAndWait();
    }

   @FXML private void updateSum(){
        payedAmount.setText(String.valueOf(contract.calculateProfit(returnDate.getValue())));
    }
}
