package sovsen;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {

    Socket client;
    DataOutputStream out;
    int player;



    public ServerThread(Socket client){
        System.out.println("ServerThread is: " + client);
        this.client = client;
    }




    public void run() {
        boolean running = true;
        while(running){
            running = doWork();
            if (Thread.interrupted()){
                return;
            }
        }
    }

    public boolean doWork(){

        ()


        return true;
    }



    public void setPlayer(int player){
        this.player = player;
    }

    public int getPlayer(){
        return player;
    }

    public void closeThread(){
            this.closeThread();
    }



    public Socket getSocket(){
        return client;
    }


}
