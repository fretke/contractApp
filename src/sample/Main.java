package sample;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Lombardas v1.4");
        primaryStage.setScene(new Scene(root, 1000, 400));
        Image image = new Image("file:inventory.png");
        primaryStage.getIcons().add(image);
        primaryStage.show();
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

    }
    private void closeWindowEvent(WindowEvent event) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setContentText("Ar norite išsaugoti pakeitimus prieš uždarant programą?");
        Stage newStage = (Stage) alert.getDialogPane().getScene().getWindow();
        newStage.getIcons().add(new Image("file:errorIcon.png"));
        ButtonType confirm = new ButtonType("Taip", ButtonBar.ButtonData.OK_DONE);
        ButtonType decline = new ButtonType("Ne", ButtonBar.ButtonData.OK_DONE);
        ButtonType back = new ButtonType("Grįžti", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().addAll(confirm,decline, back);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirm) {
            try{
                if (Controller.fileName == null){
                    ContractData.getInstance().saveContracts("Duomenys.txt");
                } else {
                    ContractData.getInstance().saveContracts(Controller.fileName);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        } else if (result.isPresent() && result.get() == decline) {
            Platform.exit();
        } else {
            event.consume();
        }
    }

    @Override
    public void init(){
        try{
            ContractData.getInstance().loadContracts("Duomenys.txt");
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
