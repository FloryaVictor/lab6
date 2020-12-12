package lab6;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import lab6.Messages.GetServer;
import lab6.Messages.RefreshList;

import java.util.ArrayList;
import java.util.Random;

public class ConfActor extends AbstractActor {
    ArrayList<String> servers = new ArrayList<>();
    Random rand = new Random();
    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(RefreshList.class, msg ->{
                    servers.clear();
                    servers.addAll(msg.getServers());
                })
                .match(GetServer.class, msg->{
                    getSender().tell(servers.get(rand.nextInt(servers.size())), ActorRef.noSender());
                })
                .build();
    }
}
