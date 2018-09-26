package sovsen;

/**
 * @Author sovsen on 22-Sep-18.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Client extends Application
        implements TicTacToeConstants {
    // Indicate whether the player has the turn
    private boolean myTurn = false;

    // Indicate the token for the player
    private char myToken = ' ';

    // Indicate the token for the other player
    private char otherToken = ' ';

    // Create and initialize cells
    private Cell[][] cell = new Cell[3][3];

    // Create and initialize a title label
    private Label lblTitle = new Label();

    // Create and initialize a status label
    private Label lblStatus = new Label();

    // Indicate selected row and column by the current move
    private int rowSelected;
    private int columnSelected;

    // Input and output streams from/to server
    private DataInputStream fromServer;
    private DataOutputStream toServer;

    // Continue to play?
    private boolean continueToPlay = true;

    // Wait for the player to mark a cell
    private boolean waiting = true;

    // Host name or ip
    private String host = "localhost";

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        // Pane to hold cell
        GridPane pane = new GridPane();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                pane.add(cell[i][j] = new Cell(i, j), j, i);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(lblTitle);
        borderPane.setCenter(pane);
        borderPane.setBottom(lblStatus);

        // Create a scene and place it in the stage
        Scene scene = new Scene(borderPane, 320, 350);
//        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setTitle("Client"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.setResizable(false); // Disables full window
        primaryStage.show(); // Display the stage

        // Connect to the server
        connectToServer();
    }

    private void connectToServer() {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(host, 3001);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        } catch (ConnectException ex) {
            // Shows too many players error message if connection is refused
            showErrorMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Control the game on a separate thread
        new Thread(() -> {
            try {
                // Get notification from the server
                int player = fromServer.readInt();

                // Am I player 1 or 2?
                if (player == PLAYER_1) {
                    myToken = 'X';
                    otherToken = 'O';
                    Platform.runLater(() -> {
                        lblTitle.setText("Player 1 spiller med 'X'");
                        lblStatus.setText("Vent på at player 2 er klar");
                    });

                    // Receive startup notification from the server
                    fromServer.readInt(); // Whatever read is ignored

                    // The other player has joined
                    Platform.runLater(() ->
                            lblStatus.setText("Player 2 er klar. Jeg starter først"));

                    // It is my turn
                    myTurn = true;
                } else if (player == PLAYER_2) {
                    myToken = 'O';
                    otherToken = 'X';
                    Platform.runLater(() -> {
                        lblTitle.setText("Player 2 spiller med 'O'");
                        lblStatus.setText("Vent på player 1's tur");
                    });
                } else if (player == ERROR_TOO_MANY_PLAYERS) {
                    Platform.runLater(
                            () -> {
                                // Calls too many players error message and shows it
                                showErrorMessage();
                            }
                    );
                }

                // Continue to play
                while (continueToPlay) {
                    if (player == PLAYER_1) {
                        waitForPlayerAction(); // Wait for player 1 to move
                        sendMove(); // Send the move to the server
                        receiveInfoFromServer(); // Receive info from the server
                    } else if (player == PLAYER_2) {
                        receiveInfoFromServer(); // Receive info from the server
                        waitForPlayerAction(); // Wait for player 2 to move
                        sendMove(); // Send player 2's move to the server
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Wait for the player to mark a cell
     */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }

        waiting = true;
    }

    /**
     * Send this player's move to the server
     */
    private void sendMove() throws IOException {
        toServer.writeInt(rowSelected); // Send the selected row
        toServer.writeInt(columnSelected); // Send the selected column
    }

    /**
     * Receive info from the server
     */
    private void receiveInfoFromServer() throws IOException {
        // Receive game status
        int status = fromServer.readInt();

        if (status == PLAYER_1_WON) {
            // Player 1 won, stop playing
            continueToPlay = false;
            if (myToken == 'X') {
                Platform.runLater(() -> lblStatus.setText("Jeg vandt! (X)"));
            } else if (myToken == 'O') {
                Platform.runLater(() ->
                        lblStatus.setText("Player 1 (X) vandt!"));
                receiveMove();
            }
        } else if (status == PLAYER_2_WON) {
            // Player 2 won, stop playing
            continueToPlay = false;
            if (myToken == 'O') {
                Platform.runLater(() -> lblStatus.setText("Jeg vandt! (O)"));
            } else if (myToken == 'X') {
                Platform.runLater(() ->
                        lblStatus.setText("Player 2 (O) vandt!"));
                receiveMove();
            }
        } else if (status == DRAW) {
            // No winner, game is over
            continueToPlay = false;
            Platform.runLater(() ->
                    lblStatus.setText("Game slut, ingen vinner!"));

            if (myToken == 'O') {
                receiveMove();
            }
        } else {
            receiveMove();
            Platform.runLater(() -> lblStatus.setText("Min tur"));
            myTurn = true; // It is my turn
        }
    }

    private void receiveMove() throws IOException {
        // Get the other player's move
        int row = fromServer.readInt();
        int column = fromServer.readInt();
        Platform.runLater(() -> cell[row][column].setToken(otherToken));
    }

    // Method to show a too many players error message
    private void showErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fejl!");
        alert.setHeaderText(null);
        alert.setContentText("Der er ikke plads til flere spillere!");

        alert.showAndWait();
        System.exit(0);
    }

    // An inner class for a cell
    public class Cell extends Pane {
        // Indicate the row and column of this cell in the board
        private int row;
        private int column;

        // Token used for this cell
        private char token = ' ';

        public Cell(int row, int column) {
            this.row = row;
            this.column = column;
            this.setPrefSize(2000, 2000); // What happens without this?
            setStyle("-fx-border-color: silver"); // Set cell's border
            this.setOnMouseClicked(e -> handleMouseClick());
        }

        /**
         * Return token
         */
        public char getToken() {
            return token;
        }

        /**
         * Set a new token
         */
        public void setToken(char c) {
            token = c;
            repaint();
        }

        protected void repaint() {
            if (token == 'X') {
                Line line1 = new Line(25, 25,
                        this.getWidth() - 25, this.getHeight() - 25);
                line1.endXProperty().bind(this.widthProperty().subtract(25));
                line1.endYProperty().bind(this.heightProperty().subtract(25));
                Line line2 = new Line(25, this.getHeight() - 25,
                        this.getWidth() - 25, 25);
                line2.startYProperty().bind(
                        this.heightProperty().subtract(25));
                line2.endXProperty().bind(this.widthProperty().subtract(25));

                // Add the lines to the pane
                this.getChildren().addAll(line1, line2);
            } else if (token == 'O') {
                Ellipse ellipse = new Ellipse(this.getWidth() / 2,
                        this.getHeight() / 2, this.getWidth() / 2 - 25,
                        this.getHeight() / 2 - 25);
                ellipse.centerXProperty().bind(
                        this.widthProperty().divide(2));
                ellipse.centerYProperty().bind(
                        this.heightProperty().divide(2));
                ellipse.radiusXProperty().bind(
                        this.widthProperty().divide(2).subtract(25));
                ellipse.radiusYProperty().bind(
                        this.heightProperty().divide(2).subtract(25));
                ellipse.setStroke(Color.BLACK);
                ellipse.setFill(Color.WHITE);

                getChildren().add(ellipse); // Add the ellipse to the pane
            }
        }

        /* Handle a mouse click event */
        private void handleMouseClick() {
            // If cell is not occupied and the player has the turn
            if (token == ' ' && myTurn) {
                setToken(myToken); // Set the player's token in the cell
                myTurn = false;
                rowSelected = row;
                columnSelected = column;
                lblStatus.setText("Vent på den næste spillers tur");
                waiting = false; // Just completed a successful move
            }
        }
    }
}