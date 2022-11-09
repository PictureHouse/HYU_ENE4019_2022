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
	//프로그램 실행인자로 port number를 입력 받도록 하였고 실행인자가 없거나 더 많을 경우에는 에러 메시지가 나오도록 하였다.
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Please enter port number as an argument!");
		} else {
			try {
            	int port = Integer.parseInt(args[0]);
            	InetAddress group;
            	Scanner scanner = new Scanner(System.in);
            	while (true) {
            		String input;
            		input = scanner.nextLine();
            		//채팅에 참여하기 위한 #JOIN 명령어를 구현하였다. #JOIN을 입력 받으면 참여할 채팅방의 이름을 입력하라는 메시지가 출력된다.
            		if (input.equalsIgnoreCase(Peer.JOIN)) {
            			/*
            			 * 채팅방의 이름을 입력 받으면 ‘SHA-256’ 해시를 이용해서 해시 값으로 byte 배열을 받는다. 
            			 * 배열의 값들 중 뒤에서 세번째 값을 x값으로, 뒤에서 두번째 값을 y값으로, 마지막 값을 z값으로 구한 후 이 값들로 Multicast address 225.x.y.z를 구했다. 
            			 * 테스트를 해보면서 배열의 값이 음수인 경우에 에러가 발생하는 것을 확인하였고 이러한 경우를 위해 음수인 값은 양수로 바꿔주는 코드를 추가하였다.
            			 */
            			System.out.print("Enter chatting room name : ");
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
            			
            			System.out.print("Enter your name : ");
            			name = scanner.nextLine();
            			break;
            		}
            	}
            	
                MulticastSocket socket = new MulticastSocket(port);
                socket.setTimeToLive(0);                  
                socket.joinGroup(group);
                
                Thread thread = new Thread(new ReadThread(port, group, socket));
                thread.start(); 
                
                System.out.println("Insert message...\n");
                while (true) {
                    String message;
                    message = scanner.nextLine();
                    //#EXIT 명령어를 입력하였을 경우에 채팅방을 나갈 수 있도록 하였다.
                    if (message.equalsIgnoreCase(Peer.EXIT)) {
                        finished = true;
                        socket.leaveGroup(group);
                        socket.close();
                        break;
                    } else if (!message.startsWith("#")) {
                    	//입력 받은 메시지가 #으로 시작하지 않을 때에만 입력 받은 메시지 앞에 전송자 정보를 붙이고 메시지가 전달되도록 하였다. 즉, #으로 시작하는 메시지는 전달되지 않는다.
	                    message = "FROM " + name + " : " + message;
	                    byte[] buffer = message.getBytes();
	                    DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
	                    socket.send(datagram);
                    }
                }
            } catch (NoSuchAlgorithmException nsae) {
            	System.out.println("Hashing error!");
            	nsae.printStackTrace();
            } catch (SocketException se) {
            	System.out.println("Socket error!");
            	se.printStackTrace();
            } catch (IOException ie) {
            	System.out.println("Socket input/output error!");
            	ie.printStackTrace();
            }  
        }
    }
}

//채팅 메시지를 전송하는 동작과 채팅 메시지를 읽는 동작이 동시에 동작할 수 있도록 Thread를 이용해서 구현하였다.
class ReadThread implements Runnable {
	private int port;
	private InetAddress group;
	private MulticastSocket socket;
	private static final int MAX_LEN = 512;
    
	public ReadThread (int port, InetAddress group, MulticastSocket socket) {
		this.port = port;
		this.group = group;
		this.socket = socket;
	}
      
    @Override
    public void run() {
        while (!Peer.finished) {
        	//채팅 메시지를 chunk 단위(512 byte)로 나누어서 전송하도록 하였다.
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
        		System.out.println("Good bye!");
        	}
        }
    }
}
