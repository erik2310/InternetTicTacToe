package sovsen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {


    private ObserverList observers = new ObserverList();
    private ServerSocket server;
    private Socket client;
    private BufferedReader toServer;
    private PrintWriter toClient;
    private ClientController playerX;
    private ClientController playerO;

    private int[] ite = new int[4];

    private int SERVER = 0;
    private int CLIENT1 = 1;
    private int CLIENT2 = 2;
    private int END = 3;



    public ServerController(ServerSocket server){
        this.server = server;
        ite[0] = 0;
        ite[1] = 1;
        ite[2] = 0;
        ite[3] = 2;

        observers.resetObservers();

        run();

    }


    public void run(){
        //  System.out.println("The server is running");
        boolean running = true;
        boolean maxUsers = false;

        Game.initGame();

        while(running){

            while (maxUsers == false){
                maxUsers = listenForUsers();
                if(observers.getObservers().size() < 2){
                    writeAutomaticMessage("Waiting for another player...");
                } else {

                    System.out.println("SOCKET" + observers.getObservers().get(0).getSocket().getPort());
                    playerX = observers.getObservers().get(0);
                    playerO = observers.getObservers().get(1);
                    maxUsers = true;
                }
            }

            running = TTTP();

        }
    }


    public void sort(){
        System.out.println("\n----sorting----\n");
        ite[0] = ite[1];
        ite[1] = ite[2];
        ite[2] = ite[3];
        ite[3] = ite[0];
    }


    public boolean TTTP(){
        System.out.println("TTTP running");
        boolean running = true;

        while(running){
            if (ite[0] == END){
                running = false;
            }

            if(ite[0] == SERVER){

                System.out.println("\nServer is writing...\n");

                notifyAllObservers();
                if (playerX != null){
                    playerX.listen();}

                if (playerO != null){
                    playerO.listen();}

                sort();
            } else if (ite[0] == CLIENT1){
                System.out.println("\nClient1 writes\n");

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
                System.out.println("\nClient2 writes\n");
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


    public void newInput(){
        try{
            toServer = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public PrintWriter newOutput(){
        try{
            toClient = new PrintWriter(client.getOutputStream());
            return toClient;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeStream(){

        try{
            if (toClient != null){toClient.close();}

            if (toServer != null){toServer.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setServerSocket(ServerSocket s){
        server = s;
    }

    public boolean listenForUsers(){
        System.out.println("Listening for users");


        try{

            client = server.accept();


            System.out.println("Client " + client.getPort() + " has joined!\n");
            ClientController s =  new ClientController(client);


            attach(s);

            //Notify client they have connected
            writeAutomaticMessage("You have connected to TicTacToe server!");

            System.out.println("Number of users: " + getObservers());
            if (observers.size() >= 2){
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection error");
        }


        return false;
    }


    public boolean listen(){
        // System.out.println("Server is listening");
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


    public void writeAutomaticMessage(String message){
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


    public void write(String message){

        //Creates a new output stream to the client socket
        String outputLine;
        outputLine = Game.processInput(message);

        if (outputLine != null) {
            System.out.println("Server: " + outputLine);
            //Uses the protocol to write to the client
            toClient.println(outputLine);
        }

    }


    public void notifyAllObservers(){
        System.out.println("NOTIFYALLOBSERVERS");
        synchronized (observers){
            observers.notifyAll();
        }

        try {
            for (ClientController c : observers.getObservers()) {
                // System.out.println("getallobservers");

                System.out.println(c.getSocket().getPort());
                toClient = new PrintWriter(c.getSocket().getOutputStream(), true);
                System.out.println("\n" + c.getSocket());
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


        for (ClientController o: observers.getObservers()) {
            System.out.println(o.getSocket().getPort());
        }


    }


    public void closeServer() throws IOException{
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




    public void attach(ClientController s){
        System.out.println("\nAttach1--socket: " + s.getSocket().getPort());



        observers.addObserver(s);


        if(observers.getObservers().size() < 2) {
            System.out.println("Observer 0 is set");

            observers.getObserver(0).setPlayer(1);

        } else {
            System.out.println("Observer 1 is set");

            observers.getObserver(1).setPlayer(2);

        }
        //Assign clients as X and O
        try {
            System.out.println("OBSERVER TEST: " + observers.getObserver(0).getSocket().getPort() + "\t" + observers.getObserver(1).getSocket().getPort());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("out of bounds");
        } catch (NullPointerException e) {
            System.out.println("Null");
        }


        System.out.println();
        s.start();
    }


    public int getObservers(){
        return observers.size();
    }

    public void setClient1(ClientController c1){
        playerX = c1;
    }
    public void setClient2(ClientController c2){
        playerO = c2;
    }

}
