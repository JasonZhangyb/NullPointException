package cs591e1_sp19.eatogether;

public class AppState {

    // The one and only instance of AppState singleton class.
    private static AppState appStateInstance;

    // Constants

    //Uber API login
    private static  final  String UBER_CLIENT_ID = "mzKxZx-zIYaBg2uGJjVCFq8dGS8C847H";
    private static  final  String UBER_CLIENT_SECRET = "PVHMJnO4zxF7g6x6AjUcdAZhYZhAetTWdjUAjdby";
    private static  final  String UBER_SEVER_TOKEN = "6rDZtxnzZIVgLFSGjjzLzD8IZtYUzbxPurcBJr4T";
    private static  final  String UBER_REDIRECT_URI = "https://login.uber.com/oauth/v2/authorize?response_type=code&client_id=mzKxZx-zIYaBg2uGJjVCFq8dGS8C847H&redirect_uri=http://localhost:3000/";

    //LYFT API LOGIN
    private static final String LYFT_CLIENT_ID = "iTF26WFfdvLA";
    private static final String LYFT_CLIENT_TOKEN = "21llDWWKvmtY51o4oa6YxqJ65GFWAr0AQKMx0GeUBvKlqGTalzY9zjHMtKjDLgOqFKZH1xBuXaZsi6xVoz9agnksvN1DIzzzXkN9J5uzVDxy/InNbILqBB8=";
    private static final String LYFT_CLIENT_SECRET = "kgiVUELlirL_jBhlJ_yNjNzdi6S9_D5X";

    // Private constructor to enforce singleton.
    private AppState() {
        // Prevent unexpected instantiation from reflection.
        if (appStateInstance != null) {
            throw new RuntimeException("AppStateInstance in AppState is not available for reflection");
        }
    }

    // Returns the running instance of our AppState singleton.
    private static AppState getInstance() {
        if (appStateInstance == null) {
            appStateInstance = new AppState();
        }

        return appStateInstance;
    }

    /*
    *   Utility functions of our AppState singleton class below
    * */
}
