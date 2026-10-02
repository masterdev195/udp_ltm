package TCP.Gzip;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TCPGZIPStream {

    public static void main(String[] args) {

        String serverIp = "36.50.135.242";
        int port = 2210;

        String studentCode = "B23DCCN728";
        String qCode = "PIAJQdTB";

        try (Socket socket = new Socket(serverIp, port)) {

            socket.setSoTimeout(5000);

            // QUAN TRỌNG:
            // Chỉ tạo MỘT GZIPOutputStream cho toàn bộ Client -> Server
            GZIPOutputStream gzipOut =
                    new GZIPOutputStream(
                            socket.getOutputStream(),
                            true
                    );

            // ==========================================
            // 1. Gửi studentCode;qCode
            // ==========================================

            String request = studentCode + ";" + qCode;

            gzipOut.write(
                    (request + "\n")
                            .getBytes(StandardCharsets.UTF_8)
            );

            // syncFlush = true nên Server có thể giải nén
            // request ngay mà chưa cần finish()
            gzipOut.flush();

            System.out.println("Request: " + request);

            // ==========================================
            // 2. Nhận response
            // ==========================================

            GZIPInputStream gzipIn =
                    new GZIPInputStream(socket.getInputStream());

            String response = readLine(gzipIn);

            System.out.println("Input: " + response);

            // ==========================================
            // 3. Reverse
            // ==========================================

            String reversed =
                    new StringBuilder(response)
                            .reverse()
                            .toString();

            System.out.println("Reversed: " + reversed);

            // ==========================================
            // 4. Base64
            // ==========================================

            String base64 =
                    Base64.getEncoder()
                            .encodeToString(
                                    reversed.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            );

            System.out.println("Base64: " + base64);

            // ==========================================
            // 5. Tạo kết quả
            // ==========================================

            String result =
                    reversed + "|" + base64;

            System.out.println("Submitted: " + result);

            // ==========================================
            // 6. Gửi kết quả
            // ==========================================

            // Dùng CHÍNH gzipOut ban đầu
            gzipOut.write(
                    (result + "\n")
                            .getBytes(StandardCharsets.UTF_8)
            );

            gzipOut.flush();

            // Đây là message cuối cùng:
            // hoàn tất GZIP stream để server đọc được EOF/trailer
            gzipOut.finish();
            gzipOut.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readLine(GZIPInputStream gzipIn)
            throws Exception {

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        int b;

        while ((b = gzipIn.read()) != -1) {

            if (b == '\n') {
                break;
            }

            if (b != '\r') {
                buffer.write(b);
            }
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }
}