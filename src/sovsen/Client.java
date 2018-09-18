package sovsen;

import java.io.*;
import java.net.*;

/**
 * @Author SovsenGrp 12-Sep-18.
 */
public class Client {


    static Socket s1;
    static BufferedReader stdIn;

    public static void main(String[] args) {


        try {

            s1 = new Socket("localhost", 3001);


            String fromServer = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(s1.getInputStream()));

            stdIn = new BufferedReader(new InputStreamReader((System.in)));


            while ((fromServer = in.readLine()) != null) {

                System.out.println("Conn");
                String[] strArr = fromServer.split("~");


                System.out.println("Server: ");

                for (String c : strArr) {
                    System.out.println("Client-c " + c + "\t");
                }

                if (fromServer.equals("End")) {
                    System.out.println("Server is silent");
                    break;
                }


                String fromUser = stdIn.readLine();

                if (fromUser != null) {

                    write(fromUser);
                    System.out.println("Client: " + fromUser);
                    // out.println(fromUser);
                }

                System.out.println("Still in while loop");
            }


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }


        System.out.println("Client end");


        //Close the connection and exit
        // dis.close();
        //s1In.close();
        //s1.close();




        /*try {
            Socket s = new Socket("127.0.0.1",3001);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            String msg = dis.readUTF();

            System.out.println(msg);
        } catch (Exception e) {
        }
    }*/
    }

    public static void write(String fromUser){
        try {

            PrintWriter out;
            out = new PrintWriter(s1.getOutputStream(), true);
            out.println(fromUser);

        } catch (IOException e) {
            e.printStackTrace();



        }
    }

}
