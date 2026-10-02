package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPStringNormalizationClient {
    public static void main(String[] args) {
        // Cấu hình thông tin server và sinh viên
        String serverIp = "36.50.135.242";
        int serverPort = 2208;
        
        String studentCode = "B23DCCN728"; // Thay bằng mã sinh viên thực tế của bạn
        String qCode = "5AKGDFle";        // Mã câu hỏi từ yêu cầu
        
        DatagramSocket socket = null;
        try {
            // Khởi tạo UDP socket
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(serverIp);
            
            // a. Gửi thông điệp lần đầu theo định dạng: ;studentCode;qCode
            String messageA = ";" + studentCode + ";" + qCode;
            byte[] sendDataA = messageA.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packetA = new DatagramPacket(sendDataA, sendDataA.length, serverAddress, serverPort);
            
            socket.send(packetA);
            System.out.println("Đã gửi (a): " + messageA);
            
            // b. Nhận thông điệp từ server theo định dạng: requestId;data
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket packetB = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packetB);
            
            String responseB = new String(packetB.getData(), 0, packetB.getLength(), StandardCharsets.UTF_8).trim();
            System.out.println("Nhận từ server (b): " + responseB);
            
            // Phân tích thông điệp (cắt tại dấu chấm phẩy đầu tiên)
            int firstSemicolon = responseB.indexOf(';');
            String requestId = responseB.substring(0, firstSemicolon);
            String data = responseB.substring(firstSemicolon + 1);
            
            // c. Chuẩn hóa chuỗi: Ký tự đầu tiên của từng từ in hoa, các ký tự còn lại in thường
            String normalizedData = normalizeString(data);
            
            // Tạo thông điệp gửi lên server theo định dạng: requestId;data
            String messageC = requestId + ";" + normalizedData;
            byte[] sendDataC = messageC.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packetC = new DatagramPacket(sendDataC, sendDataC.length, serverAddress, serverPort);
            
            socket.send(packetC);
            System.out.println("Đã gửi phản hồi (c): " + messageC);
            
            // Nhận kết quả phản hồi cuối cùng từ server (nếu có)
            socket.setSoTimeout(3000);
            try {
                DatagramPacket packetFinal = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packetFinal);
                String finalResponse = new String(packetFinal.getData(), 0, packetFinal.getLength(), StandardCharsets.UTF_8).trim();
                System.out.println("Phản hồi từ server: " + finalResponse);
            } catch (Exception e) {
                System.out.println("Không nhận thêm phản hồi từ server.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // d. Đóng socket và kết thúc chương trình
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Đã đóng socket và kết thúc chương trình.");
            }
        }
    }
    
    // Hàm phụ trợ thực hiện chuẩn hóa chuỗi (Viết hoa chữ cái đầu mỗi từ, còn lại viết thường)
    private static String normalizeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String[] words = input.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                // Viết hoa chữ cái đầu, các chữ cái sau viết thường
                String capitalized = Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
                sb.append(capitalized);
            }
        }
        
        return sb.toString();
    }
}