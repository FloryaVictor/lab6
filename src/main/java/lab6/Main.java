package lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import static akka.http.javadsl.server.Directives.*;
import org.apache.zookeeper.*;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

import lab6.Messages.GetServer;
import lab6.Messages.RefreshList;

public class Main {
    public static final String URL = "url";
    public static final String COUNT = "count";
    private static final Duration timeout = Duration.ofSeconds(5);
    public static final String HOST = "localhost";
    public static final String zookeeperConnectString  = "localhost:2181";
    public static Http http;
    public static ActorRef confActor;
    public static ZooKeeper keeper;
    public static int PORT;


    public static void main(String[] argv) throws IOException, KeeperException, InterruptedException {
        ActorSystem system = ActorSystem.create("routes");
        http = Http.get(system);
        confActor = system.actorOf(Props.create(ConfActor.class));
        PORT = Integer.parseInt(argv[0]);
        initZooKeeper();
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(HOST, PORT),
                materializer
        );
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound ->{
                    system.terminate();
                });
    }
    public static void initZooKeeper() throws IOException, KeeperException, InterruptedException {
        keeper = new ZooKeeper(zookeeperConnectString,
                (int)timeout.getSeconds() * 1000, watcher);
        keeper.create("/servers/" + PORT, (PORT+"").getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        WatchedEvent e = new WatchedEvent(Watcher.Event.EventType.NodeCreated,
                Watcher.Event.KeeperState.SyncConnected, "");
        watcher.process(e);
    }

    public static Watcher watcher = watchedEvent -> {
        if (watchedEvent.getType() == Watcher.Event.EventType.NodeCreated ||
                watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted ||
                watchedEvent.getType() == Watcher.Event.EventType.NodeDataChanged){
            ArrayList<String> newServers = new ArrayList<>();
            try {
                for(String s: keeper.getChildren("/servers", null)){
                    byte[] port = keeper.getData("/servers/" + s, false, null);
                    newServers.add(new String(port));
                }
                confActor.tell(new RefreshList(newServers), ActorRef.noSender());
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private static CompletionStage<HttpResponse> fetch(String url) {
        return http.singleRequest(HttpRequest.create(url));
    }

    public static Route createRoute() {
        return route(get(() ->
                        parameter(URL, url ->
                                parameter(COUNT, count -> {
                                    if (Integer.parseInt(count) <= 0){
                                        return completeWithFuture(fetch(url));
                                    }
                                    return completeWithFuture(Patterns.ask(confActor, new GetServer(), timeout)
                                            .thenApply(nextPort -> (String)nextPort)
                                            .thenCompose(nextPort ->
                                                    fetch(String.format(
                                                            "http://%s:%s?url=%s&count=%d",
                                                            HOST, nextPort, url, Integer.parseInt(count) - 1))));
                                })
                        )
                )
        );
    }
}
