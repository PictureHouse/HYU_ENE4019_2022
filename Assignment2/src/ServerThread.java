import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket chatSocket;
    private Socket fileSocket;
    private String name;

    public ServerThread(Socket chatSocket, Socket fileSocket) {
        this.chatSocket = chatSocket;
        this.fileSocket = fileSocket;
        this.name = "no name";
    }
    
    public void setUserName(String name) {
        this.name = name;
    }
    
    public String getUserName() {
        return name;
    }

    public void run() {
        try {
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
                Server.handleMessage(this, reader.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
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