package sample;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

public class Controller {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private TableView<Contracts> tableView;
    @FXML private TableColumn<Contracts, String> contractsColumn;
    @FXML private TableColumn<Contracts, LocalDate> startDateColumn;
    @FXML private TableColumn<Contracts, LocalDate> endDateColumn;
    @FXML private TableColumn<Contracts, Double> valueColumn;
    @FXML private TableColumn<Contracts, String> commentsColumn;
    @FXML private TableColumn<Contracts, Double> profitColumn;
    @FXML private TableColumn<Contracts, LocalDate> returnDateColumn;
    @FXML private TextField searchField;
    @FXML private CheckBox lateContractsCheckBox;
    @FXML private CheckBox almostLateContractsCheckBox;
    @FXML private CheckBox closedContracts;
    public static String fileName = null;
    @FXML

    public void initialize() {

        tableView.setOnMouseClicked((event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){
                    showInfoPane();
                }
            }
        });
        ContextMenu contextMenu = new ContextMenu();
        MenuItem modify = new MenuItem("Modifikuoti...");
        MenuItem delete = new MenuItem("Ištrinti...");
        MenuItem information = new MenuItem("Sutarties apžvalga...");

        information.setOnAction((event) -> showInfoPane());

        modify.setOnAction((actionEvent) -> showModifyContractDialogue());
        delete.setOnAction((event -> deleteSelectedItems()));

        contextMenu.getItems().addAll(modify, information, delete);
        tableView.setContextMenu(contextMenu);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        contractsColumn.setStyle("-fx-alignment: CENTER;");
        startDateColumn.setStyle("-fx-alignment: CENTER;");
        endDateColumn.setStyle("-fx-alignment: CENTER;");
        valueColumn.setStyle("-fx-alignment: CENTER;");
        profitColumn.setStyle("-fx-alignment: CENTER;");
        returnDateColumn.setStyle("-fx-alignment: CENTER;");
        commentsColumn.setStyle("-fx-alignment: CENTER;");

        contractsColumn.setCellValueFactory(new PropertyValueFactory<>("contractNumber"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("contractValue"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        profitColumn.setCellValueFactory(new PropertyValueFactory<>("profit"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("contractComment"));

        if (ContractData.getInstance().getContractList() != null){
            SortedList<Contracts> sortedList = new SortedList<>(ContractData.getInstance().getContractList(),
                    Comparator.comparing(Contracts::getContractNumber));
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedList);
        }
    }
    @FXML
    public void showAddContractDialogue() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addModifyPane.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        dialog.setTitle("Pridėti sutartį");
        ButtonType confirm = new ButtonType("Taip", ButtonBar.ButtonData.OK_DONE);
        ButtonType decline = new ButtonType("Ne", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().add(confirm);
        dialog.getDialogPane().getButtonTypes().add(decline);

        AddModifyController controller = loader.getController();
        controller.populateFields();

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirm) {
            checkForErrors(controller.processResult(), "add");
        }
        tableView.setItems(ContractData.getInstance().getContractList());
        tableView.getSelectionModel().selectLast();
    }
    @FXML
    public void showModifyContractDialogue(){
        if (!isSelected()){
            notSelectedAlert();
            return;
        }
        Contracts contract = tableView.getSelectionModel().getSelectedItem();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addModifyPane.fxml"));

        try {
            dialog.getDialogPane().setContent(loader.load());

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        AddModifyController controller = loader.getController();
        dialog.setTitle("Modifikuoti sutartį");
        controller.populateFields(contract);

        ButtonType confirm = new ButtonType("Taip", ButtonBar.ButtonData.OK_DONE);
        ButtonType decline = new ButtonType("Ne", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().add(confirm);
        dialog.getDialogPane().getButtonTypes().add(decline);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == confirm){
            checkForErrors(controller.modifyContract(contract), "modify");
          tableView.setItems(ContractData.getInstance().getContractList());
        }
    }
    @FXML
    private void checkForErrors(int number, String method) {
        switch (number) {
            case 0:
                tableView.refresh();
                break;
            case -1:
                alert("Klaida. Privalo būti įvestas sutarties nr., suma bei palūkanų dydis");
                if (method.equals("modify")){
                    showModifyContractDialogue();
                } else {
                    showAddContractDialogue();
                }
                break;
            case -2:
                alert("Klaida. Skaičiai turi būti tokiu formatu, pvz: 21.1; 0.7451");
                if (method.equals("modify")){
                    showModifyContractDialogue();
                } else {
                    showAddContractDialogue();
                }
                break;
            case -3:
                alert("Klaida. Sutartis tokiu numeriu jau egzistuoja");
                if (method.equals("modify")){
                    showModifyContractDialogue();
                } else {
                    showAddContractDialogue();
                }
                break;
            default:
                break;
        }
    }
    @FXML
    public void showInfoPane(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Sutarties informacija");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("contractInformationPane.fxml"));

        try{
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e){
            e.printStackTrace();
        }
        ContractInformationController control = loader.getController();
        control.showContractInfo(tableView.getSelectionModel().getSelectedItem());

        ButtonType confirm = new ButtonType("Grįžti");

        dialog.getDialogPane().getButtonTypes().add(confirm);
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == confirm){
            tableView.refresh();
        }
    }

    private void alert(String alertType) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Klaida");
        alert.setHeaderText(null);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("file:errorIcon.png"));
        alert.getButtonTypes().removeAll();
        alert.getButtonTypes().add(new ButtonType("Grįžti"));
        alert.setContentText(alertType);
        alert.showAndWait();
    }
    @FXML
    private void deleteAlert(Contracts contract){
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(null);
        Stage newStage = (Stage) alert.getDialogPane().getScene().getWindow();
        newStage.getIcons().add(new Image("file:errorIcon.png"));
        alert.setContentText("Ar tikrai norite ištrinti " + contract.getContractNumber() + " sutartį?");
        ButtonType confirm = new ButtonType("Taip");
        alert.getButtonTypes().add(confirm);
        alert.getButtonTypes().add(new ButtonType("Ne"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirm){
            ContractData.getInstance().removeContract(contract);
            tableView.refresh();
            searchContract();
        }
    }

    public void notSelectedAlert(){
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(null);
        Stage newStage = (Stage) alert.getDialogPane().getScene().getWindow();
        newStage.getIcons().add(new Image("file:errorIcon.png"));
        alert.setContentText("Pasirinkite sutartį.");
        alert.getButtonTypes().add(new ButtonType("Grįžti"));
        alert.showAndWait();
    }
    @FXML
    public void deleteContract(KeyEvent keyEvent) {
        Contracts contract = tableView.getSelectionModel().getSelectedItem();
        if (contract != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteAlert(contract);
            }
        }
        tableView.getSelectionModel().selectNext();
    }
    @FXML
    public void deleteContractMouse(){
        if (!isSelected()){
            notSelectedAlert();
            return;
        }
        Contracts contract = tableView.getSelectionModel().getSelectedItem();
        deleteAlert(contract);
    }
    @FXML
    public void searchContract(){
        String contractNumber = searchField.getText();

        if (closedContracts.isSelected()){
            almostLateContractsCheckBox.setSelected(false);
            lateContractsCheckBox.setSelected(false);
            tableView.setItems(ContractData.getInstance().searchContract(contractNumber));
            return;
        }
            tableView.setItems(ContractData.getInstance().searchContract(contractNumber, lateContractsCheckBox.isSelected(), almostLateContractsCheckBox.isSelected()));
    }
    @FXML
    public void expectedProfitPane(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Teorinis");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ExpectedProfitPane.fxml"));

        try{
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e){
            e.printStackTrace();
        }
        ButtonType back = new ButtonType("Grįžti", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(back);
        dialog.showAndWait();






    }

    @FXML
    public void overviewPane(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Pelnas");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Overview.fxml"));

        try{
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e){
            e.printStackTrace();
        }
        ButtonType back = new ButtonType("Grįžti", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(back);
        dialog.showAndWait();
    }
    private boolean isSelected(){
        return tableView.getSelectionModel().getSelectedItem() != null;
    }

    public void save() throws IOException {
        if (fileName == null){
            ContractData.getInstance().saveContracts("Duomenys.txt");
        } else {
            ContractData.getInstance().saveContracts(fileName);
        }
    }
    public void exit(){
        Window window = mainBorderPane.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    @FXML
    public void deleteSelectedItems(){
        ObservableList<Contracts> list = tableView.getSelectionModel().getSelectedItems();

        if (list == null){
            notSelectedAlert();
            return;
        } else if (list.size() <2){
            deleteContractMouse();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(null);
        Stage newStage = (Stage) alert.getDialogPane().getScene().getWindow();
        newStage.getIcons().add(new Image("file:errorIcon.png"));
        alert.setContentText("Ar tikrai norite ištrinti pasirinktas sutartis?");
        ButtonType confirm = new ButtonType("Taip");
        alert.getButtonTypes().add(confirm);
        alert.getButtonTypes().add(new ButtonType("Ne"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirm){
            for (Contracts contract:list){
                ContractData.getInstance().removeContract(contract);
            }
            tableView.setItems(ContractData.getInstance().getContractList());
        }
    }
    public void handleOpen() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text", "*.txt"));
        File file = chooser.showOpenDialog(mainBorderPane.getScene().getWindow());
        if (file != null){
            ContractData.getInstance().loadContracts(file.getPath());
            initialize();
        }
        assert file != null;
        fileName = file.getPath();

    }
    public void handleSaveAs() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text", "*.txt"));
        File file = chooser.showSaveDialog(mainBorderPane.getScene().getWindow());
        if (file != null){
            ContractData.getInstance().saveContracts(file.getPath());
        }
    }
}
