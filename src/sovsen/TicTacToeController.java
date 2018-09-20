package sovsen;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;


public class TicTacToeController {

    @FXML
    Button b1;
    @FXML
    Button b2;
    @FXML
    Button b3;
    @FXML
    Button b4;
    @FXML
    Button b5;
    @FXML
    Button b6;
    @FXML
    Button b7;
    @FXML
    Button b8;
    @FXML
    Button b9;

    @FXML
    GridPane gameBoard;

    private boolean PLAYER_X = true;

    public void buttonClickHandler(ActionEvent evt) {

        Button clickedButton = (Button) evt.getTarget();
        String buttonLabel = clickedButton.getText();

        if ("".equals(buttonLabel) && PLAYER_X) {
            clickedButton.setText("X");
            PLAYER_X = false;
        } else if ("".equals(buttonLabel) && !PLAYER_X) {
            clickedButton.setText("O");
            PLAYER_X = true;
        }
    }
        public void menuClickHandler(ActionEvent evt){
            MenuItem clickedMenu = (MenuItem) evt.getTarget();
            String menuLabel = clickedMenu.getText();

            if ("Spil".equals(menuLabel)){
                ObservableList<Node> buttons =
                        gameBoard.getChildren();


            }
    }
}