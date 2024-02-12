package com.ngobrol.ngobrolonlen;


import android.os.AsyncTask;

import com.ngobrol.ngobrolonlen.Utils.SocketHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SendTask extends AsyncTask<String, Void, Void> {
    private Socket socket;
    private PrintWriter out;

    @Override
    protected Void doInBackground(String... messages) {
        try {
            socket = SocketHandler.getSocket();
            out = new PrintWriter(socket.getOutputStream(), true);

            if (out != null) {
                out.println(messages[0]);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void sendMessage(final String message) {
        new SendTask().execute(message);
    }


}

