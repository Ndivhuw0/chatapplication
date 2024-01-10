import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private Socket clientSocket;
    private BufferedReader read;
    private BufferedWriter write;
    private String userName;

    public Client(Socket clientSocket, String userName){
        try{
            this.clientSocket = clientSocket;
            this.write = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.read = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.userName = userName;
        }catch(IOException e){
            closeAll(clientSocket, read, write);
        }
    }

    public void Listen(){

        new Thread(new Runnable(){

            @Override
            public void run(){
                try{
                    while(clientSocket.isConnected()){
                        String messageGroup = read.readLine();
                        System.out.println(messageGroup);
                    }
                }catch(IOException e){
                    closeAll(clientSocket, read, write);
                }
            }
        }).start();
    }

    public void sendMessage(String message){
        try{
            write.write(userName);
            write.newLine();
            write.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while(clientSocket.isConnected()){
                message = reader.readLine();
                write.write(userName + ": " + message);
                write.newLine();
                write.flush();
            }
        }catch(IOException e){
            closeAll(clientSocket, read, write);
        }
    }

    public void closeAll(Socket clientSocket, BufferedReader read, BufferedWriter write){
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
        }catch(IOException e){
            e.getStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException{

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter your userName");
        String userName = reader.readLine();
        Socket clientSocket = new Socket("localhost", 1234);
        Client client = new Client(clientSocket, userName);
        client.Listen();
        client.sendMessage(userName);
    } 
}

