package TCP.b74_dataStream;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class TCPClient {
      public static void main(String[] args){
            String SERVER_ADDRESS = "36.50.135.242";
            int PORT = 2207;
            String STUDENT_CODE = "B23DCCN728";
            String Q_CODE = "Os6iub74";

            Socket socket = null;
            DataInputStream input = null;
            DataOutputStream output = null;

            try{
                  socket = new Socket(SERVER_ADDRESS, PORT);
                  input = new DataInputStream(socket.getInputStream());
                  output = new DataOutputStream(socket.getOutputStream());

                  String message = STUDENT_CODE + ";" + Q_CODE;
                  output.writeUTF(message);
                  output.flush();

                  int a = input.readInt();
                  int b = input.readInt();

                  int tong = a + b;
                  int tich = a * b;

                  System.out.println("Tổng: " + tong);
                  System.out.println("Tích: " + tich);

                  output.writeInt(tong);
                  output.writeInt(tich);
                  output.flush();

                  input.close();
                  output.close();
                  socket.close();

            } catch (IOException e) {
                  e.printStackTrace();
            }
      }
}
