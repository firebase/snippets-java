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

import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public void getServiceAccountAccessToken() throws IOException {
        // https://firebase.google.com/docs/reference/dynamic-links/analytics#api_authorization
        // [START get_service_account_tokens]
        FileInputStream serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        credentials.refresh();
        String accessToken = credentials.getAccessToken().getTokenValue();
        long expirationTime = credentials.getAccessToken().getExpirationTime().getTime();
        // Attach accessToken to HTTPS request in the
        //   "Authorization: Bearer" header
        // After expirationTime, you must generate a new access
        //   token
        // [END get_service_account_tokens]
    }

    public static void main(String[] args) {
        // write your code here
    }
}
