package com.ngobrol.ngobrolonlen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.Settings.Secure;

import com.ngobrol.ngobrolonlen.EncryptionUtils.AccKeyExchange;
import com.ngobrol.ngobrolonlen.EncryptionUtils.Encoding;
import com.ngobrol.ngobrolonlen.EncryptionUtils.KeyExchange;
import com.ngobrol.ngobrolonlen.EncryptionUtils.Utils;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import com.ngobrol.ngobrolonlen.Models.Message;
import com.ngobrol.ngobrolonlen.Models.User;
import com.ngobrol.ngobrolonlen.Utils.SocketHandler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ChatActivity extends AppCompatActivity implements KeyExchange.SendKey, AccKeyExchange.SendKey {
    private MessagesList mMessagesList;
    private MessageInput mMessageInput;
    private MessagesListAdapter<Message> sentMessageAdapter;

    private ImageLoader mImageLoader;
    private RecyclerView mRecyclerView;

    private Socket socket;
    private ReceiveThread receive;




    private KeyExchange keyExchange;
    private AccKeyExchange acckeyExchange;
    SecretKeySpec keySpec;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        mMessagesList = new MessagesList(this);
        mMessagesList = findViewById(R.id.messagesList);
        mMessageInput = findViewById(R.id.message_input);

        mRecyclerView = findViewById(R.id.suggestionRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        mRecyclerView.setVisibility(View.INVISIBLE);

        sentMessageAdapter = new MessagesListAdapter<>("John", mImageLoader);
        mMessagesList.setAdapter(sentMessageAdapter);

        String tmDevice = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);

        Intent receivedIntent = getIntent();
        int mode = receivedIntent.getIntExtra("mode", 0);
        Log.d("udah chat", Integer.toString(mode));
        init(mode);

        mMessageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {

                if(!input.toString().trim().isEmpty()){
                    Date date = Calendar.getInstance().getTime();

                    User user = new User("John", "df", null);
                    String message = input.toString();
                    Log.d("message", "tes message chatact kirim" + message);
                    String encryptedMessage = cobaEncrypt(message);
                    Log.d("message", "ini encrypted kirim" + encryptedMessage);
                    SendTask.sendMessage(encryptedMessage); //INI NGIRIM
                    Message message0 = new Message("Will", user, message, date);

                    sentMessageAdapter.addToStart(message0, true);//INI SEND KE UI
                } else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }


                return true;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initKeyExchange(){
        keyExchange = new KeyExchange(this);
        keyExchange.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void receiveKeyExchange(){

        acckeyExchange = new AccKeyExchange(this);
        acckeyExchange.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public String cobaEncrypt(String message){
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        try {
            return Utils.encrypt(iv, message, keySpec);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "gagal wak";
    }

    public String cobaDecrypt(String message){
        Log.d("inimessage", message);
        String iv = message.substring(0, 32);
        String messagex = message.substring(32);
        byte[] ivx = Utils.hexStringToByteArray(iv);
        try {
            return Utils.decrypt(ivx, messagex, keySpec);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "gagal wak";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            disconnectSocket();
            Log.d("ayam", "kodok");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void init(int mode){
        socket = SocketHandler.getSocket();

        if(socket == null){
            Log.d("null", "coi");
        }

        if(mode == 1){
            Log.d("masuk init", "coy");
            initKeyExchange();
        } //penting jika app meng init
        else if (mode == 2) {
            Log.d("masuk receive", "coy");
            receiveKeyExchange();
        }
        else{
            Log.d("loh", Integer.toString(mode));
        }


    }
    private void processMessage(String message){
        User user = new User("Paul", "df", null);
        Date date = Calendar.getInstance().getTime();
        Message message1 = new Message("Doe", user, message, date);

        sentMessageAdapter.addToStart(message1, true);//INI RECV KE UI
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void openDialogPublicKey() {

    }

    @Override
    public void closeDialogPublicKey() {

    }

    @Override
    public void openDialogKeyPair() {

    }

    @Override
    public void closeDialogKeyPair() {

    }

    @Override
    public void sendParam(DHParameterSpec DHParam) {
        String formatPBK = Utils.byteArrayToHexString(("{\"p\":\"" + DHParam.getP().toString() + "\",\"g\":\"" + DHParam.getG().toString() + "\"}").getBytes());
        Log.d("paramnya", formatPBK);

        Log.d("hexedparam", formatPBK);
        try {
            SendTask.sendMessage(formatPBK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendPublicKey(PublicKey publicKey) {
        byte[] encodedPublicKey = publicKey.getEncoded();

        String base64PublicKey = Encoding.base64Encode(encodedPublicKey);
        String formatPBK = Utils.byteArrayToHexString(("PUB" + "@@" + base64PublicKey).getBytes());
        Log.d("publickeynya", formatPBK);

        Log.d("hexed", formatPBK);
        try {
            SendTask.sendMessage(formatPBK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String recvParam() {
        Log.d("masuk", "recvpublic");
        StringBuilder cobaBuilder = new StringBuilder();
        String response;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String coba = in.readLine();
            cobaBuilder.append(coba);

//            while ((coba = in.readLine()) != null) {
//                Log.d("masuk ga sih", coba);
//                // Read and display incoming messages
//                cobaBuilder.append(coba);
//
//            }
//            Log.d("apakah diterima stelah loop", cobaBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("cobabuil", cobaBuilder.toString());
        String diterima = Utils.byteArrayToString(Utils.hexStringToByteArray(cobaBuilder.toString()));
        Log.d("apakah diterima", diterima);

        return diterima;
    }
    @Override
    public byte[] recvPublicKey() {
        Log.d("masuk", "recvpublicss");
        StringBuilder cobaBuilder = new StringBuilder();
        String response;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String coba = in.readLine();
            cobaBuilder.append(coba);

//            while ((coba = in.readLine()) != null) {
//                Log.d("masuk ga sih", coba);
//                // Read and display incoming messages
//                cobaBuilder.append(coba);
////
//            }
//            Log.d("apakah diterima stelah loop", cobaBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("cobabuil", cobaBuilder.toString());
        String diterima = Utils.byteArrayToString(Utils.hexStringToByteArray(cobaBuilder.toString()));
        Log.d("apakah diterima", diterima);
        response = diterima;
        Log.d("kepotong geming", response.substring(0, 5));
        receive = new ReceiveThread();
        receive.start();
        if (response.substring(0, 5).equals("PUB@@")) {
            String base64Data = response.substring(5);
            // Decode the base64 data
            Log.d("hasil base64", base64Data);
            byte[] decodedData = Encoding.base64Decode(base64Data);

            return decodedData;
        }
        return null;
    }

    @Override
    public void sendError(String error) {

    }

    @Override
    public void assignPrivateKey(SecretKeySpec s) {
        Log.d("showprivchatact", Encoding.base64Encode(s.getEncoded()));
        keySpec = s;
    }

    @Override
    public void finishExchange() {
        Toast.makeText(ChatActivity.this, "Key Exchange finished", Toast.LENGTH_SHORT).show();
    }
    public class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receivedMessage;
                while ((receivedMessage = in.readLine()) != null) {
                    // Read and display incoming messages
                    if(receivedMessage != null && !receivedMessage.isEmpty()) {
                        final String decryptedMessage = cobaDecrypt(receivedMessage);//INI TERIMA
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processMessage(decryptedMessage);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Handle back button press
        disconnectSocket();
        super.onBackPressed();
    }


    private void disconnectSocket() {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}