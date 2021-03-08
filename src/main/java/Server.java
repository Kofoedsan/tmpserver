import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private int i = 0;

    static List<User> accounts = new ArrayList<>();
    static List<ClientHandler> connectionsMade = new ArrayList<>();
    int serverPort;
    ExecutorService executorService = Executors.newFixedThreadPool(10);


    public static void main(String[] args) throws IOException {
        accounts.add(new User("Kofoed", 1));
        accounts.add(new User("Kofoeden", 2));
        accounts.add(new User("Kofoedsan", 3));
        Thread startServer = new Thread(new Server(5555));
        startServer.start();
    }

    public Server(int port) {
        this.serverPort = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(serverPort);
            while (true) {
                socket = server.accept();
                System.out.println("New client request received : " + socket);
                inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                outputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("Creating a new handler for this client...");
                ClientHandler loginInCheck = new ClientHandler(socket, "tmp","client " + i, inputStream, outputStream);


                System.out.println("Adding this client to attempted connections ");


                executorService.execute(loginInCheck);
                connectionsMade.add(loginInCheck);
                i++;
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
