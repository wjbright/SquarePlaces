package com.squareplaces.squareplaces;

/**
 * Created by SQUARE PLACES on 9/19/2017.
 */

public class ConversationModel {
    private String user_response;

    public ConversationModel(String response) {
        this.user_response = response;
    }

    public ConversationModel() {

    }

    public String getUser_response() {
        return user_response;
    }

    public void setUser_response(String user_response) {
        this.user_response = user_response;
    }
}
