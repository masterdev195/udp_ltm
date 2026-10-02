package TCP.bai1;
import java.io.*;
import java.net.Socket;

public class TCPClient {
      public static void main(String[] args) {
            String SERVER_ADDRESS = "36.50.135.242";
            int serverPort = 2208;
            String STUDENT_CODE = "B23DCCN728";
            String Q_CODE = "ofgoK2r7";

            Socket socket = null;
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                  socket = new Socket(SERVER_ADDRESS, serverPort);
                  reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                  writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                  String message = STUDENT_CODE + ";" + Q_CODE;
                  writer.write(message);
                  writer.newLine();
                  writer.flush();

                  //System.out.println("Sent to server: " + message);

                  String response = reader.readLine();
                  //System.out.println("Received from server: " + response);

                  String[] domains = response.split(",");
                  StringBuilder result = new StringBuilder();

                  for (String domain : domains) {
                        domain = domain.trim();
                        if (domain.endsWith(".edu")) {
                              if (result.length() > 0) {
                                    result.append(", ");
                              }
                              result.append(domain);
                        }
                  }
                  System.out.println("Filtered .edu domains: " + result);
                  writer.write(result.toString());
                  writer.newLine();
                  writer.flush();
                  //System.out.println("Đã gửi đến server");
            } catch (Exception e) {
                  //System.out.println("Co loi xay ra");
                  e.printStackTrace();
            } finally {
                  try {
                        if (reader != null)
                              reader.close();
                        if (writer != null)
                              writer.close();
                        if (socket != null)
                              socket.close();
                        //System.out.println("Đã đóng kết nối");
                  } catch (IOException e) {
                        e.printStackTrace();
                  }
            }
      }
}
