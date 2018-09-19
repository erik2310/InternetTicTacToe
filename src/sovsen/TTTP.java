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

    static Client client1;
    static Client client2;

    static int pre;
    static int next;

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

    public static void order(){
        boolean running = true;
        int i = 0;
        while(running){
            if (ite[0] == END){
                running = false;
            }
            if (i == 3){
                i = 0;
            }

            if(ite[0] == SERVER){
                if(ite[1] == CLIENT1) {

                } else {

                }
            }



            ite[0] = ite[1];
            ite[1] = ite[2];
            ite[2] = ite[3];
            ite[3] = ite[0];
            i++;
        }



    }


    public static void awaitServerInput(){

    }

    public static void awaitClientInput(){
        Server.listen();
    }

    public static void write(String message, Socket s){

        try {
            output = new PrintWriter(s.getOutputStream(), true);


            if ()

            output.println(message);
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }

    }


    public static void setServer(Server s){
        server = s;
    }

    public static void setClient1(Client c1){
        client1 = c1;
    }
    public static void setClient2(Client c2){
        client2 = c2;
    }
}
