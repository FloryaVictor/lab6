package lab6.Messages;

import java.util.ArrayList;

public class RefreshList {
    private ArrayList<String> servers;

    public RefreshList(ArrayList<String> servers){
        this.servers = servers;
    }

    public ArrayList<String> getServers(){
        return servers
    }
}
