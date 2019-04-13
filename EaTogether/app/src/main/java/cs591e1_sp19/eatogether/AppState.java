package cs591e1_sp19.eatogether;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppState {

    // The one and only instance of AppState singleton class.
    protected static AppState appStateInstance = new AppState();

    // Constants
    protected static final String USER_DATABASE = "Users";
    protected static final String CHAT_DATABASE = "Chats";

    // References to Databases
    protected static final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase
            .getInstance()
            .getReference();


    // Variables
    public static boolean isLoggedIn;
    public static String userID;

    public static String otherChatUserId;
    public static String otherChatUserAvatar;

    public static DatabaseReference userDatabase;
    public static DatabaseReference chatDatabase;

    // Private constructor to enforce singleton.
    private AppState() {
        isLoggedIn = false;
        userID = null;

        otherChatUserId = null;
        otherChatUserAvatar = null;
    }

    // Returns the running instance of our AppState singleton.
    protected static AppState getInstance() {
        return appStateInstance;
    }

    /*
    *   Utility functions of our AppState singleton class below
    * */

    // Returns a DatabaseReference to the given table
    protected static DatabaseReference getDatabaseReference(String databaseName) {
        return DATABASE_REFERENCE.child(databaseName);
    }
}
