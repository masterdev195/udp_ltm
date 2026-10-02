package UDP;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPMetricMinMaxClient {
    public static void main(String[] args) {
        // Cấu hình thông tin server và sinh viên
        String serverIp = "36.50.135.242";
        int serverPort = 2207;
        
        String studentCode = "B23DCCN728"; // Thay bằng mã sinh viên thực tế của bạn
        String qCode = "YrihHhO8";        // Mã câu hỏi từ yêu cầu
        
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
            
            // b. Nhận thông điệp từ server theo định dạng: requestId;a1,a2,...,a50
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket packetB = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packetB);
            
            String responseB = new String(packetB.getData(), 0, packetB.getLength(), StandardCharsets.UTF_8).trim();
            System.out.println("Nhận từ server (b): " + responseB);
            
            // Phân tích thông điệp (cắt tại dấu chấm phẩy đầu tiên để lấy requestId và chuỗi số)
            int firstSemicolon = responseB.indexOf(';');
            String requestId = responseB.substring(0, firstSemicolon);
            String numbersPart = responseB.substring(firstSemicolon + 1);
            
            // c. Tìm giá trị lớn nhất (max) và nhỏ nhất (min) trong danh sách số
            String[] parts = numbersPart.split(",");
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    int val = Integer.parseInt(part.trim());
                    if (val > max) {
                        max = val;
                    }
                    if (val < min) {
                        min = val;
                    }
                }
            }
            
            // Tạo thông điệp gửi lên server theo định dạng: requestId;max,min
            String messageC = requestId + ";" + max + "," + min;
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