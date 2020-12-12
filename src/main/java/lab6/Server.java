package lab6;

import akka.NotUsed;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import lab6.Messages.GetServer;
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

    public final Http http;
    public final int port;
    public final String host;
    public final ActorSystem system;
    public final ActorMaterializer mat;
    public final ActorRef confActor;

    public Server(int port, ActorSystem system, ActorMaterializer mat, ActorRef confActor){
        this.port = port;
        this.host = "localhost";
        this.system = system;
        this.mat = mat;
        this.confActor = confActor;
        http = Http.get(system);
    }

    private CompletionStage<HttpResponse> fetch(String url) {
        return http.singleRequest(HttpRequest.create(url));
    }

    public void start() throws IOException {
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                createRoute().flow(system, mat);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host, port),
                mat
        );
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound ->{
                });
    }

    public Route createRoute() {
        return route(get(() ->
                        parameter("url", url ->
                                parameter("count", count -> {
                                    if (Integer.parseInt(count) <= 0){
                                        return completeWithFuture(fetch(url));
                                    }
                                    String nextPort = null;
                                    try {
                                        nextPort = (String)Patterns.ask(confActor, new GetServer(), timeout).toCompletableFuture().get();
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    return completeWithFuture(fetch(String.format(
                                            "%s:%s?url=%s&count=%d",
                                            host, nextPort, url, Integer.parseInt(count) - 1)));
                                })
                        )
                )
        );
    }
}
