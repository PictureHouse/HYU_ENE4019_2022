import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static ClientThread clientThread;

    public static void main(String[] args) {
    	if (args.length != 3) {
			System.out.println("Please enter an address and 2 port numbers as an argument!");
		} else {
	    	try {
	        	Scanner scanner = new Scanner(System.in);
	        	InetAddress address = InetAddress.getByName(args[0]);
	            int clientPort1 = Integer.parseInt(args[1]);
	            int clientPort2 = Integer.parseInt(args[2]);
	            int serverPort1 = 66;
	            int serverPort2 = 67;
	
	            Socket chatSocket = new Socket(address, serverPort1, null, clientPort1);
	            Socket fileSocket = new Socket(address, serverPort2, null, clientPort2);
	
	            clientThread = new ClientThread(chatSocket, fileSocket);
	            clientThread.start();
	
	            System.out.println("--------------- Chatting Room ---------------");
	
	            while (true) {
	                String input = scanner.nextLine();
	                clientThread.sendMessage(input);
	            }
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
    }
}
