package lab6;

import akka.actor.Actor;
import akka.actor.ActorRef;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ServerWatcher implements Watcher {
    private final ActorRef confActor;

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
