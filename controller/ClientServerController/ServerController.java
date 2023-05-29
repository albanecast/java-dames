package atelier3.controller.ClientServerController;



import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;



public class ServerController extends Thread{

    private static final int PORT = 8889;
    private static final int CLIENT_MAX = 2;
    private static int clientNbr;

    private final LinkedList<Socket> clients;
    public int playerCount;
    private ServerSocket serverSocket;
    private boolean isRunning;

    public static int getClientMax() {
        return CLIENT_MAX;
    }

    public static int getClientNbr() {
        return clientNbr;
    }

    public LinkedList<Socket> getClients() {
        return clients;
    }

    public ServerController() {
        clientNbr = 0;
        clients = new LinkedList<>();
    }


    private void startServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server sur port : " + PORT);
        this.start();
    }

    void sendPlayerConnected(Socket client) {
        try {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF("Player connected");
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending player connected message: " + e.getMessage());
        }
    }


    public Socket getClient() {
        if (!clients.isEmpty()) {
            return clients.get(0); // Renvoie le premier client connecté
        }
        return null;
    }

    @Override
    public void run() {
        this.isRunning = true;
        System.out.println("en attente de joueurs...");
        while (isRunning) {
            if (clientNbr == CLIENT_MAX) {
                System.out.println("Nombre de joueur maximum atteint");
            }
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            clientNbr++;

            RequestHandler requestHandler = new RequestHandler(socket, this);
            requestHandler.start();  // Lancer le gestionnaire de requêtes dans un thread séparé
        }
    }

    public static void main(String[] args) throws IOException {
        ServerController server = new ServerController();
        server.startServer();
    }



    public void clientAdd(Socket client) {
        clients.add(client);
    }

    public void clientRemove(Socket client) {
        clientNbr--;
        clients.remove(client);
    }


}




