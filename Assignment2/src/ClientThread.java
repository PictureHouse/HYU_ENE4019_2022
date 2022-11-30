import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket chatSocket;
    private Socket fileSocket;

    public ClientThread(Socket chatSocket, Socket fileSocket) {
        this.chatSocket = chatSocket;
        this.fileSocket = fileSocket;
    }

    public void run() {
        try {
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
                System.out.println(reader.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendMessage(String message) throws Exception {
        DataOutputStream dos = new DataOutputStream(chatSocket.getOutputStream());
        if (!message.startsWith("#") || message.toUpperCase().startsWith("#CREATE") || message.toUpperCase().startsWith("#JOIN") || message.toUpperCase().startsWith("#EXIT") || message.toUpperCase().startsWith("#STATUS")) {
        	String output = message + "\n";
        	dos.writeBytes(output); 
        }
    }
}

