import java.net.*;
import java.io.*;
import java.util.*;

public class Peer {
	private static final String JOIN = "#JOIN";
    private static final String EXIT = "#EXIT";
    static String address;
    static String name;
    static volatile boolean finished = false;
    
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("포트 번호가 argument로 필요합니다!");
        else {
            try {
            	int port = Integer.parseInt(args[0]);
            	InetAddress group;
            	
            	Scanner scanner = new Scanner(System.in);
            	while (true) {
            		String input;
            		input = scanner.nextLine();
            		if (input.equalsIgnoreCase(Peer.JOIN)) {
            			System.out.print("참여할 채팅방의 이름 : ");
            			address = scanner.nextLine();
            			group = InetAddress.getByName(address);
            			
            			System.out.print("사용자 이름 : ");
            			name = scanner.nextLine();
            			break;
            		}
            	}
            	
                MulticastSocket socket = new MulticastSocket(port);
                
                // Since we are deploying
                socket.setTimeToLive(0);
                //this on localhost only (For a subnet set it as 1)
                  
                socket.joinGroup(group);
                Thread t = new Thread (new ReadThread(port, group, socket));
              
                // Spawn a thread for reading messages
                t.start(); 
                  
                // sent to the current group
                System.out.println("메시지를 입력하세요...\n");
                while (true) {
                    String message;
                    message = scanner.nextLine();
                    if (message.equalsIgnoreCase(Peer.EXIT)) {
                        finished = true;
                        socket.leaveGroup(group);
                        socket.close();
                        break;
                    }
                    message = "FROM " + name + " : " + message;
                    byte[] buffer = message.getBytes();
                    DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
                    socket.send(datagram);
                }
            } catch (SocketException se) {
                System.out.println("소켓 생성 에러");
                se.printStackTrace();
            } catch (IOException ie) {
                System.out.println("소켓 값 읽기 쓰기 에러");
                ie.printStackTrace();
            }
        }
    }
}
