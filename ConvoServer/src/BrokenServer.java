
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.util.Queue;
import static javafx.application.Application.launch;

public class BrokenServer extends Application {

    // Text area for displaying contents

    private TextArea ta = new TextArea();
    private String message, disMes;
    // Number a client
    private int clientNo = 0;
    private Queue<Socket> sockQ;
    private ServerSocket serverSocket;

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("MultiThreadServer"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        sockQ = new LinkedList<>();

        // Create and start a new thread for the connection
        new Thread(new HandleSockets()).start();
        new Thread(new FindConvo()).start();
       
    }

    // Define the thread class for handling new connection
    class HandleSockets implements Runnable {
        //private Socket socket; // A connected socket

        /**
         * Construct a thread
         */
        public HandleSockets() {

        }

        /**
         * Run a thread
         */
        @Override
        public void run() {

            try {

                serverSocket = new ServerSocket(8000);
                System.out.print("MultiThreadServerIn started at "
                        + new Date() + '\n');

                while (true) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();
                    addToQ(socket);

                    // Increment clientNo
                    clientNo++;

                    Platform.runLater(() -> {
                        // Display the client number
                        System.out.print("Starting thread for input " + clientNo
                                + " at " + new Date() + '\n');

                        // Find the client's host name, and IP address
                        InetAddress inetAddress = socket.getInetAddress();
                        System.out.print("Input " + clientNo + "'s host name is "
                                + inetAddress.getHostName() + "\n");
                        System.out.print("Input " + clientNo + "'s IP Address is "
                                + inetAddress.getHostAddress() + "\n");
                    });
                }
            } catch (IOException e) {
                //ex.printStackTrace();
            }
        }
    }

    class FindConvo implements Runnable {
        //private Socket socket; // A connected socket

        /**
         * Construct a thread
         */
        public FindConvo() {

        }

        /**
         * Run a thread
         */
        @Override
        public void run() {

            Socket temp1, temp2;
                    
            while (true) {

                // Listen for a new connection request
                //System.out.println("\nQueue size: " + sockQ.getQueueSize());
                if (sockQ.size() > 0) {
                    System.out.println("Popping temp1");
                   temp1 = sockQ.poll();
                    
                   temp2 = sockQ.poll();
                   System.out.println("Popping temp2");
                    new Thread(new HandleConvo(temp1, temp2)).start();
               }
                
            }
        }
    }

    class HandleConvo implements Runnable {

        private Socket socket1, socket2; // A connected socket
        private String message;

        /**
         * Construct a thread
         */
        public HandleConvo(Socket socket1, Socket socket2) {
            this.socket1 = socket1;
            this.socket2 = socket2;
        }

        /**
         * Run a thread
         */
        @Override
        public void run() {

            try {
                // Create data input and output streams
                DataInputStream inputFromClient1 = new DataInputStream(
                        socket1.getInputStream());
                DataOutputStream outputToClient1 = new DataOutputStream(
                        socket1.getOutputStream());
                DataInputStream inputFromClient2 = new DataInputStream(
                        socket2.getInputStream());
                DataOutputStream outputToClient2 = new DataOutputStream(
                        socket2.getOutputStream());
                // Continuously serve the client
                while (true) {
                                System.out.print("\nWheeee");
                    // Receive radius from the client
                    message = inputFromClient1.readUTF();
                    //message = inputFromClient2.readUTF();
                    disMes = message;

                    // Send area back to the client
                    if (!message.equals("")) {
                        outputToClient1.writeUTF(message);
                        outputToClient2.writeUTF(message);

                        Platform.runLater(() -> {
                            System.out.print("Message In: "
                                    + disMes + '\n');
                            ta.appendText("Message: " + disMes + '\n');
                            System.out.print("Message Out: "
                                    + disMes + '\n');
                            ta.appendText("Message: " + disMes + '\n');
                        });

                        message = "";
                    }
                }
            } catch (IOException e) {
                //ex.printStackTrace();
            }
        }
    }
    
    public void addToQ(Socket socket)
    {
        sockQ.add(socket);
    }
    
    
    /**
     * The main method is only needed for the IDE with limited JavaFX support.
     * Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
