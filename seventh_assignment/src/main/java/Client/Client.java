package Client;
import Content.Message;
import Server.Server;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client{
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT_NUMBER = 3737;
    public List<User> users = null;
    private volatile boolean usersFound = false;
    private final Socket clientSocket;
    private User currentUser;
    DataInputStream in;
    DataOutputStream out;
    public Client(Socket clientSocket) {
        this.clientSocket = clientSocket;

        try {
            this.out = new DataOutputStream(clientSocket.getOutputStream());
            HandleServerResponse responseHandler = new HandleServerResponse(clientSocket, this);
            (new Thread(responseHandler)).start();
            out.writeUTF("GetUsers");
            out.flush();

            while (!usersFound) {
                Thread.onSpinWait();
            }


            while(currentUser == null)
            {
                System.out.println("Enter Username: ");
                System.out.flush();
                Scanner usernameScanner = new Scanner(System.in);

                String username = usernameScanner.next();

                for(User user : users)
                {
                    if(user.getUsername().equals(username))
                    {
                        currentUser = user;
                        out.writeUTF("SetUser|||" + currentUser.getUsername());
                        break;
                    }
                }

                System.out.println("USER NOT FOUND. Please enter again.");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while(true)
            {
                System.out.println("\n\n----MENU----\n\nPlease pick:\n1)Enter Group Chat\n2)Download Music Lyrics");
                Scanner scanner = new Scanner(System.in);

                int option = scanner.nextInt();

                if(option == 1)
                {
                    try {
                        out.writeUTF("GetMessages");
                        while (true) {

                            String userInput = reader.readLine();
                            if(userInput.toLowerCase().trim().equals("exit"))
                            {
                                out.writeUTF("exit");
                                out.flush();
                                Thread.sleep(500);
                                System.exit(0);
                            }

                            Message message = new Message(userInput, currentUser.getUsername());
                            out.writeUTF("PostMessage|||" + new JSONObject(message));
                            out.flush();
                        }
                    }
                    catch (Exception ex)
                    {
                        System.out.println("SOMETHING WENT WRONG!!!\n\n");
                    }

                }
                else
                {
                    List<String> fileNames =  Server.getFileNames();
                    int i = 0;
                    for(String fileName : fileNames)
                    {
                        i++;
                        System.out.println(i + ") " + fileName.replace(".txt",""));
                    }
                    System.out.println("----Pick----");
                    Scanner fileInput = new Scanner(System.in);
                    int selected = fileInput.nextInt();
                    if(selected > i)
                    {
                        selected = i;
                    }
                    if(selected < 1)
                    {
                        selected = 1;
                    }

                    out.writeUTF("DownloadFile|||" + (selected-1));
                    Thread.sleep(1000);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not connect to the server!!!\n\n");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            Socket clientSocket = new Socket(IP_ADDRESS, PORT_NUMBER);
            Client client = new Client(clientSocket);
        }
        catch (Exception ex)
        {

        }
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsersFound(boolean value) {
        usersFound = value;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}