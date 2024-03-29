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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.remoteconfig.Condition;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.ListVersionsPage;
import com.google.firebase.remoteconfig.Parameter;
import com.google.firebase.remoteconfig.ParameterGroup;
import com.google.firebase.remoteconfig.ParameterValue;
import com.google.firebase.remoteconfig.TagColor;
import com.google.firebase.remoteconfig.Template;
import com.google.firebase.remoteconfig.Version;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Remote Config snippets for documentation.
 *
 * See:
 * https://firebase.google.com/docs/remote-config/automate-rc
 */
public class FirebaseRemoteConfigSnippets {

  private final static String PROJECT_ID = "PROJECT_ID";
  private final static String BASE_URL = "https://firebaseremoteconfig.googleapis.com";
  private final static String REMOTE_CONFIG_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/remoteConfig";
  private final static String[] SCOPES = { "https://www.googleapis.com/auth/firebase.remoteconfig" };

  //Get the current Remote Config Template
  public static Template getRemoteConfig() throws ExecutionException, InterruptedException {
    // [START get_rc_template]
    Template template = FirebaseRemoteConfig.getInstance().getTemplateAsync().get();
    // See the ETag of the fetched template.
    System.out.println("ETag from server: " + template.getETag());
    // [END get_rc_template]
    return template;
  }

  // Modify Remote Config parameters
  public static void addParameterToGroup(Template template) {
    // [START add_rc_parameter]
    template.getParameterGroups().get("new_menu").getParameters()
            .put("spring_season", new Parameter()
                    .setDefaultValue(ParameterValue.inAppDefault())
                    .setDescription("spring season menu visibility.")
            );
    // [END add_rc_parameter]
  }

  // Modify Remote Config conditions
  public static void addNewCondition(Template template) {
    // [START add_rc_condition]
    template.getConditions().add(new Condition("android_en",
            "device.os == 'android' && device.country in ['us', 'uk']", TagColor.BLUE));
    // [END add_rc_condition]
  }

  // Validate the Remote Config template
  public static void validateTemplate(Template template) throws InterruptedException {
    // [START validate_rc_template]
    try {
      Template validatedTemplate = FirebaseRemoteConfig.getInstance()
              .validateTemplateAsync(template).get();
      System.out.println("Template was valid and safe to use");
    } catch (ExecutionException e) {
      if (e.getCause() instanceof FirebaseRemoteConfigException) {
        FirebaseRemoteConfigException rcError = (FirebaseRemoteConfigException) e.getCause();
        System.out.println("Template is invalid and cannot be published");
        System.out.println(rcError.getMessage());
      }
    }
    // [END validate_rc_template]
  }

  // Publish the Remote Config template
  public static void publishTemplate(Template template) throws InterruptedException {
    // [START publish_rc_template]
    try {
      Template publishedTemplate = FirebaseRemoteConfig.getInstance()
              .publishTemplateAsync(template).get();
      System.out.println("Template has been published");
      // See the ETag of the published template.
      System.out.println("ETag from server: " + publishedTemplate.getETag());
    } catch (ExecutionException e) {
      if (e.getCause() instanceof FirebaseRemoteConfigException) {
        FirebaseRemoteConfigException rcError = (FirebaseRemoteConfigException) e.getCause();
        System.out.println("Unable to publish template.");
        System.out.println(rcError.getMessage());
      }
    }
    // [END publish_rc_template]
  }

  /**
   * Remote Config snippets for Manage Remote Config template versions documentation.
   *
   * See:
   * https://firebase.google.com/docs/remote-config/templates
   */
  // List all stored versions of the Remote Config template
  public static void listAllVersions() throws InterruptedException, ExecutionException {
    // [START list_all_versions]
    ListVersionsPage page = FirebaseRemoteConfig.getInstance().listVersionsAsync().get();
    while (page != null) {
      for (Version version : page.getValues()) {
        System.out.println("Version: " + version.getVersionNumber());
      }
      page = page.getNextPage();
    }

    // Iterate through all versions. This will still retrieve versions in batches.
    page = FirebaseRemoteConfig.getInstance().listVersionsAsync().get();
    for (Version version : page.iterateAll()) {
      System.out.println("Version: " + version.getVersionNumber());
    }
    // [END list_all_versions]
  }

  // Retrieve a specific version of the Remote Config template
  public static void getRemoteConfigAtVersion(long versionNumber) throws ExecutionException, InterruptedException {
    // [START get_rc_template_at_version]
    Template template = FirebaseRemoteConfig.getInstance().getTemplateAtVersionAsync(versionNumber).get();
    // See the ETag of the fetched template.
    System.out.println("Successfully fetched the template with ETag: " + template.getETag());
    // [END get_rc_template_at_version]
  }

  // Roll back to a specific stored version of the Remote Config template
  public static void rollbackToVersion(long versionNumber) throws InterruptedException {
    // [START rollback_rc_template]
    try {
      Template template = FirebaseRemoteConfig.getInstance().rollbackAsync(versionNumber).get();
      System.out.println("Successfully rolled back to template version: " + versionNumber);
      System.out.println("New ETag: " + template.getETag());
    } catch (ExecutionException e) {
      if (e.getCause() instanceof FirebaseRemoteConfigException) {
        FirebaseRemoteConfigException rcError = (FirebaseRemoteConfigException) e.getCause();
        System.out.println("Error trying to rollback template.");
        System.out.println(rcError.getMessage());
      }
    }
    // [END rollback_rc_template]
  }

  /**
   * Retrieve a valid access token that can be used to authorize requests to the Remote Config REST
   * API.
   *
   * @return Access token.
   * @throws IOException
   */
  // [START retrieve_access_token]
  public static String getAccessToken() throws IOException {
    GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(new FileInputStream("service-account.json"))
            .createScoped(Arrays.asList(SCOPES));
    googleCredentials.refreshAccessToken();
    return googleCredentials.getAccessToken().getTokenValue();
  }
  // [END retrieve_access_token]

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    System.out.println("Hello, FirebaseRemoteConfigSnippets!");

    // Initialize Firebase
    try {
      // [START initialize]
      FileInputStream serviceAccount = new FileInputStream("service-account.json");
      FirebaseOptions options = FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();
      FirebaseApp.initializeApp(options);
      // [END initialize]
    } catch (IOException e) {
      System.out.println("ERROR: invalid service account credentials. See README.");
      System.out.println(e.getMessage());

      System.exit(1);
    }

    // Smoke test
    Template template = getRemoteConfig();
    template.getParameterGroups().put("new_menu", new ParameterGroup());
    addParameterToGroup(template);
    addNewCondition(template);
    validateTemplate(template);
    publishTemplate(template);
    listAllVersions();
    getRemoteConfigAtVersion(6);
    rollbackToVersion(6);
    System.out.println("Done!");
  }

}
