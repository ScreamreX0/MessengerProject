package com.example.messengerproject;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Variables {
    // User
    public static FirebaseUser currentUser;
    public static String currentUserPhoneNumber;

    // References
    public static DatabaseReference conversationsReference;
    public static DatabaseReference usersReference;

    // Current user references
    public static DatabaseReference currentUserContactsReference;
    public static DatabaseReference currentUserConversationsReference;
    public static DatabaseReference currentUserReference;
}
