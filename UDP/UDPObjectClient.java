package UDP;

import UDP.Student;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPObjectClient {
    public static void main(String[] args) {
        // Cấu hình thông tin server và sinh viên
        String serverIp = "36.50.135.242";
        int serverPort = 2209;
        
        String studentCode = "B23DCCN728"; // Thay bằng mã sinh viên thực tế của bạn
        String qCode = "Qe3TwHW5";        // Mã câu hỏi từ yêu cầu
        
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
            
            // b. Nhận thông điệp từ server: 8 byte đầu là requestId, các byte sau chứa đối tượng Student
            byte[] receiveBuffer = new byte[65535];
            DatagramPacket packetB = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packetB);
            
            byte[] receivedData = packetB.getData();
            int receivedLength = packetB.getLength();
            
            // Trích xuất 8 byte đầu cho requestId
            String requestId = new String(receivedData, 0, 8, StandardCharsets.UTF_8).trim();
            System.out.println("Nhận requestId: " + requestId);
            
            // Trích xuất các byte còn lại để giải tuần tự hóa đối tượng Student
            byte[] objectBytes = new byte[receivedLength - 8];
            System.arraycopy(receivedData, 8, objectBytes, 0, receivedLength - 8);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(objectBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Student student = (Student) ois.readObject();
            System.out.println("Nhận đối tượng Student từ server: " + student);
            
            // c. Chuẩn hóa tên và tạo email theo yêu cầu
            String normalizedName = normalizeName(student.getName());
            student.setName(normalizedName);
            
            String generatedEmail = generateEmail(normalizedName);
            student.setEmail(generatedEmail);
            
            System.out.println("Sau khi xử lý - Tên: " + student.getName() + " | Email: " + student.getEmail());
            
            // Tuần tự hóa lại đối tượng Student đã sửa đổi
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(student);
            oos.flush();
            byte[] modifiedObjectBytes = baos.toByteArray();
            
            // Đóng gói thông điệp gửi lên server: 8 byte đầu là requestId + các byte sau là đối tượng Student
            byte[] sendBuffer = new byte[8 + modifiedObjectBytes.length];
            byte[] reqIdBytes = requestId.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(reqIdBytes, 0, sendBuffer, 0, Math.min(reqIdBytes.length, 8));
            System.arraycopy(modifiedObjectBytes, 0, sendBuffer, 8, modifiedObjectBytes.length);
            
            DatagramPacket packetC = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
            socket.send(packetC);
            System.out.println("Đã gửi phản hồi đối tượng lên server (c).");
            
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
    
    // Hàm chuẩn hóa tên: Chữ cái đầu mỗi từ in hoa, còn lại in thường
    private static String normalizeName(String name) {
        if (name == null || name.isEmpty()) return name;
        String[] words = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
    
    // Hàm tạo email theo quy tắc: Tên (từ cuối cùng) + các chữ cái đầu của họ và đệm + @ptit.edu.vn
    private static String generateEmail(String normalizedName) {
        String[] words = normalizedName.toLowerCase().split("\\s+");
        if (words.length == 0) return "@ptit.edu.vn";
        
        String firstName = words[words.length - 1]; // Tên ở cuối
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            initials.append(words[i].charAt(0));
        }
        return firstName + initials.toString() + "@ptit.edu.vn";
    }
}