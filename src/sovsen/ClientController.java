package sovsen;

import java.io.*;
import java.net.Socket;

public class ClientController extends Thread {



    static Socket socket;
    static BufferedReader stdIn;
    static BufferedReader toClient = null;
    static PrintWriter toServer = null;
    static int player;



    public ClientController(Socket s){
        socket = s;
    }


    public void run() {

        boolean running = true;
        while (running) {
            running = listen();
            if (Thread.interrupted()){
                return;
            }
        }
    }


    public static void printBoard(){

    }



    public void write(){
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
        try{
            toClient.close();
            toServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean listen() {
        try {

            String input = "";

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

                    //WRITE
                    System.out.println("Give your input");
                }
            }

        } catch (IOException IOE) {


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
