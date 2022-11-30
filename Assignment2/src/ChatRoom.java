import java.util.ArrayList;

public class ChatRoom {
    private String roomName;
    private ArrayList<ServerThread> memberList;

    public ChatRoom(String name) throws Exception {
        this.roomName = name;
        this.memberList = new ArrayList<ServerThread>();
    }

    public String getRoomName() {
        return this.roomName;
    }
    
    public boolean exist(ServerThread member) {
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) == member) {
            	return true;
            }
        }
        return  false;
    }

    public void add(ServerThread member) {
        memberList.add(member);
    }
    
    public void remove(ServerThread member) {
        memberList.remove(member);
    }

    public String printMember() {
        String result = "";
        for (int i = 0; i < memberList.size(); i++) {
            if (i == memberList.size() - 1) {
            	result += memberList.get(i).getUserName();
            } else {
            	result += memberList.get(i).getUserName() + ", ";
            }
        }
        return result;
    }

    public void sendToAll(String message) throws Exception {
        for (int i = 0; i < memberList.size(); i++) {
            memberList.get(i).sendMessage(message);
        }
    }
}