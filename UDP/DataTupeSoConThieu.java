package UDP;
import java.net.*;
import java.nio.charset.*;
import java.util.*;


public class DataTupeSoConThieu {
      public static void main(String[] args) {
        // Cấu hình địa chỉ server và thông tin cá nhân
        String serverIp = "36.50.135.242"; // Thay đổi thành IP server nếu chạy máy khác
        int serverPort = 2207;
        
        String studentCode = "B23DCCN728"; // Thay bằng mã sinh viên của bạn
        String qCode = "NuprVxSx";       // Thay bằng mã câu hỏi của bạn
        
        DatagramSocket socket = null;
        try {
            // Khởi tạo UDP socket
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(serverIp);
            
            // a. Gửi thông điệp lần đầu theo định dạng ;studentCode;qCode
            String messageA = ";" + studentCode + ";" + qCode;
            byte[] sendDataA = messageA.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packetA = new DatagramPacket(sendDataA, sendDataA.length, serverAddress, serverPort);
            
            socket.send(packetA);
            System.out.println("Đã gửi (a): " + messageA);
            
            // b. Nhận thông điệp từ server: requestId;n;A1,A2,...Am
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket packetB = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packetB);
            
            String responseB = new String(packetB.getData(), 0, packetB.getLength(), StandardCharsets.UTF_8).trim();
            System.out.println("Nhận từ server (b): " + responseB);
            
            // Phân tích thông điệp nhận được
            String[] parts = responseB.split(";", -1); // Dùng -1 để giữ lại phần tử trống nếu có
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1].trim());
            
            Set<Integer> receivedSet = new HashSet<>();
            if (parts.length > 2 && !parts[2].trim().isEmpty()) {
                String[] vals = parts[2].split(",");
                for (String val : vals) {
                    if (!val.trim().isEmpty()) {
                        receivedSet.add(Integer.parseInt(val.trim()));
                    }
                }
            }
            
            // c. Tìm kiếm các giá trị còn thiếu từ 1 đến n
            List<Integer> missingVals = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                if (!receivedSet.contains(i)) {
                    missingVals.add(i);
                }
            }
            
            // Tạo chuỗi các giá trị thiếu cách nhau bằng dấu phẩy
            StringBuilder missingStr = new StringBuilder();
            for (int i = 0; i < missingVals.size(); i++) {
                missingStr.append(missingVals.get(i));
                if (i < missingVals.size() - 1) {
                    missingStr.append(",");
                }
            }
            
            // Gửi thông điệp phản hồi lên server: requestId;B1,B2,...,Bm
            String messageC = requestId + ";" + missingStr.toString();
            byte[] sendDataC = messageC.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packetC = new DatagramPacket(sendDataC, sendDataC.length, serverAddress, serverPort);
            
            socket.send(packetC);
            System.out.println("Đã gửi phản hồi (c): " + messageC);
            
            // (Tùy chọn) Nhận kết quả xác nhận từ server
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
