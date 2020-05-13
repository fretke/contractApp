package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddPaymentController {
    @FXML private DatePicker payedDate;
    @FXML private DatePicker endDate;
    @FXML private TextField payedAmount;

    @FXML
    public void initializeDate(Contracts contract){
        payedDate.setValue(LocalDate.now());
        payedDate.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(contract.getStartDate()));
            }
        });
        endDate.setValue(contract.getEndDate());
        if (contract.getProfit() - contract.totalAmountPayed() != 0) {
            payedAmount.setText(String.valueOf(contract.getProfit() - contract.totalAmountPayed()));
        }
        endDate.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(contract.getStartDate()));
            }
        });
    }
    @FXML
    public boolean addPayment(Contracts contract){

        if (payedAmount.getText().isEmpty()){
            if (!contract.getEndDate().equals(endDate.getValue())){
                contract.setEndDate(endDate.getValue());
            }
            return true;
        } else if (AddModifyController.isNumeric(payedAmount.getText())){
            Payment payment = new Payment(payedDate.getValue(), Double.parseDouble(payedAmount.getText()));
            contract.addPayment(payment);
            contract.setEndDate(endDate.getValue());
            return true;
        } else {
            alert();
        }
       return false;
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
}
