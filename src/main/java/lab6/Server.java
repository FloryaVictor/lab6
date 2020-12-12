package lab6;

import akka.NotUsed;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import lab6.Messages.GetServer;
import org.apache.zookeeper.ZooKeeper;
import scala.concurrent.Future;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static akka.http.javadsl.server.Directives.*;
import static akka.pattern.Patterns.ask;

public class Server {
    public static final String URL = "url";
    public static final String COUNT = "count";
    private final static Duration timeout = Duration.ofSeconds(5);
    public static int PORT;
    public static String HOST = "localhost";
    public static Http http;
    public static ActorRef confActor;

    public static void main(String[] argv) throws IOException {
        ZooKeeper keeper = new ZooKeeper(HOST + ":" + PORT, timeout, )
        PORT = Integer.parseInt(argv[0]);
        ActorSystem system = ActorSystem.create("routes");
        http = Http.get(system);
        confActor = system.actorOf(Props.create(ConfActor.class));
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

    private static CompletionStage<HttpResponse> fetch(String url) {
        return http.singleRequest(HttpRequest.create(url));
    }

    public static Route createRoute() {
        return route(get(() ->
                        parameter("url", url ->
                                parameter("count", count -> {
                                    if (Integer.parseInt(count) <= 0){
                                        return completeWithFuture(fetch(url));
                                    }
                                    return completeWithFuture(Patterns.ask(confActor, new GetServer(), timeout)
                                            .thenApply(nextPort -> (String)nextPort)
                                            .thenCompose(nextPort ->
                                                    fetch(String.format(
                                                        "http://%s:%s?url=%s&count=%d",
                                                        HOST, null, url, Integer.parseInt(count) - 1))));
                                })
                        )
                )
        );
    }
}
