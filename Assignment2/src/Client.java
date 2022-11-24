import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.Socket;

public class Client {
	private static final String CREATE = "#CREATE";
	private static final String JOIN = "#JOIN";
	private static final String EXIT = "#EXIT";
	private static final String PUT = "#PUT";
	private static final String GET = "#GET";
	private static String room;
	protected static String name;
	protected static volatile boolean finished = false;
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Please enter <server IP address, port number 1, port number 2> as an argument!");
		} else {
			try {
				String address = args[0];
				int port1 = Integer.parseInt(args[1]);
				int port2 = Integer.parseInt(args[2]);
				
				Scanner scanner = new Scanner(System.in);
				while (true) {
					String input;
					input = scanner.nextLine();
					if (input.equalsIgnoreCase(Client.CREATE)) {
						System.out.print("Enter chatting room name : ");
            			room = scanner.nextLine();
            			System.out.print("Enter your name : ");
            			name = scanner.nextLine();
						break;
					}
					if (input.equalsIgnoreCase(Client.JOIN)) {
						System.out.print("Enter chatting room name : ");
            			room = scanner.nextLine();
            			System.out.print("Enter your name : ");
            			name = scanner.nextLine();
						break;
					}
				}
				
				Socket chatSocket = new Socket(address, port1);
				Socket fileSocket = new Socket(address, port2);
				
                while (true) {
                    String message;
                    message = scanner.nextLine();
                    if (message.equalsIgnoreCase(Client.EXIT)) {
                        finished = true;
                        chatSocket.close();
                        fileSocket.close();
                        break;
                    } else if (!message.startsWith("#")) {
	                    message = "FROM " + name + " : " + message;
	                    byte[] buffer = message.getBytes();

                    }
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
