package com.ngobrol.ngobrolonlen.Utils;

import android.util.Log;

import java.net.Socket;

public class SocketHandler {
    private static Socket socket;
    public static synchronized Socket getSocket(){
        return socket;
    }
    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
        Log.d("socket masuk", "socket");
    }

}
