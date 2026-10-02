package TCP.AoM;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class TCPClient {
      public static void main(String[] args){
            String ADDRESS_SERVER = "36.50.135.242";
            int PORT = 2208;
            String STUDENT_CODE = "B23DCCN728";
            String Q_CODE = "kxOgTAoM";

            Socket socket = null;
            BufferedReader reader = null;
            BufferedWriter writer = null;

            try{
                  socket = new Socket(ADDRESS_SERVER, PORT);
                  socket.setSoTimeout(5000);
                  reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                  writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                  String message = STUDENT_CODE + ";" + Q_CODE;
                  writer.write(message);
                  writer.newLine();
                  writer.flush();

                  String response = reader.readLine();

                  Map<Character, Integer> countMap = new LinkedHashMap<>();

                  for(char c : response.toCharArray()){
                        countMap.put(c, countMap.getOrDefault(c, 0) + 1);
                  }

                  StringBuilder result = new StringBuilder();
                  for(Map.Entry<Character, Integer> entry : countMap.entrySet()){
                        if(entry.getValue() > 1){
                              // if(result.length() > 0){
                              //       result.append(",");
                              // }
                              result.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
                        }
                  }

                  System.out.println("Result: " + result.toString());
                  writer.write(result.toString());
                  writer.newLine();
                  writer.flush();

                  reader.close();
                  writer.close();
                  socket.close();


            } catch (Exception e) {
                  e.printStackTrace();
            }
      }
}
