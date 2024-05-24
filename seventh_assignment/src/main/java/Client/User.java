package Client;

public class User
{
    private String username;
    private String userColor;
    public String getUsername()
    {
        return username;
    }
    public String getUserColor()
    {
        return userColor;
    }

    public User(String username, String userColor)
    {
        this.username = username;
        this.userColor = userColor;
    }
}