package TCP.JcP;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class TCPClient {
      public static void main(String[] args) {
            String SERVER_ADDRESS = "36.50.135.242";
            int PORT = 2206;

            String STUDENT_CODE = "B23DCCN728";
            String Q_CODE = "SF52RJcP";
            
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;

            try{
                  socket = new Socket(SERVER_ADDRESS, PORT);
                  input = socket.getInputStream();
                  output = socket.getOutputStream();

                  String message = STUDENT_CODE + ";" + Q_CODE;
                  output.write(message.getBytes());
                  output.flush();

                  byte[] buffer = new byte[1024];
                  int bytesRead = input.read(buffer);
                  String response = new String(buffer, 0, bytesRead).trim();
                  System.out.println("Server gửi: " + response);

                  String[] parts = response.split(",");
                  int[] numbers = new int[parts.length];

                  for (int i = 0; i < parts.length; i++) {
                        numbers[i] = Integer.parseInt(parts[i]);
                  }

                 // Arrays.sort(numbers);
                  int maxValue = Integer.MIN_VALUE;
                  int secondMax = Integer.MIN_VALUE;

                  for(int number : numbers){
                        if(number > maxValue){
                              secondMax = maxValue;
                              maxValue = number;
                        } else if(number > secondMax && number < maxValue){
                              secondMax = number;
                        }
                        
                  }
                  int pos = -1;
                  for(int i = 0; i < numbers.length; i++){
                        if(numbers[i] == secondMax){
                              pos = i;
                              break;
                        }
                  }

                  String result = secondMax + "," + pos;
                  System.out.println(result);
                  
                  output.write(result.getBytes());
                  output.flush();

            } catch (Exception e) {
                  e.printStackTrace();
            } finally {
                  try {
                        if (input != null)
                              input.close();
                        if (output != null)
                              output.close();
                        if (socket != null)
                              socket.close();
                  } catch (IOException e) {
                        e.printStackTrace();
                  }
            }
      }
}

