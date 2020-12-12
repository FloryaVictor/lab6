package lab6;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class Server {
    public static final String URL = "url";
    public static final String COUNT = "count";

    public final int port;
    public final String host;
    public final ActorSystem system;
    public final ActorMaterializer mat;
    public Server(int port, ActorSystem system, ActorMaterializer mat){
        this.port = port;
        this.system = system;
        this.mat = mat;
        this.host = "localhost";
    }

    public static Flow<HttpRequest, HttpResponse, NotUsed> createFlow(ActorMaterializer mat){
        return Flow.of(HttpRequest.class)
                .map(req->{
                    Query q = req.getUri().query();
                    String url = q.get(URL).get();
                })
    }
    public void start(){
        final Http http = Http.get(system);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host, port),
                mat
        );
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound ->{
                });
    }
}
