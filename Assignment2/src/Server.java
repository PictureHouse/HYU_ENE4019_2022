import java.util.Scanner;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
	private static final String STATUS = "#STATUS";
	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Please enter <port number 1, port number 2> as an argument!");
		} else {
			try {
				int port1 = Integer.parseInt(args[0]);
				int port2 = Integer.parseInt(args[1]);
				
				ServerSocket chatServerSocket = new ServerSocket(port1);
				ServerSocket fileServerSocket = new ServerSocket(port2);
				
				Socket chatSocket = chatServerSocket.accept();
				Socket fileSocket = fileServerSocket.accept();
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
