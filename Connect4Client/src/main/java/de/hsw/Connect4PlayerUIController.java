package de.hsw;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Connect4PlayerUIController {

    @FXML private Stage stage;

    @FXML private Label statusLabel;

    @FXML private Label player1Label;
    @FXML private Label player2Label;

    @FXML private GridPane connect4Board;

    private Button[][] buttons;
    private char[][] boardState;
    private String[] opponents;

    private String IP;
    private int PORT;
    private Connect4BoardClientProxy connect4BoardClientProxy;
    private Connect4Player connect4Player;
    private String playerName;

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(windowEvent -> handleWindowClose());
    }

    public void initializeConnect4Game() {
        initializeServerConnectionInputDialog();
        setupCommunicationWithServer();
        initializePlayerNameInputDialog();
    }

    private void initializeServerConnectionInputDialog() {
        try {
            Dialog<Pair<String, Integer>> dialog = new Dialog<>();
            dialog.setTitle("Connect 4 - Server Connection");
            dialog.setHeaderText("Please enter the IP address and Port");

            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/connect4.png"))));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            // IP Address
            TextField ipAddressField = new TextField();
            ipAddressField.setPromptText("IP Address");
            grid.add(new Label("IP Address:"), 0, 0);
            grid.add(ipAddressField, 1, 0);

            // PORT
            TextField portField = new TextField();
            portField.setPromptText("Port");
            grid.add(new Label("Port:"), 0, 1);
            grid.add(portField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            ButtonType connectButton = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(connectButton, ButtonType.CANCEL);

            Platform.runLater(ipAddressField::requestFocus);

            dialog.setResultConverter(clickedButton -> {
                try {
                    if (clickedButton == connectButton) {
                        return new Pair<>(ipAddressField.getText(), Integer.parseInt(portField.getText()));
                    }

                    return null;
                } catch (NumberFormatException e) {
                    closeStage();
                    throw new RuntimeException("[UI - CONTROLLER]: Oops! That not a valid PORT number!");
                }
            });

            dialog.showAndWait().ifPresentOrElse(userInput -> {
                IP = userInput.getKey();
                PORT = userInput.getValue();
            }, () -> {
                closeStage();
                throw new RuntimeException("[UI - CONTROLLER]: Oops! You forgot to specify the IP and port. Thus missing Connect(ion) 4 playing.");
            });
        } catch (Exception e) {
            handleConnectionError(e);
        }
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
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Connect 4 - Player Name");
            dialog.setHeaderText("Please enter your Player Name");

            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/connect4.png"))));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            // Player Name
            TextField playerNameField = new TextField();
            playerNameField.setPromptText("Player Name");
            grid.add(new Label("Player Name:"), 0, 0);
            grid.add(playerNameField, 1, 0);

            dialog.getDialogPane().setContent(grid);

            ButtonType joinButton = new ButtonType("Join", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(joinButton, ButtonType.CANCEL);

            Platform.runLater(playerNameField::requestFocus);

            dialog.setResultConverter(clickedButton -> {
                if (clickedButton == joinButton) {
                    return playerNameField.getText();
                }

                return null;
            });

            dialog.showAndWait().ifPresentOrElse(userInput -> {
                playerName = userInput;
                player1Label.setText(playerName);
                connect4Player = new Connect4Player(this, playerName);

                try {
                    initializeConnect4Board(connect4BoardClientProxy.getBoardState());
                    connect4BoardClientProxy.joinGame(connect4Player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                closeStage();
                throw new RuntimeException("[UI - CONTROLLER]: Oops! You forgot to specify Player Name.");
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
                connect4Board.add(button, col, row);
                buttons[row][col] = button;
            }
        }

        setColumnConstraints(numCols);
        setRowConstraints(numRows);
        updateBoardState(boardState);
    }

    private Button createBoardButton() {
        Button button = new Button();
        button.setPrefSize(12.0 * buttons.length, 12.0 * buttons.length);
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
            connect4Board.getColumnConstraints().add(column);
        }
    }

    private void setRowConstraints(int numRows) {
        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setValignment(javafx.geometry.VPos.CENTER);
            rowConstraint.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
            connect4Board.getRowConstraints().add(rowConstraint);
        }
    }

    public void updateBoardState(char[][] boardState) {
        this.boardState = boardState;

        int numRows = boardState.length;
        int numCols = boardState[0].length;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Button button = buttons[row][col];
                char cellValue = boardState[row][col];

                Platform.runLater(() -> {
                    // Reset
                    button.getStyleClass().clear();
                    button.getStyleClass().add("connect4-button");

                    if (opponents.length == 2) {
                        switch (cellValue) {
                            case 'X' -> button.getStyleClass().add("red");
                            case 'O' -> button.getStyleClass().add("yellow");
                        }
                    } else {
                        switch (cellValue) {
                            case 'A' -> button.getStyleClass().add("pink");
                            case 'B' -> button.getStyleClass().add("cyan");
                            case 'C' -> button.getStyleClass().add("purple");
                            case 'D' -> button.getStyleClass().add("orange");
                            case 'E' -> button.getStyleClass().add("light-red");
                            case 'F' -> button.getStyleClass().add("blue");
                            case 'G' -> button.getStyleClass().add("yellow");
                            case 'H' -> button.getStyleClass().add("red");
                        }
                    }
                });


            }
        }
    }

    public void updateOpponents(String[] opponents) {
        this.opponents = opponents;
        ArrayList<String> opponentsList = new ArrayList<>(Arrays.asList(opponents));
        opponentsList.remove(playerName);

        Platform.runLater(() -> {
            player1Label.setText(playerName);
            player2Label.setText(String.join(" ; ", opponentsList));

            if (opponents.length == 2) {
                switch (connect4Player.getPlayerChar()) {
                    case 'X' -> {
                        player1Label.getStyleClass().add("red-player-name");
                        player2Label.getStyleClass().add("yellow-player-name");
                    }
                    case 'O' -> {
                        player1Label.getStyleClass().add("yellow-player-name");
                        player2Label.getStyleClass().add("red-player-name");
                    }
                }
            } else {
                switch (connect4Player.getPlayerChar()) {
                    case 'A' -> player1Label.getStyleClass().add("pink-player-name");
                    case 'B' -> player1Label.getStyleClass().add("cyan-player-name");
                    case 'C' -> player1Label.getStyleClass().add("purple-player-name");
                    case 'D' -> player1Label.getStyleClass().add("orange-player-name");
                    case 'E' -> player1Label.getStyleClass().add("green-player-name");
                    case 'F' -> player1Label.getStyleClass().add("blue-player-name");
                    case 'G' -> player1Label.getStyleClass().add("yellow-player-name");
                    case 'H' -> player1Label.getStyleClass().add("red-player-name");
                }
            }
        });
    }

    public void setStatusLabel(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    public void setWinner(char winnerChar) {
        Platform.runLater(() -> {
            if (winnerChar == '-') {
                setStatusLabel("It's a tie!");
                return;
            }

            try {
                setWinningLabel();

                int[][] winningPieces;
                winningPieces = connect4BoardClientProxy.getWinningPieces();

                for (int[] piece : winningPieces) {
                    buttons[piece[0]][piece[1]].getStyleClass().add("winningPiece");
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[UI - CONTROLLER]: Error while trying to set the winner: " + e.getMessage());
            }
        });
    }

    private void setWinningLabel() throws IOException {
        if (opponents.length == 2) {
            setStatusLabel(connect4Player.getPlayerChar() == connect4BoardClientProxy.getWinner() ? "You won!" : player2Label.getText() + " won!");
        } else {
            setStatusLabel(connect4Player.getPlayerChar() == connect4BoardClientProxy.getWinner() ? "You won!" : "Your opponent won!");
        }
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
                            System.err.println("[UI - CONTROLLER]: Error while trying to make a move: " + e.getMessage());
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
            System.err.println("[UI - CONTROLLER]: Error while trying to reset the board: " + e.getMessage());
        }
    }

    private void makeMoveAndUpdateStatus(int col) throws IOException {
        if (!connect4BoardClientProxy.makeMove(col, connect4Player) && !connect4BoardClientProxy.isGameOver()) {
            setStatusLabel("It's not your turn!");
        } else if (connect4BoardClientProxy.isGameOver()) {
            setWinningLabel();
        } else {
            setStatusLabel("");
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
        throwable.printStackTrace();
        String errorMessage = "An error occurred: " + throwable.getMessage();
        Platform.runLater(() -> setStatusLabel(errorMessage));
        System.err.println("[UI - CONTROLLER]: " + errorMessage);
    }

    private void closeStage() {
        stage.close();
        System.exit(1);
    }
}
