package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.function.Predicate;

public class ExpectedProfitPaneController {
    @FXML
    private Label totalContractNumber;
    @FXML
    private Label borrowedAmount;
    @FXML
    private DatePicker searchStart;
    @FXML
    private DatePicker searchEnd;
    @FXML
    private Label profitForPeriod;

    @FXML private ChoiceBox<String> choiceBox;

    private ObservableList<String> listOfChoices = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        LocalDate current = LocalDate.now();
        searchStart.setValue(current.withDayOfMonth(1));
        searchEnd.setValue(current.withDayOfMonth(current.lengthOfMonth()));
        searchEnd.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(searchStart.getValue()));
            }
        });

        String gold = "AUKSAS";
        String items = "DAIKTAI";
        String business = "VERSLAS";
        String all = "VISI";
        listOfChoices.addAll(gold, items, business, all);
        choiceBox.getItems().setAll(listOfChoices);
        choiceBox.getSelectionModel().select(all);
        choiceOnAction();
    }
    @FXML
    public void setOverviewPane(Predicate<Contracts> condition) {
        totalContractNumber.setText("Iš viso sutarčių: " + ContractData.getInstance().getNumberOfContracts(condition));
        borrowedAmount.setText("Paskolinta už: " + ContractData.getInstance().getTotalValueOfContracts(condition));
        profitForPeriod.setText("Už periodą teorinis pelnas: " + ContractData.getInstance().profitForPeriod(searchStart.getValue(), searchEnd.getValue(), condition));
    }

    public void choiceOnAction(){
        Predicate<Contracts> condition = contracts -> true;
        switch (choiceBox.getSelectionModel().getSelectedItem()){
            case "AUKSAS":
                condition = contracts -> contracts.getType() == Contracts.ContractType.AUKSAS;
                break;
            case "DAIKTAI":
                condition = contracts -> contracts.getType() == Contracts.ContractType.DAIKTAI;
                break;
            case "VERSLAS":
                condition = contracts -> contracts.getType() == Contracts.ContractType.VERSLAS;
                break;
            default: break;
        }
        setOverviewPane(condition);
    }
}
