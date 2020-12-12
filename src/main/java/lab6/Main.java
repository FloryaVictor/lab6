package lab6;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] argv) throws IOException {
        ActorSystem system = ActorSystem.create("routes");
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        Server s = new Server(8081, system, materializer, null);
        ActorSystem system1 = ActorSystem.create("routes");
        final ActorMaterializer materializer1 = ActorMaterializer.create(system);
        Server s1 = new Server(8082, system1, materializer1, null);
        s.start();
        s1.start();
    }

}
