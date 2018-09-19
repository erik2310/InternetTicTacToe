package sovsen;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class TTTP {

    //When Server talks, Clients both listen
    //When Client1 talks, Server listens and Client2 waits
    //When Client2 talks, Server listens and Client1 waits

    static BufferedInputStream input;
    static PrintWriter output;

    static ServerThread client1;
    static ServerThread client2;

     static int[] ite = new int[4];

    static int SERVER = 0;
    static int CLIENT1 = 1;
    static int CLIENT2 = 2;
    static int END = 3;

    public TTTP(){
        ite[0] = 0;
        ite[1] = 1;
        ite[2] = 0;
        ite[3] = 2;
    }


    private static void sort(){
        ite[0] = ite[1];
        ite[1] = ite[2];
        ite[2] = ite[3];
        ite[3] = ite[0];
    }

    public static void order(){
        boolean running = true;

        while(running){
            if (ite[0] == END){
                running = false;
            }

            if(ite[0] == SERVER){
               client1.listen();
               client2.listen();
            } else if (ite[0] == CLIENT1){
                Server.listen();
                client2.listen();
            } else if (ite[0] == CLIENT2){
                Server.listen();
                client1.listen();
            }

            sort();
        }
    }

    public static void write(String message, Socket s, int caller){

        //If the caller isn't the one allowed to write, send an error message
        if (caller == ite[0]){
            try {
                //Call Order here, to make sure the right items are listening
                order();
                output = new PrintWriter(s.getOutputStream(), true);
                output.println(message);
            } catch (IOException IOE) {
                IOE.printStackTrace();
            }
        } else {
            //Send an error message to whoever tried to write
        }




    }


    public static void setClient1(Client c1){
        client1 = c1;
    }
    public static void setClient2(Client c2){
        client2 = c2;
    }
}
