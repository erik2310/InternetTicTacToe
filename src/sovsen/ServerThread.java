package sovsen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

    BufferedReader in = null;
    PrintWriter out = null;
    Socket client;
    String outputLine;
    String inputLine;

    public ServerThread(Socket client){
        this.client = client;
        try {
            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
            out = new PrintWriter(
                    client.getOutputStream(), true
            );

            out.println("YOU ARE CONNECTED");

        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getCause());
        }
    }

    public void run() {

        try {
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Client says: " + inputLine);


                writeToClient(outputLine);

                updateBoard();
            }
        } catch (IOException ex) {

            System.out.println("IOException in client " + this.getName() + ". " + ex.getCause());
        } catch (NullPointerException npe) {

            System.out.println("runClient " + this.getName() + " is closed");
            System.out.println(npe.getCause());


        }


    }


    public void updateBoard(){
        Game.processInput(inputLine);
    }

    public void writeToClient(String message){


        try {

            out.println(message);

            if (outputLine != null){
                String[] strArr = outputLine.split("~");
                System.out.println("Server says: ");

                for (String c : strArr) {
                    System.out.println(c + "\t");
                }
            }



            out.println(outputLine);

        } catch (NullPointerException npe) {

            System.out.println("Client " + this.getName() + " is closed2");
            npe.printStackTrace();

        }
    }



    public void update(String board){
        try {
            out = new PrintWriter(
                    client.getOutputStream(), true
            );
            System.out.println("ServerThread::update() " + this.getName() + " " + board);
            out.println("Board: " + board);

        } catch (IOException ex) {

            System.out.println("IOException in client " + this.getName() + ". " + ex.getCause());
        } catch (NullPointerException npe) {
            System.out.println(this.getName() + " has a problem. " + npe.getCause());
            npe.printStackTrace();
        }
    }



    public void closeConnection(){

        try {
            System.out.println("Connection closing");
            if (in != null){
                in.close();
            }

            if (out != null){
                out.close();
                System.out.println("Output socket stream closed");
            }

            if (client != null){
                client.close();
            }

        } catch (IOException ex){
            System.out.println("Socket close error: " + ex.getCause());
        }
    }

}
