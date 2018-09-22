package sovsen;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientController extends Thread {


    //Socket holds the socket from the client
    private Socket socket;
    //Readers and writers
    private BufferedReader stdIn;
    private BufferedReader toClient = null;
    private PrintWriter toServer = null;
    //And integer to tell whether the player is X (1) or O (2)
    private int player;
    private boolean WAIT = true;
    private boolean WRITE = false;
    private boolean LISTENING = false;

    /**
     * ClientController runs all logic for Client-side communication.
     * Client extends Thread, making it able to run concurrently with the Server and other Threads.
     * @param s
     */
    public ClientController(Socket s){
        socket = s;
    }

    /**
     *Forces the Thread to stay alive until interrupted or ended
     */
    public void run() {

        boolean running = true;
        while (running) {
        synchronized (this){
            System.out.println("WRITE IS: " + WAIT);
            if (WAIT == true) {
                if (Thread.interrupted()) {
                    this.closeThread();
                }
                try {
                    System.out.print("wait");
                    this.wait();
                    System.out.println("After wait?");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
                if (WRITE == false){
                    System.out.println("CLIENT " + this.getName() + " IS LISTENING");

                    LISTENING = true;
                    running = listen();
                } System.out.println("CLIENT " + this.getName() + " IS WRITING");

            write();
                }

            System.out.println("TEST");

        }

        System.out.println("TEST2");
    }

    /**
     * Used to print integers, representing the board setup, to Client.
     */
    public int[] printBoard(){
        int[] temp = new int[9];


        return null;
    }


    /**
     *Write to Server using
     */
    public void write(){
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

    /**
     *
     */
    public void openInput(){
        try{
            toClient = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     *
     */
    public void openOutput(){

        try{
        toServer = new PrintWriter(socket.getOutputStream());
    } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     *
     */
    public void closeStream(){
        System.out.println("Close Stream");
        try{
            if (toClient != null){toClient.close();}

            if (toServer != null){toServer.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public boolean listen() {
        Client.write("Client::Listen-");

        String input;

        openInput();


        try {

            if (toClient != null){
                while ((input = toClient.readLine()) != null) {
                    System.out.println("Server: " + input);
                    //If server output is 'end', stop the Running-loop
                    if (input == "end") {
                        System.out.println("Server is offline");
                        return false;
                    }

                    if(socket == null){
                        System.out.println("No servers available");
                        closeThread();
                    }
                }
            }

        } catch (IOException IOE) {

            System.out.println("Error: " + IOE.getCause());
            IOE.printStackTrace();
        }

        System.out.println("End of client listen");

        return true;
    }

    /**
     *
     * @return
     */
    public int getPlayer(){
        return player;
    }


    public boolean listening(){
        return LISTENING;
    }

    public void setPlayer(int p){
        player = p;
    }


    public void closeThread(){
        this.closeThread();
    }



    public void setWAIT(boolean wait){
        this.WAIT = wait;
    }

    public void setWRITE(boolean write){
        this.WRITE = write;
    }


    public Socket getSocket(){
        return socket;
    }



}
