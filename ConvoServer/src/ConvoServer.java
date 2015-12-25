import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ConvoServer extends Application {
  // Text area for displaying contents
  private TextArea ta = new TextArea();
  private String message, disMes;
  // Number a client
  private int clientNo = 0;

  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(ta), 450, 200);
    primaryStage.setTitle("MultiThreadServer"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    message = "testing";

    new Thread( () -> {
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8000);
        System.out.print("MultiThreadServerIn started at " 
          + new Date() + '\n');
    
        while (true) {
          // Listen for a new connection request
          Socket socket = serverSocket.accept();
    
          // Increment clientNo
          clientNo++;
          
          Platform.runLater( () -> {
            // Display the client number
           System.out.print("Starting thread for input " + clientNo +
              " at " + new Date() + '\n');

            // Find the client's host name, and IP address
            InetAddress inetAddress = socket.getInetAddress();
            System.out.print("Input " + clientNo + "'s host name is "
              + inetAddress.getHostName() + "\n");
           System.out.print("Input " + clientNo + "'s IP Address is "
              + inetAddress.getHostAddress() + "\n");
          });
          
          // Create and start a new thread for the connection
          new Thread(new HandleConvo(socket)).start();
        }
      }
      catch(IOException ex) {
        System.err.println(ex);
      }
    }).start();
  }
  
  // Define the thread class for handling new connection
  class HandleConvo implements Runnable {
    private Socket socket; // A connected socket

    /** Construct a thread */
    public HandleConvo(Socket socket) {
      this.socket = socket;
    }

    /** Run a thread */
    @Override
    public void run() {
      try {
        // Create data input and output streams
        DataInputStream inputFromClient = new DataInputStream(
          socket.getInputStream());
        DataOutputStream outputToClient = new DataOutputStream(
          socket.getOutputStream());

        // Continuously serve the client
        while (true) {
              // Receive radius from the client
              message = inputFromClient.readUTF();
              disMes = message;

              // Send area back to the client
              if (!message.equals("")) {
                  outputToClient.writeUTF(message);

                  Platform.runLater(() -> {
                    System.out.print("Message In: "
                              + message + '\n');
                      ta.appendText("Message: " + disMes + '\n');
                      System.out.print("Message Out: "
                              + disMes + '\n');
                      ta.appendText("Message: " + disMes + '\n');
                  });

                  message = "";
              }
        }
      }
      catch(IOException e) {
        //ex.printStackTrace();
      }
    }
  }
  
  
    // Define the thread class for handling new connection
  class HandleOutput implements Runnable {
    private Socket socket; // A connected socket

    /** Construct a thread */
    public HandleOutput(Socket socket) {
      this.socket = socket;
    }

    /** Run a thread */
    @Override
    public void run() {
      try {
        // Create data input and output streams
        DataOutputStream outputToClient = new DataOutputStream(
          socket.getOutputStream());

        // Continuously serve the client
        while (true) {

            // Send area back to the client
           if(!message.equals("")){
              outputToClient.writeUTF(message);         

            Platform.runLater(() -> {
             System.out.print("Message Out: " +
                message + '\n');
             ta.appendText("Message: " + message + '\n');
            });
            
            message = "";
           }
        }
      }
      catch(IOException e) {
        //ex.printStackTrace();
      }
    }
  }
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
