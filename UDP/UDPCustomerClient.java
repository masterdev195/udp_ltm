package UDP;

import UDP.Customer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPCustomerClient {
    public static void main(String[] args) {
        String serverIp = "36.50.135.242";
        int serverPort = 2209;
        
        String studentCode = "B23DCCN728";
        String qCode = "B3mYSkr6";
        
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
            
            // b. Nhận thông điệp từ server: 8 byte đầu là requestId, các byte sau là đối tượng Customer
            byte[] receiveBuffer = new byte[65535];
            DatagramPacket packetB = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packetB);
            
            byte[] receivedData = packetB.getData();
            int receivedLength = packetB.getLength();
            
            // Trích xuất 8 byte đầu cho requestId
            String requestId = new String(receivedData, 0, 8, StandardCharsets.UTF_8).trim();
            System.out.println("Nhận requestId: " + requestId);
            
            // Trích xuất các byte còn lại để giải tuần tự hóa đối tượng Customer
            byte[] objectBytes = new byte[receivedLength - 8];
            System.arraycopy(receivedData, 8, objectBytes, 0, receivedLength - 8);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(objectBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Customer customer = (Customer) ois.readObject();
            System.out.println("Nhận đối tượng Customer từ server: " + customer);
            
            // c. Xử lý yêu cầu thay đổi thông tin:
            String originalName = customer.getName();
            
            // 1. Chuẩn hóa tên: nguyen van hai duong -> DUONG, Nguyen Van Hai
            String formattedName = formatName(originalName);
            customer.setName(formattedName);
            
            // 2. Chuyển ngày sinh từ mm-dd-yyyy sang dd/mm/yyyy (ví dụ: 10-11-2012 -> 11/10/2012)
            String formattedDob = formatDob(customer.getDayOfBirth());
            customer.setDayOfBirth(formattedDob);
            
            // 3. Tạo tài khoản (userName): nguyen van hai duong -> nvhduong
            String userName = generateUserName(originalName);
            customer.setUserName(userName);
            
            System.out.println("Sau khi cập nhật: " + customer);
            
            // Tuần tự hóa lại đối tượng Customer đã sửa đổi
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(customer);
            oos.flush();
            byte[] modifiedObjectBytes = baos.toByteArray();
            
            // Đóng gói thông điệp gửi lên server: 8 byte đầu là requestId + các byte sau là đối tượng Customer
            byte[] sendBuffer = new byte[8 + modifiedObjectBytes.length];
            byte[] reqIdBytes = requestId.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(reqIdBytes, 0, sendBuffer, 0, Math.min(reqIdBytes.length, 8));
            System.arraycopy(modifiedObjectBytes, 0, sendBuffer, 8, modifiedObjectBytes.length);
            
            DatagramPacket packetC = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
            socket.send(packetC);
            System.out.println("Đã gửi phản hồi đối tượng lên server.");
            
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
    
    // Hàm chuẩn hóa tên: nguyen van hai duong -> DUONG, Nguyen Van Hai
    private static String formatName(String name) {
        if (name == null || name.trim().isEmpty()) return name;
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].toUpperCase();
        }
        String lastName = parts[parts.length - 1].toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            String w = parts[i];
            if (!w.isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase());
            }
        }
        return lastName + ", " + sb.toString();
    }
    
    // Hàm chuyển đổi ngày sinh từ mm-dd-yyyy sang dd/mm/yyyy
    private static String formatDob(String dob) {
        if (dob == null || dob.trim().isEmpty()) return dob;
        String[] parts = dob.trim().split("-");
        if (parts.length == 3) {
            // parts[0] = mm, parts[1] = dd, parts[2] = yyyy
            return parts[1] + "/" + parts[0] + "/" + parts[2];
        }
        return dob;
    }
    
    // Hàm tạo username: nguyen van hai duong -> nvhduong
    private static String generateUserName(String name) {
        if (name == null || name.trim().isEmpty()) return "";
        String[] parts = name.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(parts[i].charAt(0));
            }
        }
        if (parts.length > 0) {
            sb.append(parts[parts.length - 1]);
        }
        return sb.toString();
    }
}
