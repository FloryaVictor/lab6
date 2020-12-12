package lab6;

import akka.stream.ActorMaterializer;

public class Server {
    public final int port;
    public final ActorMaterializer mat;
    public Server(int port, ActorMaterializer mat){
        this.port = port;
        this.mat = mat;
    }
    public void start(){

    }
}
