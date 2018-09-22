package sovsen;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientController extends Thread {

    private Socket socket;
    private BufferedReader stdIn;
    private BufferedReader toClient = null;
    private PrintWriter toServer = null;
    private int player;



    public ClientController(Socket s){
        System.out.println("Client socket: " + s.toString());
        socket = s;
    }


    public void run() {
        System.out.println("Client socket: " + socket.toString());
        boolean running = true;
        while (running) {
            running = listen();
            listen();
            if (Thread.interrupted()){
                return;
            }
        }

        this.closeThread();
    }


    public void printBoard(){

    }



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

                Game.initGame();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void openInput(){
        try{
            toClient = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void openOutput(){

        try{
        toServer = new PrintWriter(socket.getOutputStream());
    } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void closeStream(){
        System.out.println("Close Stream");
        try{
            if (toClient != null){toClient.close();}

            if (toServer != null){toServer.close();}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean listen() {
        System.out.println("Client socket: " + socket.getPort());
        try {

            String input = "";

            openInput();

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

        return true;
    }


    public int getPlayer(){
        return player;
    }

    public void setPlayer(int p){
        player = p;
    }


    public void closeThread(){
        this.closeThread();
    }



    public Socket getSocket(){
        return socket;
    }



}
