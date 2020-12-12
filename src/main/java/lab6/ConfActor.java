package lab6;

import akka.actor.AbstractActor;

import java.util.ArrayList;

public class ConfActor extends AbstractActor {
    ArrayList<String> servers = new ArrayList<>();

    @Override
    public Receive createReceive() {

    }
}
