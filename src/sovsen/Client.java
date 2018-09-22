package sovsen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @Author SovsenGrp 12-Sep-18.
 */
public class Client extends Application {


    private static int[][] grid = new int[3][3];
    private static String fromServer;


    public Client() throws IOException {

       Socket socket;
       ClientController controller;

       //Åbner socket på port 3001
        try {

            socket = new Socket("localhost", 3001);

            //Instantierer og kører ClientController. ClientController holder al logikken for kommunikation med Server.
            controller = new ClientController(socket);
            write("Portnumber: " + controller.getSocket().getLocalPort());
            controller.run();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }
    }

    public static void printBoard(){
        String[] strArr = fromServer.split("~");
        System.out.println("Server: ");

        for (String c : strArr) {
            System.out.println("Client-c " + c + "\t");
        }
    }

    public static void write(String message){

        System.out.println(message);
    }

    public static void endClient(){
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("TicTacToe.fxml"));
            Scene scene = new Scene(root,300,320);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
