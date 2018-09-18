package sovsen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

    Socket client;
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

      //System.out.println("ServerThread RUN");


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
