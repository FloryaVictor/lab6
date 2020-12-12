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
                    servers = msg.getServers();
                })
                .match(GetServer.class, msg->{
                    String s = servers.get(rand.nextInt(servers.size()));
                    System.out.println("fdls;kkwfeookfewopwfekfoewfkep");
                    getSender().tell(s, ActorRef.noSender());
                })
                .build();
    }
}
