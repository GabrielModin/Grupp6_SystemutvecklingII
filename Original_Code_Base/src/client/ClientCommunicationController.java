package client;

import model.Buffer;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class manages the communication between the Client classes and the Server classes.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class ClientCommunicationController {
    private ClientController clientController;
    private Buffer buffer;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String className = "Class: ClientCommunicationController ";
    private volatile boolean isConnected = true;
    private volatile boolean oisIsNull = true;

    /**
     * Receives a clientController object and then try to connect with the server. Constructs a buffer,
     * ClientSender and a ClientReceiver Object. Then starts two Threads.
     *
     * @param clientController The received ClientController object.
     */
    public ClientCommunicationController(ClientController clientController) {
        this.clientController = clientController;
        connect();
        buffer = new Buffer();
        new ClientSender().start();
        new ClientReceiver().start();
    }

    /**
     * Tries to create a new socket and connect to the server's IP.
     */
    public ClientCommunicationController connect() {
        try {
            socket = new Socket("127.0.0.1", 4343);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * This method tries to close the socket and the connection to the server.
     * @return boolean
     */
    public boolean disconnect() {
        isConnected = false;
        try {
            Thread.sleep(2000);
            socket.close();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        if (socket.isConnected()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method lays an object in a buffer which mission is to be sent to the server.
     *
     * @param object the object to be sent.
     */

    public void sendObject(Object object) {
        if(object instanceof BufferedImage) {
            new ImageSender((BufferedImage) object).start();
        } else {
            buffer.put(object);
        }

    }

    // ClientSender starts a new thread which retrieves an object from a buffer and sends it to the server.
    private class ClientSender extends Thread {

        /**
         * Tries to construct a OutPutStream.
         */
        public ClientSender() {
            try {
                if (oos == null) {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * A thread which retrieves an object from the buffer and then writes it to the stream.
         */
        public void run() {
            while (isConnected) {
                try {
                    Object object = buffer.get();
                    oos.writeUnshared(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientReceiver extends Thread {
        private Object object;

        /**
         * Tries to open an Input Stream then tries to read an object from the stream.
         * Then checks the object's class value and sends it to {@link ClientController}.
         */
        public void run() {
            while (oisIsNull) {
                try {
                    ois = new ObjectInputStream(socket.getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            while (isConnected) {
                try {
                    sleep(2000);
                    object = ois.readObject();
                    /*if (object instanceof Message) {
                        Message message = (Message) object;
                        if(message.getType().equals(MessageType.Login)) {
                            User user = message.getUser();
                            clientController.receiveUser(user);
                        } else if (message.getType().equals(MessageType.Logout)) {
                            disconnect();
                        } else if (message.getType().equals(MessageType.NewActivity)) {
                            Activity activity = (Activity) message.getData();
                            clientController.receiveNotificationFromCCC(activity);
                        }
                    }*///TODO REVERT
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
    }


    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    private class ImageSender extends Thread {
        private BufferedImage image;

        public ImageSender(BufferedImage image) {
            this.image = image;
        }

        /**
         * Sends an image to the server
         */
        public void run() {
            try (Socket socket = new Socket("localhost", 25000)) {
                ImageIO.write(image, "png", socket.getOutputStream());
            } catch (Exception e) {
            }
        }
    }

}