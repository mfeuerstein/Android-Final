package com.example.michael.finalproto;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity
        implements OnEditorActionListener {

    private AutoCompleteTextView playerChatBox;
    private TextView chatArea;
    private String chatAreaString, playerChatString;
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;
    Socket clientWrite, clientRead;
    private PrintWriter printwriter;
    private InputStreamReader reader;
    private static BufferedReader bufferedReader;
    private String message, name;
    private String textFromSever;
    String ipA = "209.6.128.75";
    private Thread output;
    private Handler handler;
    private ClientServer clientServer;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*try {
            // Create a socket to connect to the server
            client = new Socket(ipA, 8000);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(client.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(client.getOutputStream());
        }
        catch (IOException ex) {
            //
        }*/


        playerChatBox = (AutoCompleteTextView) findViewById(R.id.userChatBox);
        chatArea = (TextView) findViewById(R.id.chatArea);
        chatArea.setMovementMethod(new ScrollingMovementMethod());
        name = "Mike";
        chatAreaString = "";

        //new Thread().start();

        playerChatBox.setOnEditorActionListener(this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        playerChatString = playerChatBox.getText().toString();

        if (!playerChatString.equals("")) {


            message = "\n" + name + ":   " + playerChatBox.getText().toString();
            clientServer.newMessage();
            playerChatBox.setText("");
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.michael.finalproto/http/host/path")
        );


        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (!textFromSever.equals("")) {
                    chatArea.append(textFromSever);
                    textFromSever = "";
                }
            }
        };

        clientServer = new ClientServer();
        new Thread(clientServer).start();
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.michael.finalproto/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // Define the thread class for handling new connection
    class ClientServer implements Runnable {
        private Socket socket;
        private DataOutputStream outputToServer;
        private DataInputStream inputFromServer;

        /** Construct a thread */
        public ClientServer() {

        }

        /** Run a thread */
        @Override
        public void run() {
            try {
                try
                {
                    socket = new Socket(ipA, 8000); // connect to the server

                    inputFromServer = new DataInputStream(
                            socket.getInputStream());
                    outputToServer = new DataOutputStream(
                            socket.getOutputStream());
                }

                catch (IOException e) {
                    e.printStackTrace();
                }
                // Continuously check for new messages
                while (true) {

                    textFromSever = inputFromServer.readUTF();
                    if(!textFromSever.equals(""))
                        handler.sendEmptyMessage(0);
                }
            }
            catch(IOException e) {
                //ex.printStackTrace();
            }
        }

        void newMessage()
        {
            Messenger msg = new Messenger();
            new Thread(msg).start();
        }

        class Messenger implements Runnable {
            public Messenger() {

            }
            @Override
            public void run() {
                try {
                    outputToServer.writeUTF(message);
                    Log.i("Server Message", "Message: " + message);
                    //clientWrite.close(); // closing the connection
                }
                catch(IOException e) {
                    //ex.printStackTrace();
                }
            }
        }
    }
}

