import java.net.*;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;

public class Peer {
	private static final String JOIN = "#JOIN";
    private static final String EXIT = "#EXIT";
    private static String room;
    private static String address;
    protected static String name;
    protected static volatile boolean finished = false;
    
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
            			room = scanner.nextLine();
            			MessageDigest digest = MessageDigest.getInstance("SHA-256");
            			byte[] hash = digest.digest(room.getBytes(StandardCharsets.UTF_8));
            			int x = hash[hash.length - 3];
            			if (x < 0) { x = -x; }
            			int y = hash[hash.length - 2];
            			if (y < 0) { y = -y; }
            			int z = hash[hash.length - 1];
            			if (z < 0) { z = -z; }
            			address = "225." + x + "." + y + "." + z;
            			group = InetAddress.getByName(address);
            			
            			System.out.print("사용자 이름 : ");
            			name = scanner.nextLine();
            			break;
            		}
            	}
            	
                MulticastSocket socket = new MulticastSocket(port);

                socket.setTimeToLive(0);
                  
                socket.joinGroup(group);
                Thread thread = new Thread (new ReadThread(port, group, socket));

                thread.start(); 
                
                System.out.println("메시지를 입력하세요...\n");
                while (true) {
                    String message;
                    message = scanner.nextLine();
                    if (message.equalsIgnoreCase(Peer.EXIT)) {
                        finished = true;
                        socket.leaveGroup(group);
                        socket.close();
                        break;
                    } else if (!message.startsWith("#")) {
	                    message = "FROM " + name + " : " + message;
	                    byte[] buffer = message.getBytes();
	                    DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
	                    socket.send(datagram);
                    }
                }
            } catch (NoSuchAlgorithmException nsae) {
				System.out.println("해싱 에러");
				nsae.printStackTrace();
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

class ReadThread implements Runnable {
	private int port;
    private InetAddress group;
    private MulticastSocket socket;
    private static final int MAX_LEN = 512;
    
    ReadThread (int port, InetAddress group, MulticastSocket socket) {
        this.port = port;
        this.group = group;
        this.socket = socket;
    }
      
    @Override
    public void run() {
        while (!Peer.finished) {
        	byte[] buffer = new byte[ReadThread.MAX_LEN];
        	DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
        	String message;
            try {
                socket.receive(datagram);
                message = new String(buffer, 0, datagram.getLength(), "UTF-8");	
                if (!message.startsWith("FROM " + Peer.name)) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("채팅방을 떠났습니다!");
            }
        }
    }
}
