import java.net.*;
import java.io.*;

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