package com.einfoplanet.chatapp.app;

/**
 * Created by bridgeit007 on 15/6/16.
 */

public class EndPoints {
    // localhost url -
    public static final String BASE_URL = "http://192.168.0.121/gcm_chat/v1/login.php";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}
