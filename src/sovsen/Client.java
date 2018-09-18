package sovsen;

import java.io.*;
import java.net.*;

/**
 * @Author SovsenGrp 12-Sep-18.
 */
public class Client {

    static Socket socket;
    static BufferedReader stdIn;
    static String fromServer;
    static BufferedReader toClient = null;
    static PrintWriter toServer = null;

    private static int[][] grid = new int[3][3];


    public static void main(String[] args) {

        try {


            socket = new Socket("localhost", 3001);
            System.out.println("Localhost");
            toClient = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            toServer = new PrintWriter(
                    socket.getOutputStream(), true
            );


            running();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }



        //Close the connection and exit
        // dis.close();
        //s1In.close();
        //s1.close();


        /*try {
            Socket s = new Socket("127.0.0.1",3001);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            String msg = dis.readUTF();

            System.out.println(msg);
        } catch (Exception e) {
        }
    }*/
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

}