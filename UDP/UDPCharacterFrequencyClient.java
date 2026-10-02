package UDP;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UDPCharacterFrequencyClient {
    public static void main(String[] args) {
        // Cấu hình thông tin server và sinh viên
        String serverIp = "36.50.135.242";
        int serverPort = 2208;
        
        String studentCode = "B23DCCN728"; // Thay bằng mã sinh viên thực tế của bạn
        String qCode = "6hjdwtiY";        // Mã câu hỏi từ yêu cầu
        
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
            
            // Phân tích thông điệp nhận được (cắt tại dấu chấm phẩy đầu tiên)
            int firstSemicolon = responseB.indexOf(';');
            String requestId = responseB.substring(0, firstSemicolon);
            String data = responseB.substring(firstSemicolon + 1);
            
            // c. Tìm kiếm ký tự xuất hiện nhiều nhất và các vị trí xuất hiện (vị trí tính từ 1)
            Map<Character, Integer> countMap = new LinkedHashMap<>();
            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                countMap.put(c, countMap.getOrDefault(c, 0) + 1);
            }
            
            char maxChar = ' ';
            int maxCount = 0;
            for (Map.Entry<Character, Integer> entry : countMap.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    maxChar = entry.getKey();
                }
            }
            
            // Lấy danh sách các vị trí xuất hiện của ký tự đó (1-based index)
            List<Integer> positions = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == maxChar) {
                    positions.add(i + 1);
                }
            }
            
            // Xây dựng chuỗi vị trí cách nhau bằng dấu phẩy (có kèm dấu phẩy cuối theo ví dụ)
            StringBuilder posStr = new StringBuilder();
            for (Integer pos : positions) {
                posStr.append(pos).append(",");
            }
            
            // Tạo thông điệp gửi lên server theo định dạng: requestId;ký tự:các vị trí
            String messageC = requestId + ";" + maxChar + ":" + posStr.toString();
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
}