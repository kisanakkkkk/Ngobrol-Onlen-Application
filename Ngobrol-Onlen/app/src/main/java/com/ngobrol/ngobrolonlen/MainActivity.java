package com.ngobrol.ngobrolonlen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ngobrol.ngobrolonlen.Utils.SocketHandler;


public class MainActivity extends AppCompatActivity {

    private EditText hostname;
    private EditText port;

    private ConnectTask connect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button chathost = findViewById(R.id.chatButtonHost);
        Button chatclient = findViewById(R.id.chatButtonClient);

        hostname = findViewById(R.id.hostname);
        port = findViewById(R.id.port);


        chathost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                validateAndConnect(1);

            }
        });
        chatclient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                validateAndConnect(2);

            }
        });

    }
    private void validateAndConnect(int mode){
        if(hostname.getText().toString().trim().isEmpty() || port.getText().toString().trim().isEmpty()){
            Toast.makeText(MainActivity.this, "Hostname and port cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(hostname.getText().toString(), port.getText().toString());
        connect = new ConnectTask();
        connect.connectToServer(hostname, port);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SocketHandler.getSocket() != null && SocketHandler.getSocket().isConnected()) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                    Log.d("dari main", Integer.toString(mode));
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to Connect", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000);
    }
}