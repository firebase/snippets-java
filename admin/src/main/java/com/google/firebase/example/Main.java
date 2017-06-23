package com.google.firebase.example;

import com.google.firebase.auth.FirebaseCredential;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.auth.GoogleOAuthAccessToken;
import com.google.firebase.internal.NonNull;
import com.google.firebase.tasks.OnCompleteListener;
import com.google.firebase.tasks.Task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public void getServiceAccountAccessToken() throws FileNotFoundException, IOException{
        // [START get_service_account_tokens]
        FileInputStream serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");
        FirebaseCredential credential = FirebaseCredentials.fromCertificate(serviceAccount);
        credential.getAccessToken().addOnCompleteListener(
                new OnCompleteListener<GoogleOAuthAccessToken>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleOAuthAccessToken> task) {
                        if (task.isSuccessful()) {
                            String accessToken = task.getResult().getAccessToken();
                            long expirationTime = task.getResult().getExpiryTime();
                            // Attach accessToken to HTTPS request in the
                            //   "Authorization: Bearer" header
                            // After expirationTime. you must generate a new access
                            //   token
                        } else {
                            // Error
                        }
                    }});
        // [END get_service_account_tokens]
    }

    public static void main(String[] args) {
        // write your code here
    }
}
