package lab6;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.stream.ActorMaterializer;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class Server {
    public final int port;

    public final ActorSystem system;
    public final ActorMaterializer mat;
    public Server(int port, ActorSystem system, ActorMaterializer mat){
        this.port = port;
        this.system = system;
        this.mat = mat;
    }


    public void start(){
        final Http http = Http.get(system);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(, port),
                mat
        );
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound ->{
                    system.terminate();
                    try {
                        asyncHttpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
