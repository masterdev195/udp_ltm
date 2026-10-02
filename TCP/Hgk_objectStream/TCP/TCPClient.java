package TCP;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class TCPClient {
      public static int reverseNumber(int number) {

            int reversed = 0;

            while (number != 0) {
                  int digit = number % 10;

                  reversed = reversed * 10 + digit;

                  number /= 10;
            }

            return reversed;
      }
      public static void main(String[] args){
            String SERVER_ADDRESS = "36.50.135.242";
            int PORT = 2209;
            String STUDENT_CODE = "B23DCCN728";
            String Q_CODE = "3gt2PHgk";

            try{
                  Socket socket = new Socket(SERVER_ADDRESS, PORT);
                  socket.setSoTimeout(5000);
                  ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                  ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                  String message = STUDENT_CODE + ";" + Q_CODE;
                  output.writeObject(message);
                  output.flush();

                  Laptop laptop = (Laptop) input.readObject();
                  System.out.println(laptop);
                  String name = laptop.getName();
                  String[] words = name.trim().split("\\s+");
                  if(words.length >= 2){
                        String temp = words[0];
                        words[0] = words[words.length - 1];
                        words[words.length - 1] = temp;
                  }
                  String CrName = String.join(" ", words);
                  laptop.setName(CrName);
                  int quantity = laptop.getQuantity();
                  int CrQuantity = reverseNumber(quantity);
                  laptop.setQuantity(CrQuantity);
                  output.writeObject(laptop);
                  output.flush();

                  input.close();
                  output.close();
                  socket.close();
            } catch (IOException | ClassNotFoundException e) {
                  e.printStackTrace();
            }


      }
}
