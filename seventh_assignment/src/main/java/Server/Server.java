package Server;

import Client.Client;
import Client.ClientHandler;
import Client.User;
import Content.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int PORT = 3737;
    private static ArrayList<ClientHandler> clients = new ArrayList();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    private static List<Message> messages = new ArrayList<>();
    private static final List<String> fileNames =  Arrays.asList("a-man-without-love-ngelbert-Hmperdinck.txt","all-of-me-john-legend.txt","birds-imagine-dragons.txt","blinding-lights-the-weekend.txt", "dont-matter-to-me-drake.txt", "feeling-in-my-body-elvis.txt", "out-of-time-the-weekend.txt", "something-in-the-way-nirvana.txt", "why-you-wanna-trip-on-me-michael-jackson.txt", "you-put-a-spell-on-me-austin-giorgio.txt");
    private static final List<User> users = Arrays.asList(new User("user1", "green"), new User("user2","blue"), new User("user3","purple"), new User("user4", "yellow"));


    public static void main(String[] args) {
        ServerSocket listener = null;

        try {
            listener = new ServerSocket(PORT);
            System.out.println("[SERVER] Server started. Waiting for client connections...");
            int i = 1;
            while(true) {
                Socket client = listener.accept();

                System.out.println("[SERVER] Connected to client: " + String.valueOf(client.getInetAddress()));
                ClientHandler clientThread = new ClientHandler(client);
                i++;
                clients.add(clientThread);
                pool.execute(clientThread);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (listener != null) {
                try {
                    listener.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            pool.shutdown();
        }

    }
    public static synchronized void newMessage(Message message) {
        messages.add(message);
    }
    public static synchronized List<Message> getMessages()
    {
        return messages;
    }
    public static List<String> getFileNames()
    {
        return fileNames;
    }
    public static List<User> getUsers() {
        return  users;
    }
    public static void sendToAll(Message newMessage)
    {
        for(ClientHandler clientHandler : clients)
        {
            if(!clientHandler.getUsername().equals(newMessage.getUsername()))
            {
                clientHandler.print("NewMessage|||" + newMessage.getUsername() +": " + newMessage.getText());
            }
        }
    }

}
