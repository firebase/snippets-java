/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public void getServiceAccountAccessToken() throws FileNotFoundException, IOException {
        // https://firebase.google.com/docs/reference/dynamic-links/analytics#api_authorization
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
                            // After expirationTime, you must generate a new access
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
