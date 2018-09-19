package sovsen;

import java.io.DataOutputStream;
import java.io.*;
import java.net.*;
import java.sql.SQLOutput;

/**
 * @Author SovsenGrp on 12-Sep-18.
 */
public class Server {

    static ServerSocket server;
    static Socket client;
    static BufferedReader toServer;
    static PrintWriter toClient;

    public static void main(String[] args) {

        try {
            server = new ServerSocket(3001);
            System.out.println("Server listening");
            run();



        } catch (Exception e) {

        }

      /*  try {

            ServerSocket server = new ServerSocket(3001);

            Socket s = server.accept();
            System.out.println("Connected");

            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF("Welcome to Socket");
        } catch (Exception e) {
        }*/
    }




    public static void run(){

        System.out.println("The server is running");
        boolean running = true;
        boolean maxUsers = false;
        while(running){


            while (maxUsers == false){
                maxUsers = listenForUsers();
                if(Game.getObservers() < 2){
                    writeAutomaticMessage("You have been connected! Waiting for another player...");
                } else {
                    System.out.println("MAXUSERS");
                    maxUsers = true;
                }
            }


            System.out.println("Run:Listen");
            Game.initGame();
            running = listen();

        }
    }

    public static boolean listenForUsers(){
        System.out.println("Listening for users");
        try{

            client = server.accept();
            System.out.println("Client " + client.toString() + " has joined!");
            ServerThread s =  new ServerThread(client);
            Game.attach(s);

            //Notify client they have connected
            writeAutomaticMessage("You have connected to TicTacToe server!");

            System.out.println("Number of users: " + Game.getObservers());
            if (Game.getObservers() >= 2){

                System.out.println("Return");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection error");
        }


        return false;
    }


    public static boolean listen(){
        System.out.println("Server is listening");
        String inputLine = "";



        try {
            toServer = new BufferedReader(
                new InputStreamReader(client.getInputStream())
        );

            while ((inputLine = toServer.readLine()) != null) {
                System.out.println("Client says: " + inputLine);

                write(inputLine);

            }
        } catch (IOException ex) {

            System.out.println("IOException in client. " + ex.getCause());
            return false;
        } catch (NullPointerException npe) {

            System.out.println("Client threw exception: NullPointerException");
            npe.printStackTrace();
            System.out.println("Cause for exception: " + npe.getCause());

            return false;
        }





        return true;
    }


    public static void writeAutomaticMessage(String message){

        try {

            toClient = new PrintWriter(client.getOutputStream(), true);

            if (message != null) {
                System.out.println("Server: " + message);
                toClient.println(message);

            }

        } catch (IOException IOE) {
            System.out.println("Automatic message failure!");
            IOE.printStackTrace();



        }
    }


    public static void write(String message){

        try {

            toClient = new PrintWriter(client.getOutputStream(), true);

            String outputLine = "";


            outputLine = Game.processInput(message);

            toClient.write(outputLine);


            if (outputLine != null) {
                System.out.println("Server: " + outputLine);
                toClient.println(outputLine);

            }

        } catch (IOException IOE) {
            System.out.println("Automatic message failure!");
            IOE.printStackTrace();



        }


    }




    public static void notifyAllObservers(){
        System.out.println("NotifyAllObservers()");

        try {


            for (ServerThread s : Game.getAllObservers()) {

                toClient = new PrintWriter(s.getSocket().getOutputStream(), true);

                //Change to the updating line
                String outputLine = "heya";

                if (outputLine != null) {
                    System.out.println("Server: " + outputLine);
                    toClient.println(outputLine);

                }



            }
        } catch (IOException IOE) {
            System.out.println("Automatic message failure!");
            IOE.printStackTrace();



        }





    }


    public static void closeServer() throws IOException{
        System.out.println("Close connection");

        try {
            System.out.println("Connection closing");
            if (toServer != null){
                toServer.close();
            }

            if (toClient != null){
                toClient.close();
                System.out.println("Output socket stream closed");
            }

            if (client != null){
                client.close();
            }

        } catch (IOException ex){
            System.out.println("Socket close error: " + ex.getCause());
        }

    }


}