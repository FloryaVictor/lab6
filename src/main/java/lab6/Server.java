package lab6;

import akka.NotUsed;
import akka.actor.Actor;
import akka.actor.ActorRef;
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

    public final Http http;
    public final int port;
    public final String host;
    public final ActorSystem system;
    public final ActorMaterializer mat;
    public final ActorRef confActor;

    public Server(int port, ActorSystem system, ActorMaterializer mat, ActorRef confActor){
        this.port = port;
        this.system = system;
        this.mat = mat;
        this.confActor = confActor;
        this.host = "localhost";
        http = Http.get(system);
    }

    private CompletionStage<HttpResponse> fetch(String url) {
        return http.singleRequest(HttpRequest.create(url));
    }

    private Flow<HttpRequest, HttpResponse, NotUsed> createFlow(ActorMaterializer mat){
        return Flow.of(HttpRequest.class)
                .map(req->{
                    Query q = req.getUri().query();
                    String url = q.get(URL).get();
                    int count = Integer.parseInt(q.get(COUNT).get());
                    if (count >=0){
                        return 
                    }else {
                        return fetch(url).thenApply(
                                resp ->{return resp;}
                        );
                    }
                });
    }
    public void start(){

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
