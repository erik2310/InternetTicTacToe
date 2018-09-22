package sovsen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController extends Thread{

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

    /**
     *
     */
    public void run(){
        //  System.out.println("The server is running");
        boolean running = true;
        boolean maxUsers = false;

        Game.initGame();

        while(running){
            System.out.print("Running-");
            while (maxUsers == false){
                maxUsers = listenForUsers();
                if(observers.getObservers().size() < 2){
                    writeAutomaticMessage("Waiting for another player...");
                } else {
                    playerX = observers.getObservers().get(0);
                    playerO = observers.getObservers().get(1);
                    maxUsers = true;
                }
            }

            running = TTTP();

        }
    }

    /**
     * Iterates through the iterator that determines who is allowed to speak.
     */
    public void sort(){
        System.out.println("\n----sorting----\n");

        ite[0] = ite[1];
        ite[1] = ite[2];
        ite[2] = ite[3];
        ite[3] = ite[0];
    }


    /**
     * Forces Server or Clients into listen mode, whenever another one speaks.
     * Call TTTP whenever a client or server has spoken.
     * @return
     */
    public boolean TTTP(){
        System.out.print("TTTP-");

        //A while loop that keeps ServerController alive until it receives an end command
        boolean running = true;
        while(running){
            System.out.println("\n\tIteration is - " + ite[0]);
            if (ite[0] == END){
                running = false;
            }

            //Server is speaking, Clients listen
            //Code moved to a helper-method for ease-of-read
            if(ite[0] == SERVER){
                System.out.println("\tServer is writing...\n");
                serverSpeak();
            } else if (ite[0] == CLIENT1){
                System.out.println("\nClient1 writes\n");
                clientSpeak(true);
            } else if (ite[0] == CLIENT2){
                System.out.println("\nClient2 writes\n");
                clientSpeak(false);
            }

            //Call 'sort' after each communication
            System.out.println("Call to Sort");
            sort();
        }

        //Continue the loop until an end-command is received
        return true;
    }

    /**
     *
     */
    private void serverSpeak(){
        System.out.println("ServerSpeak");

        synchronized (observers.getObserver(0)){
            try{

                observers.getObserver(0).setWAIT(false);
                observers.getObserver(0).notify();

            } catch (IllegalMonitorStateException e){
                e.printStackTrace();
            }
        }

        synchronized (observers.getObserver(1)){
            try{

                observers.getObserver(1).setWAIT(false);
                observers.getObserver(1).notify();

            } catch (IllegalMonitorStateException e){
                e.printStackTrace();
            }
        }

        notifyAllObservers();
        System.out.println("ServerSpeak end");
    }

    /**
     *
     * @param client
     */
    private void clientSpeak(boolean client){
        if (client == true){
            //Client2-thread is set to wait
            System.out.println("Client1 speak");
            synchronized(playerO){
                try {
                    if (playerO != null){
                        observers.getObserver(1).setWAIT(false);}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            synchronized (playerX){

                if (playerX != null){
                    observers.getObserver(1).setWAIT(true);
            }
                this.listen();}

        } else {
            synchronized (playerO){
                if (playerO != null){

                    observers.getObserver(0).setWAIT(true);}
            }
            synchronized (playerX){
                if (playerX != null){

                    observers.getObserver(1).setWAIT(false);}
            }
            System.out.println("Client2 speak");
            this.listen();



        }
    }

    /**
     * Used to set the Server to listen for clients. Exited once 2 clients have connected.
     * @return
     */
    public boolean listenForUsers(){
        /*System.out.println("Listening for users");*/
        System.out.print("listenForUsers-");
        try{
            //Server accepts client requests
            client = server.accept();

            //Create a new thread for the client
            ClientController s =  new ClientController(client);

            //Attach adds the thread to ObserverList
            attach(s);

            //Notify client they have connected
            writeAutomaticMessage("You have connected to TicTacToe server!");
            if (observers.size() >= 2){
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection error");
        }

    //Continue the loop until 2 clients have connected (in which case the boolean becomes true)
        return false;
    }


    /**
     *
     * @return
     */
    public boolean listen(){
         System.out.print("Server is listening-");
        String inputLine;

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

    /**
     *
     * @param message
     */
    public void writeAutomaticMessage(String message){
        try {

            toClient = new PrintWriter(client.getOutputStream(), true);


            if (message != null) {
               /* System.out.println("Server: " + message);*/
                toClient.println(message);
            }

        } catch (IOException IOE) {
            System.out.println("Automatic message failure!");
            IOE.printStackTrace();

        }
    }


    /**
     *
     * @param message
     */
    public void write(String message){

        //Creates a new output stream to the client socket
        String outputLine;
        outputLine = Game.processInput(message);

        if (outputLine != null) {
           /* System.out.println("Server: " + outputLine);*/
            //Uses the protocol to write to the client
            toClient.println(outputLine);
        }

    }


    /**
     *
     */
    public void notifyAllObservers(){
        System.out.print("NotifyAllObservers-");
        synchronized (observers){
            observers.notifyAll();
        }

        try {
            for (ClientController c : observers.getObservers()) {
                if (c.listening() == true){

                    // System.out.println("getallobservers");
                    toClient = new PrintWriter(c.getSocket().getOutputStream(), true);


                    //Change to the updating line
                    String outputLine = "heya";

                    if (outputLine != null) {
                        System.out.println("Server: " + outputLine);
                        toClient.println(outputLine);
                    }
                }
            }
        } catch (IOException IOE) {
            System.out.println("Automatic message failure!");
            IOE.printStackTrace();
        }


        System.out.print("EndOfNotify-");

    }

    /**
     *
     * @throws IOException
     */
    public void closeServer() throws IOException{
        System.out.print("closeServer-");

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


    /**
     * Attaches a ClientController to ObserverList
     * @param s
     */
    public void attach(ClientController s){
        System.out.print("Attach-");

        observers.addObserver(s);

        //Assign clients as X and O
        if(observers.getObservers().size() < 2) {
          observers.getObserver(0).setPlayer(1);

        } else {
            observers.getObserver(1).setPlayer(2);
        }

        s.start();
    }


    /**
     *
     */
    public void newInput(){
        try{
            toServer = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     *
     * @return
     */
    public PrintWriter newOutput(){
        try{
            toClient = new PrintWriter(client.getOutputStream());
            return toClient;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     */
    public void closeStream(){

        try{
            if (toClient != null){toClient.close();}

            if (toServer != null){toServer.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public int getObservers(){
        return observers.size();
    }

}
