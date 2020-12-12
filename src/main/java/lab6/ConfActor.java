package lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import lab6.Messages.GetServer;
import lab6.Messages.RefreshList;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ConfActor extends AbstractActor {
    ArrayList<String> servers = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(RefreshList.class, msg ->{
                    servers.clear();
                    servers.addAll(msg.getServers());
                })
                .match(GetServer.class, msg->{
                    int index = ThreadLocalRandom.current().nextInt(servers.size());
                    sender().tell();
                })
                .build();
    }
}
