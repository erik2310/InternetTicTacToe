package sovsen;

import javafx.application.Application;

import java.io.DataOutputStream;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author SovsenGrp on 12-Sep-18.
 */
public class Server {

    public static void main(String[] args) {
        try {

            ServerSocket server;

            server = new ServerSocket(3001);
            System.out.println("Server listening");
            ServerController controller = new ServerController(server);

            controller.run();


        } catch (IOException e) {
            System.out.println("Initializing the server failed, " + e.getCause());
            e.printStackTrace();
        }
    }


}