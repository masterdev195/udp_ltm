package TCP.TCP_CHARACTER;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class TCPClient {
      public static void main(String[] args){
            String SERVER_ADDRESS = "36.50.135.242";
            int PORT = 2206;
            String STUDENT_CODE = "B23DCCN728";
            String Q_CODE = "b2X3t6to";

            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try{
                  socket = new Socket(SERVER_ADDRESS, PORT);
                  inputStream = socket.getInputStream();
                  outputStream = socket.getOutputStream();

                  String message = STUDENT_CODE + ";" + Q_CODE;
                  outputStream.write(message.getBytes());
                  outputStream.flush();

                  byte[] buffer = new byte[1024];
                  int bytesRead = inputStream.read(buffer);
                  String response = new String(buffer, 0, bytesRead).trim();

                  String[] parts = response.split(",");
                  int[] numbers = new int[parts.length];

                  for (int i = 0; i < parts.length; i++) {
                        numbers[i] = Integer.parseInt(parts[i]);
                  }

                  Arrays.sort(numbers);
                  int maxDistance = Integer.MAX_VALUE;
                  int firstNumber = 0;
                  int secondNumber = 0;

                  for(int i =0; i < numbers.length-1; i++){
                        int distance = numbers[i+1] - numbers[i];
                        if(distance < maxDistance){
                              maxDistance = distance;
                              firstNumber = numbers[i];
                              secondNumber = numbers[i+1];
                        } else if(distance == maxDistance && numbers[i+1] < secondNumber){
                              firstNumber = numbers[i];
                              secondNumber = numbers[i+1];
                        }
                  }

                  String result = maxDistance + "," + firstNumber + "," + secondNumber;
                  System.out.println(result);
                  
                  outputStream.write(result.getBytes());
                  outputStream.flush();
                  

            } catch (IOException e) {
                  e.printStackTrace();
            } finally {
                  try {
                        if (inputStream != null) inputStream.close();
                        if (outputStream != null) outputStream.close();
                        if (socket != null) socket.close();
                  } catch (IOException e) {
                        e.printStackTrace();
                  }
            }
      }
}
