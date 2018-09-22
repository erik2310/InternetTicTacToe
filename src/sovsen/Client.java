package sovsen;

import java.io.*;
import java.net.Socket;

/**
 * @Author SovsenGrp 12-Sep-18.
 */
public class Client {

    private static int[][] grid = new int[3][3];

    public static void main(String[] args) {

       Socket socket;
       ClientController controller;

       //Åbner socket på port 3001
        try {

            socket = new Socket("localhost", 3001);

            //Instantierer og kører ClientController. ClientController holder al logikken for kommunikation med Server.
            controller = new ClientController(socket);
            controller.run();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }
    }


    public static void print(String message){
        System.out.println(message);
    }

    public void endClient(){

    }

}