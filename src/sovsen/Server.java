package sovsen;

import java.io.*;
import java.net.*;

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
            TTTP.initTTTP();
            run();



        } catch (Exception e) {

        }
    }




    public static void run(){

        System.out.println("The server is running");
        boolean running = true;
        boolean maxUsers = false;
        Game.initGame();
        while(running){

            while (maxUsers == false){
                maxUsers = listenForUsers();
                if(Game.getObservers() < 2){
                    System.out.println("Server:while max users");
                    TTTP.write("Waiting for another player",client,0);
                   // writeAutomaticMessage("Waiting for another player...");
                } else {
                    System.out.println("MAXUSERS");
                    maxUsers = true;
                }
            }


            System.out.println("Run:Listen");
            TTTP.order();

        }
    }


    public static void openInput(){
        try{
            toServer = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void openOutput(){
        try{
            toClient = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void closeStream(){
        try{
            toClient.close();
            toServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean listenForUsers(){
        System.out.println("Listening for users");
        try{

            client = server.accept();
            System.out.println("Client " + client.toString() + " has joined!");
            ClientController s =  new ClientController(client);
            Game.attach(s);
            s.run();


            //Notify client they have connected
            //writeAutomaticMessage("You have connected to TicTacToe server!");

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

        System.out.println("Server:WriteAutomaticMessage()");
        try {

            toClient = new PrintWriter(client.getOutputStream(), true);



            if (message != null) {
                System.out.println("Server: " + message);
                TTTP.write(message, client, 0);

            }

        } catch (IOException IOE) {
            System.out.println("Automatic message failure!");
            IOE.printStackTrace();



        }
    }


    public static void write(String message){
        System.out.println("server:write()");
        try {

            //Creates a new output stream to the client socket
            toClient = new PrintWriter(client.getOutputStream(), true);
            String outputLine;
            outputLine = Game.processInput(message);


            if (outputLine != null) {
                System.out.println("Server: " + outputLine);
                //Uses the protocol to write to the client
                TTTP.write(outputLine, client, 0);

            }

        } catch (IOException IOE) {
            System.out.println("Automatic message failure!");
            IOE.printStackTrace();
        }
    }




    public static void notifyAllObservers(){
        System.out.println("NotifyAllObservers()");

        try {


            for (ClientController s : Game.getAllObservers()) {

                toClient = new PrintWriter(s.getSocket().getOutputStream(), true);
                System.out.println(s.getSocket().getOutputStream().toString());
                //Change to the updating line
                int[] test = new int[3];
                test[0] = 0;
                test[1] = 1;
                test[2] = 2;
                test[3] = 3;

                String outputLine = "heya";


                if (outputLine != null) {
                    System.out.println("Server: " + test);
                    toClient.println(test);

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