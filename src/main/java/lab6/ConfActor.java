package lab6;

import akka.actor.AbstractActor;
import lab6.Messages.RefreshList;

import java.util.ArrayList;

public class ConfActor extends AbstractActor {
    ArrayList<String> servers = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder().create()
                .match(RefreshList.class, msg ->{
                    servers.rep
                })
                .match().build();
    }
}
