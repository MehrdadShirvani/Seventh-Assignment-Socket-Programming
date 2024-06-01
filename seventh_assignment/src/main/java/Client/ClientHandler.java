
package Client;

import Content.Message;
import Server.Server;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        this.in = new DataInputStream(client.getInputStream());
        this.out = new DataOutputStream(client.getOutputStream());
    }

    public void run() {
        try {
            while(true) {

                    String request = this.in.readUTF();
                    if (request.toLowerCase().trim().equals("exit")) {
                        this.in.close();
                        this.out.close();
                        this.client.close();
                        break;
                    }
                    else if (request.equals("GetUsers")) {
                        JSONArray array = new JSONArray(Server.getUsers());
                        String message = array.toString();
                        this.out.writeUTF("GetUsers|||" + message);
                    }
                    else if (request.startsWith("SetUser|||")) {
                        username = request.replace("SetUser|||","");
                    }
                    else if (request.equals("GetMessages")) {
                        out.writeUTF("GetMessages|||" + new JSONArray(Server.getMessages()));
                    } else if (request.equals("GetFileNames")) {
                    }
                    else if (request.startsWith("PostMessage|||")) {
                        String rawJson = request.replace("PostMessage|||", "");
                        JSONObject jsonObject = new JSONObject(rawJson);
                        Message newMessage = new Message(jsonObject.getString("text"), jsonObject.getString("username"));
                        Server.newMessage(newMessage);

                        Server.sendToAll(newMessage);
                    }
                    else if (request.startsWith("DownloadFile|||")) {
                        String message = request.replace("DownloadFile|||", "");
                        int index = Integer.parseInt(message);

                        InputStream stream = Client.class.getResourceAsStream("/data/" + Server.getFileNames().get(index));

                        InputStreamReader reader = new InputStreamReader(stream);
                        BufferedReader buffered = new BufferedReader(reader);
                        String text = "";
                        String line = buffered.readLine();
                        while(line != null && !line.isBlank())
                        {
                            text += line + "\n";
                            line = buffered.readLine();
                        }

                        out.writeUTF("DownloadFile|||" + Server.getFileNames().get(index) + "+++" + text);
                    }

            }
        } catch (IOException ex) {
            System.err.println("IO Exception!");
        } finally
        {
            try {
                this.in.close();
                this.out.close();
                this.client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void print(String requst) {
        try {
            out.writeUTF(requst);
        } catch (IOException e) {
        }
    }

    public String getUsername()
    {
        return username;
    }
}