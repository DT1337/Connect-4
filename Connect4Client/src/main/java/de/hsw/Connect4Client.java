package de.hsw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Connect4Client extends Application {

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Connect 4");

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/connect4.png")));
            stage.getIcons().add(icon);

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
            System.err.println("[CLIENT]: Error while starting client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("[CLIENT]");
        System.out.println("[CLIENT]: Welcome to Connect 4!");
        launch(args);
    }
}
