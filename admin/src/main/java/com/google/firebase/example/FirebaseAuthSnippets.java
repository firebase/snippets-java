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

import java.util.List;
import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProvider;
import com.google.firebase.auth.UserRecord;

public class FirebaseAuthSnippets {

  public void getUserByFederatedId() throws Exception {
    // [START get_user_by_federated_id]
    UserRecord user = FirebaseAuth.getInstance().getUserByFederatedIdAsync("google.com", "google_uid1234").get();
    System.out.println("Successfully fetched user data: " + user.getUid());
    // [END get_user_by_federated_id]
  }

  public void updateUserLinkFederated() throws Exception {
    String uid = "someuid1234";
    // [START update_user_link_federated]
    // Link the user with a federated identity provider (like Google).
    UserRecord.UpdateRequest update = new UserRecord.UpdateRequest(uid)
        .setLinkProvider(
              UserProvider.builder()
                  .setProviderId("google.com")
                  .setUid("google_uid1234")
                  .build());

    UserRecord user = FirebaseAuth.getInstance().updateUserAsync(update).get();
    System.out.println("Successfully updated data: " + user.getUid());
    // [END update_user_link_federated]
  }

  public void updateUserUnlinkFederated() throws Exception {
    String uid = "someuid1234";
    // [START update_user_unlink_federated]
    // Unlink the user from a federated identity provider (like Google).
    List<String> providers = new ArrayList<String>();
    providers.add("google.com");

    UserRecord.UpdateRequest update = new UserRecord.UpdateRequest(uid)
        .setDeleteProviders(providers);

    UserRecord user = FirebaseAuth.getInstance().updateUserAsync(update).get();
    System.out.println("Successfully updated data: " + user.getUid());
    // [END update_user_unlink_federated]
  }
}