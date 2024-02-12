package com.ngobrol.ngobrolonlen;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.ngobrol.ngobrolonlen.Utils.SocketHandler;

import java.io.IOException;
import java.net.Socket;

public class ConnectTask extends AsyncTask<Void, Void, Void> {
    private static EditText hostname;

    private static EditText port;

    private Socket socket;


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String hostnameString = hostname.getText().toString();
            Integer portName = Integer.parseInt(port.getText().toString());

            Log.d("hostname", hostnameString);
            Log.d("port", String.valueOf(portName));

            socket = new Socket(hostnameString, portName);
            if(socket != null && socket.isConnected()) {
                Log.d("socket", "notnull");
                SocketHandler.setSocket(socket);
            }

        } catch (IOException e) {
            Log.d("apakah ke sini", "sini");
            e.printStackTrace();
        }
        return null;
    }

    public void connectToServer(EditText hostname, EditText port) {
        this.hostname = hostname;
        this.port = port;
        Log.d(hostname.getText().toString(), port.getText().toString());
        new ConnectTask().execute();
    }

}
