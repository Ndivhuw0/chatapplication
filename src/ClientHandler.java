import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> users = new ArrayList<>();
    private Socket clientSocket;
    private BufferedReader read;
    private BufferedWriter write;
    private String userName;
    
    public ClientHandler(Socket clienSocket){
        try{
            this.clientSocket = clienSocket;
            this.write = new BufferedWriter(new OutputStreamWriter(clienSocket.getOutputStream()));
            this.read = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()));
            this.userName = read.readLine();
            users.add(this);
            broadcastMsg(userName + " has joined the chat");
        }catch(IOException e){
            closeAll(clienSocket, read, write);
        }
    }

    @Override
    public void run(){ 
        while(clientSocket.isConnected()){
            try{
                String clientMessage = read.readLine();
                broadcastMsg(clientMessage);
            }catch(IOException e){
                closeAll(clientSocket, read, write);
                break;
            }
        }
    }

    public void broadcastMsg(String message){
        for(ClientHandler clientHandler: users){
            try{
                if(!clientHandler.userName.equals(userName)){
                    clientHandler.write.write(message);
                    clientHandler.write.newLine();
                    clientHandler.write.flush();
                }
            }catch(IOException e){
                closeAll(clientSocket, read, write);
            }
        }

    }

    public void leftClientHandler(){
        users.remove(this);
        broadcastMsg(userName + " left the chat");
    }

    public void closeAll(Socket clientSocket, BufferedReader read, BufferedWriter write){

        leftClientHandler();
        try{
            if(read != null){
                read.close();
            }
            if(write != null){
                write.close();
            }
            if(clientSocket != null){
                clientSocket.close();
            }
        }catch( IOException e){
            e.getStackTrace();
        }
    }


}
