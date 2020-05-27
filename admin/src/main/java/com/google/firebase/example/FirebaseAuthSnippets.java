/*
 * Copyright 2020 Google Inc. All Rights Reserved.
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

import com.google.firebase.auth.DeleteUsersResult;
import com.google.firebase.auth.EmailIdentifier;
import com.google.firebase.auth.ErrorInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetUsersResult;
import com.google.firebase.auth.PhoneIdentifier;
import com.google.firebase.auth.ProviderIdentifier;
import com.google.firebase.auth.UidIdentifier;
import com.google.firebase.auth.UserIdentifier;
import com.google.firebase.auth.UserRecord;
import java.util.Arrays;

public class FirebaseAuthSnippets {

  public static void bulkGetUsers() throws Exception {
    // [START bulk_get_users]
    GetUsersResult result = FirebaseAuth.getInstance().getUsersAsync(Arrays.asList(
        new UidIdentifier("uid1"),
        new EmailIdentifier("user2@example.com"),
        new PhoneIdentifier("+15555550003"),
        new ProviderIdentifier("google.com", "google_uid4"))).get();

    System.out.println("Successfully fetched user data:");
    for (UserRecord user : result.getUsers()) {
      System.out.println(user.getUid());
    }

    System.out.println("Unable to find users corresponding to these identifiers:");
    for (UserIdentifier uid : result.getNotFound()) {
      System.out.println(uid);
    }
    // [END bulk_get_users]
  }

  public static void bulkDeleteUsers() throws Exception {
    // [START bulk_delete_users]
    DeleteUsersResult result = FirebaseAuth.getInstance().deleteUsersAsync(
        Arrays.asList("uid1", "uid2", "uid3")).get();

    System.out.println("Successfully deleted " + result.getSuccessCount() + " users");
    System.out.println("Failed to delete " + result.getFailureCount() + " users");
    for (ErrorInfo error : result.getErrors()) {
      System.out.println("error #" + error.getIndex() + ", reason: " + error.getReason());
    }
    // [END bulk_delete_users]
  }
}
