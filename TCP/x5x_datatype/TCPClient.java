package TCP.x5x_datatype;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class TCPClient {
      public static void main(String[] args){
            String SERVER_ADDRESS = "36.50.135.242";
            int port = 2207;
            String STUDENT_CODE = "B23DCCN728";
            String Q_CODE = "6cshXvaR";

            Socket socket  = null;
            DataInputStream input = null;
            DataOutputStream output = null;

            try{
                  socket = new Socket(SERVER_ADDRESS, port);
                  input = new DataInputStream(socket.getInputStream());
                  output = new DataOutputStream(socket.getOutputStream());

                  String message = STUDENT_CODE + ";" + Q_CODE;
                  output.writeUTF(message);
                  output.flush();

                  String response = input.readUTF();
                  int s = input.readInt();
                  System.out.println("Server gửi: " + response);
                  System.out.println("Giá trị s: " + s);
                  StringBuilder result = new StringBuilder();

                  for( char c : response.toCharArray()){
                        if(c >= 'A' && c <= 'Z'){
                              char newChar = (char)('A' + ((c - 'A') - s +26) % 26);
                              result.append(newChar);
                        }else if (c >= 'a' && c <= 'z'){
                              char newChar = (char)('a' + ((c - 'a') - s + 26) % 26);
                              result.append(newChar);
                        }else{
                              result.append(c);
                        }
                  }

                  output.writeUTF(result.toString());
                  output.flush();

                  input.close();
                  output.close();
                  socket.close();

            } catch (IOException e) {
                  e.printStackTrace();

            }

      }
}
