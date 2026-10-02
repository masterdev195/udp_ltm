package TCP;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TCPClient {

    // Chuẩn hóa tên
    // nguyen van hai duong
    // -> DUONG, Nguyen Van Hai
    public static String formatName(String name) {
        name = name.trim().replaceAll("\\s+", " ");

        String[] words = name.split(" ");

        StringBuilder result = new StringBuilder();

        // Từ cuối viết IN HOA
        result.append(words[words.length - 1].toUpperCase());
        result.append(", ");

        // Các từ còn lại viết hoa chữ cái đầu
        for (int i = 0; i < words.length - 1; i++) {
            String word = words[i].toLowerCase();

            result.append(
                    Character.toUpperCase(word.charAt(0))
            );

            if (word.length() > 1) {
                result.append(word.substring(1));
            }

            if (i < words.length - 2) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    // 10-11-2012 -> 11/10/2012
    public static String formatDate(String date) {
        DateTimeFormatter input =
                DateTimeFormatter.ofPattern("MM-dd-uuuu");

        DateTimeFormatter output =
                DateTimeFormatter.ofPattern("dd/MM/uuuu");

        LocalDate d = LocalDate.parse(date, input);

        return d.format(output);
    }

    // nguyen van hai duong
    // -> nvhduong
    public static String createUsername(String name) {
        name = name.trim().replaceAll("\\s+", " ").toLowerCase();

        String[] words = name.split(" ");

        StringBuilder username = new StringBuilder();

        // Lấy chữ cái đầu của các từ trừ từ cuối
        for (int i = 0; i < words.length - 1; i++) {
            username.append(words[i].charAt(0));
        }

        // Ghép nguyên từ cuối
        username.append(words[words.length - 1]);

        return username.toString();
    }

    public static void main(String[] args) {
        String studentCode = "B23DCCN728";
        String qCode = "haKoHQgP";

        String serverAddress = "36.50.135.242";
        int port = 2209;

        try (
                Socket socket = new Socket(serverAddress, port);
                ObjectOutputStream output =
                        new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input =
                        new ObjectInputStream(socket.getInputStream())
        ) {

            // Server cho phép tối đa 5 giây
            socket.setSoTimeout(5000);

            // =============================
            // BƯỚC 1: Gửi mã SV + mã câu hỏi
            // =============================

            String request = studentCode + ";" + qCode;

            output.writeObject(request);
            output.flush();

            // =============================
            // BƯỚC 2: Nhận Customer
            // =============================

            Customer customer = (Customer) input.readObject();

            // Lưu tên ban đầu vì username phải tạo từ tên gốc
            String originalName = customer.getName();

            // =============================
            // BƯỚC 3: Chuẩn hóa
            // =============================

            String newName = formatName(originalName);
            String newDOB = formatDate(customer.getDayOfBirth());
            String newUsername = createUsername(originalName);

            customer.setName(newName);
            customer.setDayOfBirth(newDOB);
            customer.setUserName(newUsername);

            // =============================
            // Gửi Customer đã sửa
            // =============================

            output.writeObject(customer);
            output.flush();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}