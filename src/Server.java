import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
            this.serverSocket = serverSocket; //Listening for incoming users

    }

    public void Start(){
        while(!serverSocket.isClosed()){
            try{
                Socket clientSocket = serverSocket.accept(); //accept the user connection
                System.out.println( " A new person has joined the chat");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void closeServer(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.Start();
    }
}
