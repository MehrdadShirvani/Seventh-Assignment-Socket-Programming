//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HandleServerResponse implements Runnable {

    private final Client client;
    private final DataInputStream in;

    public HandleServerResponse(Socket clientSocket, Client client) throws IOException {
        this.client = client;
        this.in = new DataInputStream(clientSocket.getInputStream());
    }
    @Override
    public void run() {

        while(true) {
            try {
                String response = this.in.readUTF();
                if(response == null)
                {
                    System.out.println("LEFT");
                    return;
                }

                if(false){}
                else if (response.startsWith("GetUsers|||"))
                {
                    String rawUsers = response.replace("GetUsers|||","");
                    JSONArray array = new JSONArray(rawUsers);
                    client.users = new ArrayList<User>();
                    for(int i = 0; i < array.length(); i++)
                    {
                        JSONObject jsonObject =  array.getJSONObject(i);
                        client.users.add(new User(jsonObject.getString("username"),jsonObject.getString("userColor")));
                    }
                    client.setUsersFound(true);
                }
                else if(response.startsWith("GetMessages|||"))
                {
                    String message = response.replace("GetMessages|||","");
                    JSONArray array = new JSONArray(message);
                    for(int i = 0; i < array.length(); i++)
                    {
                        System.out.println(array.getJSONObject(i).getString("username") + ": " + array.getJSONObject(i).getString("text"));
                        System.out.flush();
                    }
                }
                else if(response.equals("GetFileNames|||"))
                {

                }
                else if(response.startsWith("NewMessage|||"))
                {
                    String message = response.replace("NewMessage|||","");
                    System.out.println(message);
                    System.out.flush();

                }
                else if(response.startsWith("DownloadFile|||"))
                {
                    String message = response.replace("DownloadFile|||","");

                    String saveDirectory = "C:\\SocketProgrammingPractice\\" + client.getCurrentUser().getUsername();


                    try
                    {
                        Path directory = Paths.get(saveDirectory);
                        Files.createDirectories(directory);
                        String fileName =  message.split("[+]{3}")[0];
                        String text =  message.split("[+]{3}")[1];
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "\\" + fileName))) {
                            writer.write(text);
                        }
                        catch (IOException e) {
                        }
                        System.out.println("File Successfully Saved!\nDirectory: " + directory + "\\" + fileName);
                        System.out.flush();

                    }
                    catch (IOException ignore)
                    {

                    }

                }

            } catch (IOException ex) {

            }
        }
    }

}
