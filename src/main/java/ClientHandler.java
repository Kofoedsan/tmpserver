import javax.print.DocFlavor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

class ClientHandler implements Runnable {
    private String userName;
    private String clintID;
    final DataInputStream inputStream;
    final DataOutputStream outputStream;
    Socket socket;
    boolean isloggedin;
    boolean running = true;

    public ClientHandler(Socket socket, String userName, String cliendID, DataInputStream inputStream, DataOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.clintID = cliendID;
        this.userName = userName;
        this.socket = socket;
        this.isloggedin = false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void run() {
        String token;
        try {
            acceptedUser();
            while (running) {
                token = inputStream.readUTF();
                userLogOut(token);
                userChat(token);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void userChat(String token) throws IOException {
        int counter = 0;
        boolean running = true;
        String msg = "";
        String recipient = "";
        while (running) {
            counter = 0;
            StringTokenizer tokenizer = new StringTokenizer(token, "#");
            msg = tokenizer.nextToken();
            recipient = tokenizer.nextToken();
        for (ClientHandler clients : Server.connectionsMade) {
            if (recipient.equalsIgnoreCase(clients.getUserName()) && clients.isloggedin) {
                clients.outputStream.writeUTF(this.userName + ":" + msg);
                outputStream.writeUTF("Message sent ");
                counter++;
                running=false;
            }
        }
        }
        if (counter == 0) {
            outputStream.writeUTF("No user found ");
        }
    }


    private void userLogOut(String token) throws IOException {
        if (token.equalsIgnoreCase("logout")) {
            this.outputStream.writeUTF("Signing out.. ");
            this.isloggedin = false;
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
            this.running = false;
            Thread.currentThread().interrupt();
            return;
        }
    }

    private void acceptedUser() throws IOException {
        String input;
        int counter = 0;
        input = inputStream.readUTF();
        for (User account : Server.accounts) {
            if ((account.getUserName().equalsIgnoreCase(input) && !this.isloggedin)) {
                this.isloggedin = true;
                setUserName(input);
                counter++;
                this.outputStream.writeUTF("Welcome " + userName);
            }
        }
            if (counter == 0) {
                this.outputStream.writeUTF("Signing out.. ");
                this.isloggedin = false;
                this.inputStream.close();
                this.outputStream.close();
                this.socket.close();
                this.running = false;
            }
    }
}
