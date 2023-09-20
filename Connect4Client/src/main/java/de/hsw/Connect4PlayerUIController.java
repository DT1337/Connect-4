package de.hsw;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class Connect4PlayerUIController {

    @FXML private Stage stage;

    @FXML private StackPane root;
    @FXML private Label statusLabel;

    @FXML private Label player1Label;
    @FXML private Label player2Label;
    private Label[] playerLabels;

    @FXML private GridPane connect4Board;
    @FXML private Button resetButton;

    private Button[][] buttons;

    private String IP;
    private int PORT = 7171;
    private Connect4BoardClientProxy connect4BoardClientProxy;
    private Connect4Player connect4Player;

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(windowEvent -> handleWindowClose());
    }

    public void initialize() {
        initializeServerConnectionInputDialog();
        setupCommunicationWithServer();
        initializePlayerNameInputDialog();
    }

    private void initializeServerConnectionInputDialog() {
        try {
            TextInputDialog inputDialog = createTextInputDialog("Connect 4 - Server Connection", "Please enter the Server IP:");
            Optional<String> result = inputDialog.showAndWait();
            result.ifPresent(userInput -> {
                IP = userInput;
                setupCommunicationWithServer();
            });
        } catch (Exception e) {
            handleConnectionError(e);
        }
    }

    private TextInputDialog createTextInputDialog(String title, String contentText) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle(title);
        inputDialog.setHeaderText(null);
        inputDialog.setContentText(contentText);
        return inputDialog;
    }

    private void setupCommunicationWithServer() {
        try {
            Socket socket = new Socket(IP, PORT);
            connect4BoardClientProxy = new Connect4BoardClientProxy(socket);
        } catch (IOException e) {
            handleConnectionError(e);
        }
    }

    private void initializePlayerNameInputDialog() {
        try {
            TextInputDialog inputDialog = createTextInputDialog("Connect 4 - Player Name", "Please enter your Player Name:");
            Optional<String> result = inputDialog.showAndWait();
            result.ifPresent(userInput -> {
                player1Label.setText(userInput);
                connect4Player = new Connect4Player(this, userInput);
                playerLabels = new Label[2];
                playerLabels[0] = player1Label;
                playerLabels[1] = player2Label;

                try {
                    initializeConnect4Board(connect4BoardClientProxy.getBoardState());
                    connect4BoardClientProxy.joinGame(connect4Player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            handleConnectionError(e);
        }
    }

    public void initializeConnect4Board(char[][] boardState) {
        int numRows = boardState.length;
        int numCols = boardState[0].length;
        buttons = new Button[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Button button = createBoardButton();
                buttons[row][col] = button;
                connect4Board.add(button, col, row);
            }
        }

        setColumnConstraints(numCols);
        setRowConstraints(numRows);
        updateBoardState(boardState);
    }

    private Button createBoardButton() {
        Button button = new Button();
        button.setPrefSize(600 / buttons.length, 600 / buttons.length);
        button.getStyleClass().add("connect4-button");
        button.setOnAction(event -> onConnect4BoardButtonPressed(button));
        GridPane.setHalignment(button, HPos.CENTER);
        return button;
    }

    private void setColumnConstraints(int numCols) {
        for (int col = 0; col < numCols; col++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHalignment(HPos.CENTER);
            column.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
            column.setMinWidth(10);
            column.setPrefWidth(100);
            connect4Board.getColumnConstraints().add(column);
        }
    }

    private void setRowConstraints(int numRows) {
        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setValignment(javafx.geometry.VPos.CENTER);
            rowConstraint.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
            rowConstraint.setMinHeight(10);
            rowConstraint.setPrefHeight(30);
            connect4Board.getRowConstraints().add(rowConstraint);
        }
    }

    public void updateBoardState(char[][] boardState) {
        Platform.runLater(() -> {
            int numRows = boardState.length;
            int numCols = boardState[0].length;
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    Button button = buttons[row][col];
                    char cellValue = boardState[row][col];

                    // Reset
                    button.getStyleClass().remove("red");
                    button.getStyleClass().remove("yellow");
                    button.getStyleClass().remove("winningPiece");

                    if (cellValue == 'X') {
                        button.getStyleClass().add("red");
                    } else if (cellValue == 'O') {
                        button.getStyleClass().add("yellow");
                    }
                }
            }
        });
    }

    public void updateOpponents(String[] opponents) {
        Platform.runLater(() -> {
            for (int i = 0; i < playerLabels.length; i++) {
                if (playerLabels[i] != null) {
                    playerLabels[i].setText(opponents[i]);
                }
            }
        });
    }

    public void setStatusLabel(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    public void setWinner(char winnerChar) {
        Platform.runLater(() -> {
            statusLabel.setText(connect4Player.getPlayerChar() == winnerChar ? "You won!" : "Your opponent won!");

            int[][] winningPieces;
            try {
                winningPieces = connect4BoardClientProxy.getWinningPieces();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            for (int[] piece : winningPieces) {
                buttons[piece[0]][piece[1]].getStyleClass().add("winningPiece");
            }
        });
    }

    @FXML
    private void onConnect4BoardButtonPressed(Button pressedButton) {
        Platform.runLater(() -> {
            for (Button[] button : buttons) {
                for (int col = 0; col < button.length; col++) {
                    if (button[col] == pressedButton) {
                        try {
                            makeMoveAndUpdateStatus(col);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    @FXML
    private void onResetButtonPressed() {
        try {
            if (connect4BoardClientProxy.resetBoard(connect4Player)) {
                setStatusLabel("Board reset!");
            } else {
                setStatusLabel("Cannot reset board!");
            }
        } catch (IOException e) {
            System.out.println("Error while trying to reset the board: " + e.getMessage());
        }
    }

    private void makeMoveAndUpdateStatus(int col) throws IOException {
        if (!connect4BoardClientProxy.makeMove(col, connect4Player) && !connect4BoardClientProxy.isGameOver()) {
            setStatusLabel("It's not your turn!");
        }
    }

    private void handleWindowClose() {
        try {
            if (connect4BoardClientProxy != null) {
                connect4BoardClientProxy.leaveGame(connect4Player);
                connect4BoardClientProxy.endConnection();
            }
            System.exit(0);
        } catch (IOException e) {
            handleConnectionError(e);
        }
    }

    private void handleConnectionError(Throwable throwable) {
        String errorMessage = "An error occurred: " + throwable.getMessage();
        Platform.runLater(() -> {
            statusLabel.setText(errorMessage);
        });
        System.err.println(errorMessage);
    }
}
