import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static final String CREATE = "#CREATE";
	private static final String JOIN = "#JOIN";
	private static final String EXIT = "#EXIT";
	private static final String STATUS = "#STATUS";
    private static ServerSocket chatServerSocket;
    private static ServerSocket fileServerSocket;
    private static ArrayList<ChatRoom> roomList;

    public static void main(String[] args) {
    	if (args.length != 2) {
			System.out.println("Please enter 2 port numbers as an argument!");
		} else {
	    	try {
	            int port1 = Integer.parseInt(args[0]);
	            int port2 = Integer.parseInt(args[1]);
	
	            chatServerSocket = new ServerSocket(port1);
	            fileServerSocket = new ServerSocket(port2);
	            
	            roomList = new ArrayList<ChatRoom>();
	            roomList.add(new ChatRoom("Waiting Room"));
	
	            System.out.println("Server ON");
	
	            while (true) {
	            	Socket chatSocket = chatServerSocket.accept();
	                Socket fileSocket = fileServerSocket.accept();
	                
	                ServerThread serverThread = new ServerThread(chatSocket, fileSocket);
	                serverThread.start();
	
	                goToWaitingRoom(serverThread);
	
	                System.out.println("New connection");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return;
	        }
		}
    }

    public static void handleMessage(ServerThread member, String message) throws Exception {
        String[] split = message.split(" ");

        if (split[0].equalsIgnoreCase(Server.CREATE)) {
            create(member, split[1], split[2]);
        } else if (split[0].equalsIgnoreCase(Server.JOIN)) {
            join(member, split[1], split[2]);
        } else if (split[0].equalsIgnoreCase(Server.EXIT)) {
            exit(member);
        } else if (split[0].equalsIgnoreCase(Server.STATUS)) {
            status(member);
        } else {
        	chattingRoom(member,message);
        }
    }
    
    public static void create(ServerThread member, String roomName, String name) throws Exception {
        for (int i = 1; i < roomList.size(); i++) {
            if (roomList.get(i).getRoomName().equals(roomName)) {
                member.sendMessage(("This room name is already exist!"));
                return;
            }
        }
        roomList.add(new ChatRoom(roomName));
        goToChattingRoom(member, roomName, name);
        System.out.println("<" + roomName + "> created");
        member.sendMessage("<" + roomName + "> created");
    }

    public static void join(ServerThread member, String roomName, String name) throws Exception {
        if (goToChattingRoom(member, roomName, name) == 1) {
            member.sendMessage("<" + roomName + ">");
        } else {
        	member.sendMessage("Room does not exist!");
        }
    }

    public static void exit(ServerThread member) throws Exception {
        if (goToWaitingRoom(member) == 1) {
        	member.sendMessage("Enter waiting room.");
        } else {
        	member.sendMessage("Currently in waiting room.");
        }
    }

    private static int goToWaitingRoom(ServerThread member) {
        if (roomList.get(0).exist(member) == false) {
            for (int i = 0; i < roomList.size(); i++) {
                roomList.get(i).remove(member);
            }
            member.setUserName("waiting person");
            roomList.get(0).add(member);
            return 1;
        }
        return 0;
    }

    private static int goToChattingRoom(ServerThread member, String roomName, String name) throws Exception {
        goToWaitingRoom(member);
        for (int i = 1; i < roomList.size(); i++) {
            if (roomList.get(i).getRoomName().equals(roomName)) {
                roomList.get(0).remove(member);
                member.setUserName(name);
                roomList.get(i).add(member);
                return 1;
            }
        }
        return 0;
    }
    
    public static void chattingRoom(ServerThread member, String message) throws Exception {
        for (int i = 1; i < roomList.size(); i++) {
            if (roomList.get(i).exist(member)) {
                roomList.get(i).sendToAll("FROM " + member.getUserName() + " : "+ message);
                return;
            }
        }
    }

    public static void status(ServerThread member) throws Exception {
        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).exist(member)) {
                member.sendMessage("Chatting Room : " + roomList.get(i).getRoomName() + " | Member : " + roomList.get(i).printMember());
                return;
            }
        }
    }
}
