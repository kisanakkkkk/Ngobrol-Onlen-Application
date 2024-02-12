package com.ngobrol.ngobrolonlen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.ngobrol.ngobrolonlen.Models.Message;
import com.ngobrol.ngobrolonlen.Models.User;
import com.ngobrol.ngobrolonlen.Samples.DialogsFixtures;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;


import com.ngobrol.ngobrolonlen.Models.Dialog;
import com.ngobrol.ngobrolonlen.DemoDialogsActivity;

import java.util.ArrayList;

public class DialogActivity extends DemoDialogsActivity {
    public static void open(Context context) {
        context.startActivity(new Intent(context, DialogActivity.class));
    }

    private DialogsList dialogsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        dialogsList = findViewById(R.id.dialogsList);
        initAdapter();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        Intent receivedIntent = getIntent();
        int mode = receivedIntent.getIntExtra("mode", 0);
        Intent intent = new Intent(DialogActivity.this, ChatActivity.class);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

    private void initAdapter() {
        ArrayList<User> usersx = new ArrayList<>();
        User userx = new User("0", "ayam", "bebs");
        usersx.add(userx);
        Message messagex = new Message("1", userx, "ax");
        Dialog dialogx = new Dialog("0", "bebek", "ayam", usersx, messagex, 0);
        super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
        super.dialogsAdapter.addItem(dialogx);

        super.dialogsAdapter.setOnDialogClickListener(this);
        super.dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(super.dialogsAdapter);
    }

    //for example
    private void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

    //for example
    private void onNewDialog(Dialog dialog) {
        dialogsAdapter.addItem(dialog);
    }
}