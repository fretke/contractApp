package sample;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;


public class ContractInformationController {

    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, LocalDate> paymentDateColumn;
    @FXML private TableColumn<Payment, Double> paymentSumColumn;
    @FXML private Label contractNumber;
    @FXML private Label startDate;
    @FXML private Label endDate;
    @FXML private Label contractValue;
    @FXML private Label comments;
    @FXML private DialogPane modifyPane;
    @FXML private Label totalPayed;
    @FXML private Label remaining;
    @FXML private Label isReturned;
    @FXML private Label profit;
    @FXML private Label contractCategory;
    private Contracts contract;

    @FXML
    public void initialize(Contracts contract){
        this.contract = contract;
        updateFields(contract);
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Pridėti..");
        MenuItem delete = new MenuItem("Ištrinti..");

        add.setOnAction((event) -> addPaymentPane(contract));

        delete.setOnAction((event -> deletePayment(contract)));

        menu.getItems().addAll(add, delete);
        paymentTable.setContextMenu(menu);
    }

    public void splitMenuExtend(){
        addPaymentPane(this.contract);
    }
    public void splitMenuClose(){
        openReturnContractPane(this.contract);
    }
    @FXML
    public void showContractInfo(Contracts contract){
        contractNumber.setText(contract.getContractNumber());
        startDate.setText(contract.getStartDate().toString());
        endDate.setText(contract.getEndDate().toString());
        contractValue.setText(String.valueOf(contract.getContractValue()));
        profit.setText(String.valueOf(contract.getProfit()));
        comments.setText(contract.getContractComment());
        contractCategory.setText(contract.getType().toString());

        if (contract.getReturnDate() != null){
            isReturned.setText("GRĄŽINTA " + contract.getReturnDate().toString());
        }

        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfPayment"));
        paymentSumColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        paymentTable.setItems(contract.getListOfPayments());
        initialize(contract);
    }
    @FXML
    public void addPaymentPane(Contracts contract){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(modifyPane.getScene().getWindow());
        dialog.setTitle("Pratęsti sutartį");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addPaymentPane.fxml"));

        try{
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e){
            e.printStackTrace();
        }

        AddPaymentController controller = loader.getController();
        controller.initializeDate(contract);

        ButtonType confirm = new ButtonType("Taip", ButtonBar.ButtonData.OK_DONE);
        ButtonType decline = new ButtonType("Ne", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().add(confirm);
        dialog.getDialogPane().getButtonTypes().add(decline);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == confirm){
            if(controller.addPayment(contract)){
                updateFields(contract);
            } else {
                addPaymentPane(contract);
            }
        }
    }
    @FXML
    public void deletePayment(Contracts contract){
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(null);
        Stage newStage = (Stage) alert.getDialogPane().getScene().getWindow();
        newStage.getIcons().add(new Image("file:errorIcon.png"));
        alert.setContentText("Ar tikrai norite ištrinti šį mokėjimą?");
        ButtonType confirm = new ButtonType("Taip");
        alert.getButtonTypes().add(confirm);
        alert.getButtonTypes().add(new ButtonType("Ne"));


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirm){
            contract.removePayment(paymentTable.getSelectionModel().getSelectedItem());
            updateFields(contract);
        }
    }
    @FXML
    private void updateFields(Contracts contract){
        contract.calculateProfit();
        totalPayed.setText(String.valueOf(contract.totalAmountPayed()));
        double remainingSum = (contract.getProfit() - contract.totalAmountPayed());
        remainingSum = Math.round(remainingSum * 100.0)/100.0;
        remaining.setText(String.valueOf(remainingSum));
        contractValue.setText(String.valueOf(contract.getContractValue()));
        endDate.setText(contract.getEndDate().toString());
        profit.setText(String.valueOf(contract.getProfit()));
    }
    @FXML
    public void openReturnContractPane(Contracts contract){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(modifyPane.getScene().getWindow());
        dialog.setTitle("Grąžinti sutartį");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("endContractPane.fxml"));

        try{
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e){
            e.printStackTrace();
        }

        EndContractController controller = loader.getController();
        controller.populateDatePicker(contract);

        ButtonType confirm = new ButtonType("Taip", ButtonBar.ButtonData.OK_DONE);
        ButtonType decline = new ButtonType("Ne", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().add(confirm);
        dialog.getDialogPane().getButtonTypes().add(decline);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == confirm){
            controller.processResults(contract);
            if (contract.getReturnDate() != null){
                isReturned.setText("GRĄŽINTA " + contract.getReturnDate().toString());
            } else {
                isReturned.setText(null);
            }
        }
        updateFields(contract);
    }

}
