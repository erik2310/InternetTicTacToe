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

    static Socket socket;
    static BufferedReader stdIn;
    static String fromServer;
    static BufferedReader toClient = null;
    static PrintWriter toServer = null;

    private static int[][] grid = new int[3][3];


    public Client() throws IOException {
        socket = new Socket("localhost", 3001);
    }

    public static void running() {

        System.out.println("A connction is established to the server ");

        boolean running = true;
        while (running) {
            running = listen();
        }
        endClient();
    }
    public static void write(){
        System.out.println("Write");
        String fromUser;
        try {

            stdIn = new BufferedReader(
                    new InputStreamReader((System.in))
            );

            fromUser = stdIn.readLine();

            toServer = new PrintWriter(socket.getOutputStream(), true);

            if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                toServer.println(fromUser);

                Game.initGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean listen() {
        System.out.println("Listen");

        try {
            fromServer = "";
            toClient = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            while ((fromServer = toClient.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                //If server output is 'end', stop the Running-loop
                if (fromServer == "end") {
                    System.out.println("Server is offline");
                    return false;
                }
                if(socket == null){
                    System.out.println("No servers available");
                    endClient();
                }
                //WRITE
                System.out.println("Give your input");
                write();
            }
        } catch (IOException IOE) {
        }
        return true;
    }

    public static void printBoard(){
        String[] strArr = fromServer.split("~");
        System.out.println("Server: ");

        for (String c : strArr) {
            System.out.println("Client-c " + c + "\t");
        }
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
