package de.hsw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Connect4Client extends Application {

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Connect 4 Board");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/connect4Player.fxml"));
            Parent root = loader.load();

            Connect4PlayerUIController connect4PlayerUIController = loader.getController();
            connect4PlayerUIController.setStage(stage);
            connect4PlayerUIController.initializeConnect4Game();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while starting Client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Client");
        System.out.println("Welcome to Connect 4");
        launch(args);
    }
}
