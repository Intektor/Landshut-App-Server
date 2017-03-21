import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.*;

/**
 * @author Intektor
 */
public class Server {

    private static Server server;
    private volatile ServerSocket serverSocket;

    private Logger logger = Logger.getLogger("server.main");

    public static void main(String[] args) throws Exception {
        server = new Server();
        server.startServer();
    }

    private void startServer() throws Exception {
        logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
        logger.addHandler(new FileHandler("log.txt", true));


        new Thread("Network Thread") {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(27206);
                    serverSocket.setSoTimeout(0);

                    while (serverSocket.isBound()) {
                        final Socket socket = serverSocket.accept();
                        new Thread("Socket Thread -> " + socket.getInetAddress()) {
                            @Override
                            public void run() {
                                socket
                            }
                        }.start();
                    }
                } catch (Exception e) {
                    logError(e);
                }
            }
        }.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.nextLine().equals("stop")) {
                System.exit(0);
            }
        }
    }

    public void logError(Throwable throwable) {
        logger.log(Level.WARNING, "Exception!", throwable);
    }
}
