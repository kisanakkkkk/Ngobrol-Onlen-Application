package com.ngobrol.ngobrolonlen.Models;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class Message implements IMessage {

    private String Id;
    private String Text;
    private Date CreatedAt;
    private User mUser;

    public Message(String id, User user, String text) {
        this(id, user, text, new Date());
    }
    public Message(String Id, User user, String Text, Date CreatedAt){
        this.Id = Id;
        this.Text = Text;
        this.CreatedAt = CreatedAt;
        this.mUser = user;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public String getText() {
        return Text;
    }

    @Override
    public IUser getUser() {
        return mUser;
    }

    @Override
    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.CreatedAt = createdAt;
    }
}