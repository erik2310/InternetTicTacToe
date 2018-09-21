package sovsen;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author SovsenGrp on 12-Sep-18.
 */
public class Server {

    private static List<ClientController> observers = new ArrayList<ClientController>();

    static ServerSocket server;
    static Socket client;
    static BufferedReader toServer;
    static PrintWriter toClient;
    static ClientController playerX;
    static ClientController playerO;

    static int[] ite = new int[4];

    static int SERVER = 0;
    static int CLIENT1 = 1;
    static int CLIENT2 = 2;
    static int END = 3;


    public static void main(String[] args) {
        try {
            System.out.println("initTTTP");
            ite[0] = 0;
            ite[1] = 1;
            ite[2] = 0;
            ite[3] = 2;

            server = new ServerSocket(3001);
            System.out.println("Server listening");
            run();

        } catch (IOException e) {
            System.out.println("Initializing the server failed, " + e.getCause());
            e.printStackTrace();
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
                if(getObservers() < 2){
                    System.out.println("Server:while max users");
                    writeAutomaticMessage("Waiting for another player...");
                } else {
                    System.out.println("MAXUSERS");
                    playerX = observers.get(0);
                    playerO = observers.get(1);
                    maxUsers = true;
                }
            }

            System.out.println("Run:Listen");
           running = TTTP();

        }
    }


    public static void sort(){
        System.out.println("TTTP:sort()");
        ite[0] = ite[1];
        ite[1] = ite[2];
        ite[2] = ite[3];
        ite[3] = ite[0];
    }


    public static boolean TTTP(){
        System.out.println("TTTP");
        boolean running = true;

        while(running){
            if (ite[0] == END){
                running = false;
            }

            if(ite[0] == SERVER){

                Client.print("Server is writing...");
                notifyAllObservers();
                if (playerX != null){
                    playerX.listen();}

                if (playerO != null){
                    playerO.listen();}

                write("Test");
            } else if (ite[0] == CLIENT1){

                Client.print("client1 is writing...");

                try {
                    if (playerO != null){
                        playerO.wait();}
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (playerX != null){
                    playerX.write();
                    listen();}
            } else if (ite[0] == CLIENT2){

                Client.print("Client2 is writing...");
                listen();
                if (playerX != null){
                    playerX.listen();}
                if (playerO != null){
                    playerO.write();}

            }

            sort();
        }

        return true;
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

    public static void closeStream(){

        try{
            if (toClient != null){toClient.close();}

            if (toServer != null){toServer.close();}
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
            attach(s);

            //Notify client they have connected
            writeAutomaticMessage("You have connected to TicTacToe server!");

            System.out.println("Number of users: " + getObservers());
            if (getObservers() >= 2){

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
        openInput();

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

        System.out.println("Server:WriteAutomaticMessage()");
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
        System.out.println("server:write()");

        openOutput();
        //Creates a new output stream to the client socket
        String outputLine;
        outputLine = Game.processInput(message);

        if (outputLine != null) {
            System.out.println("Server: " + outputLine);
            //Uses the protocol to write to the client
            toClient.println(outputLine);
        }

    }




    public static void notifyAllObservers(){

        synchronized (observers){
            observers.notifyAll();

        }

        try {
            for (ClientController s : getAllObservers()) {

                toClient = new PrintWriter(s.getSocket().getOutputStream(), true);
                System.out.println(s.getSocket().getOutputStream().toString());
                //Change to the updating /**/line

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




    public static void attach(ClientController s){
        System.out.println(s.toString() + " has been attached to Game");
        observers.add(s);
        //Assign clients as X and O

        if (getObservers() < 2){
            observers.get(0).setPlayer(1);
            TTTP.setClient1(s);
        } else {
            observers.get(1).setPlayer(2);
            TTTP.setClient1(s);
        }

        s.start();
    }




    public static List<ClientController> getAllObservers(){
        return observers;
    }

    public static int getObservers(){
        return observers.size();
    }




}