package de.intektor.landshut_app_server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.*;

/**
 * @author Intektor
 */
public class Server {

    private static Server server;
    private volatile ServerSocket serverSocket;

    private List<BuildingInformation> buildingList = Collections.synchronizedList(new ArrayList<BuildingInformation>());
    private volatile String currentVersion;

    private Logger logger = Logger.getLogger("server.main");

    public static void main(String[] args) throws Exception {
        server = new Server();
        server.startServer();
    }

    private void startServer() throws Exception {
        logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
        logger.addHandler(new FileHandler("log.txt", true));

        reloadResources();

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
                                try {
                                    DataInputStream in = new DataInputStream(socket.getInputStream());
                                    String currentVersion = in.readUTF();
                                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                    boolean newVersionAvailable = currentVersion.equals(Server.this.currentVersion);
                                    out.writeBoolean(newVersionAvailable);
                                    if (!newVersionAvailable) {
                                        out.writeInt(buildingList.size());
                                        for (BuildingInformation bInfo : buildingList) {
                                            bInfo.writeToStream(out);
                                        }
                                    }
                                } catch (Exception e) {
                                    logError(e);
                                }
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
            String nextLine = scanner.nextLine();
            if (nextLine.equals("stop")) {
                System.exit(0);
            } else if (nextLine.equals("reload")) {
                reloadResources();
                logger.info("Reloaded resources!");
            }
        }
    }

    private void reloadResources() throws IOException {
        Scanner scanner = new Scanner(new FileInputStream("version.txt"));
        currentVersion = scanner.nextLine();
        scanner.close();
        buildingList.clear();
        File file = new File("buildings");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File file1 : files) {
                if (file1.getName().endsWith(".building")) {
                    scanner = new Scanner(new FileInputStream(file1));
                    String name = scanner.nextLine();
                    String http = scanner.nextLine();
                    BufferedImage htmlPicture = ImageIO.read(new File("buildings/htmlpictures/" + http + ".png"));
                    String imageName = scanner.nextLine();
                    BufferedImage image = ImageIO.read(new File("images/" + imageName + ".png"));
                    buildingList.add(new BuildingInformation(name, htmlPicture, image));
                    scanner.close();
                }
            }
        }
    }

    public void logError(Throwable throwable) {
        logger.log(Level.WARNING, "Exception!", throwable);
    }
}
