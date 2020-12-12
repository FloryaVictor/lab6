package lab6;

import akka.actor.Actor;
import akka.actor.ActorRef;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ServerWatcher implements Watcher {
    private final ActorRef confActor;
    public ServerWatcher(ActorRef confActor){
        this.confActor = confActor;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDeleted ||
            watchedEvent.getType() == Event.EventType.NodeCreated){
            watchedEvent.
        }
    }
}
