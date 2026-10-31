/*
 * Copyright 2022 Google LLC. All Rights Reserved.
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

import static com.google.cloud.firestore.pipeline.expressions.AggregateFunction.average;
import static com.google.cloud.firestore.pipeline.expressions.AggregateFunction.countAll;
import static com.google.cloud.firestore.pipeline.expressions.AggregateFunction.maximum;
import static com.google.cloud.firestore.pipeline.expressions.AggregateFunction.sum;
import static com.google.cloud.firestore.pipeline.expressions.Expression.constant;
import static com.google.cloud.firestore.pipeline.expressions.Expression.currentTimestamp;
import static com.google.cloud.firestore.pipeline.expressions.Expression.equal;
import static com.google.cloud.firestore.pipeline.expressions.Expression.exists;
import static com.google.cloud.firestore.pipeline.expressions.Expression.field;
import static com.google.cloud.firestore.pipeline.expressions.Expression.greaterThanOrEqual;
import static com.google.cloud.firestore.pipeline.expressions.Expression.not;
import static com.google.cloud.firestore.pipeline.expressions.Expression.nullValue;
import static com.google.cloud.firestore.pipeline.expressions.Expression.stringConcat;
import static com.google.cloud.firestore.pipeline.expressions.Expression.variable;

import com.google.cloud.firestore.AggregateField;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Pipeline;
import com.google.cloud.firestore.PipelineSource;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.pipeline.stages.Aggregate;
import com.google.cloud.firestore.pipeline.stages.Insert;
import com.google.cloud.firestore.pipeline.stages.PipelineExecuteOptions;
import com.google.cloud.firestore.pipeline.stages.Search;
import com.google.cloud.firestore.pipeline.stages.Upsert;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseFirestoreSnippets {

    public static void countAggregateCollection(Firestore db) throws ExecutionException, InterruptedException {
        // [START count_aggregate_collection]
        Query query = db.collection("cities");
        AggregateQuerySnapshot snapshot = query.count().get().get();
        System.out.println("Count: " + snapshot.get(AggregateField.count()));
        // [END count_aggregate_collection]
    }

    public static void countAggregateQuery(Firestore db) throws ExecutionException, InterruptedException {
        // [START count_aggregate_query]
        Query query = db.collection("cities").whereEqualTo("state", "CA");
        AggregateQuerySnapshot snapshot = query.count().get().get();
        System.out.println("Count: " + snapshot.get(AggregateField.count()));
        // [END count_aggregate_query]
    }

    public static void collectionStageExamples(Firestore db) throws ExecutionException, InterruptedException {
        // [START collection_example]
        Pipeline.Snapshot results = db.pipeline()
                .collection("users/bob/games")
                .sort(field("name").ascending())
                .execute()
                .get();
        // [END collection_example]
        System.out.println(results);

        // [START collection_input_data]
        db.collection("cities").document("SF").set(Map.of(
                "name", "San Francsico",
                "state", "California")).get();
        db.collection("cities").document("NYC").set(Map.of(
                "name", "New York City",
                "state", "New York")).get();
        db.collection("cities").document("CHI").set(Map.of(
                "name", "Chicago",
                "state", "Illinois")).get();
        db.collection("states").document("CA").set(Map.of(
                "name", "California")).get();
        // [END collection_input_data]

        // [START collection_input]
        Pipeline.Snapshot citiesResults = db.pipeline()
                .collection("cities")
                .sort(field("name").ascending())
                .execute()
                .get();
        // [END collection_input]
        System.out.println(citiesResults);

        // [START subcollection_input_data]
        db.collection("cities/SF/departments").document("building").set(Map.of(
                "name", "SF Building Deparment",
                "employees", 750)).get();
        db.collection("cities/NY/departments").document("building").set(Map.of(
                "name", "NY Building Deparment",
                "employees", 1000)).get();
        db.collection("cities/CHI/departments").document("building").set(Map.of(
                "name", "CHI Building Deparment",
                "employees", 900)).get();
        db.collection("cities/NY/departments").document("finance").set(Map.of(
                "name", "NY Finance Deparment",
                "employees", 1200)).get();
        // [END subcollection_input_data]

        // [START subcollection_input]
        Pipeline.Snapshot deptResults = db.pipeline()
                .collection("cities/NY/departments")
                .sort(field("employees").ascending())
                .execute()
                .get();
        // [END subcollection_input]
        System.out.println(deptResults);
    }

    public static void aggregateStageExamples(Firestore db) throws ExecutionException, InterruptedException {
        // [START aggregate_syntax]
        Pipeline.Snapshot syntaxResult = db.pipeline()
                .collection("cities")
                .aggregate(
                        countAll().as("total"),
                        average("population").as("averagePopulation"))
                .execute()
                .get();
        // [END aggregate_syntax]
        System.out.println(syntaxResult);

        // [START aggregate_group_syntax]
        Pipeline.Snapshot groupSyntaxResult = db.pipeline()
                .collectionGroup("cities")
                .aggregate(
                        Aggregate.withAccumulators(
                                countAll().as("cities"),
                                sum(field("population")).as("totalPopulation"))
                                .withGroups(field("location.state").as("state")))
                .execute()
                .get();
        // [END aggregate_group_syntax]
        System.out.println(groupSyntaxResult);

        // [START aggregate_data]
        Map<String, Object> sf = new HashMap<>();
        sf.put("name", "San Francisco");
        sf.put("state", "CA");
        sf.put("country", "USA");
        sf.put("population", 870000);
        db.collection("cities").document("SF").set(sf).get();

        Map<String, Object> la = new HashMap<>();
        la.put("name", "Los Angeles");
        la.put("state", "CA");
        la.put("country", "USA");
        la.put("population", 3970000);
        db.collection("cities").document("LA").set(la).get();

        Map<String, Object> ny = new HashMap<>();
        ny.put("name", "New York");
        ny.put("state", "NY");
        ny.put("country", "USA");
        ny.put("population", 8530000);
        db.collection("cities").document("NY").set(ny).get();

        Map<String, Object> tor = new HashMap<>();
        tor.put("name", "Toronto");
        tor.put("state", null);
        tor.put("country", "Canada");
        tor.put("population", 2930000);
        db.collection("cities").document("TOR").set(tor).get();

        Map<String, Object> mex = new HashMap<>();
        mex.put("name", "Mexico City");
        mex.put("state", null);
        mex.put("country", "Mexico");
        mex.put("population", 9200000);
        db.collection("cities").document("MEX").set(mex).get();
        // [END aggregate_data]

        // [START aggregate_without_group]
        Pipeline.Snapshot withoutGroupResult = db.pipeline()
                .collection("cities")
                .aggregate(
                        countAll().as("total"),
                        average("population").as("averagePopulation"))
                .execute()
                .get();
        // [END aggregate_without_group]
        System.out.println(withoutGroupResult);

        // [START aggregate_group_example]
        Pipeline.Snapshot groupResult = db.pipeline()
                .collection("cities")
                .aggregate(
                        Aggregate.withAccumulators(
                                countAll().as("numberOfCities"),
                                maximum("population").as("maxPopulation"))
                                .withGroups(field("country"), field("state")))
                .execute()
                .get();
        // [END aggregate_group_example]
        System.out.println(groupResult);

        // [START aggregate_group_complex]
        Pipeline.Snapshot complexGroupResult = db.pipeline()
                .collection("cities")
                .aggregate(
                        Aggregate.withAccumulators(
                                sum("population").as("totalPopulation"))
                                .withGroups(field("state").equal(nullValue()).as("stateIsNull")))
                .execute()
                .get();
        // [END aggregate_group_complex]
        System.out.println(complexGroupResult);
    }

    public static void pipelineJoinExamples(Firestore db) throws ExecutionException, InterruptedException {
        // [START pipeline_join_test_data]
        // Load set of cities.
        CollectionReference cities = db.collection("cities");

        Map<String, Object> sfData = new HashMap<>();
        sfData.put("name", "San Francisco");
        sfData.put("state", "CA");
        sfData.put("country", "USA");
        cities.document("SF").set(sfData).get();

        Map<String, Object> laData = new HashMap<>();
        laData.put("name", "Los Angeles");
        laData.put("state", "CA");
        laData.put("country", "USA");
        cities.document("LA").set(laData).get();

        Map<String, Object> dcData = new HashMap<>();
        dcData.put("name", "Washington, D.C.");
        dcData.put("state", null);
        dcData.put("country", "USA");
        cities.document("DC").set(dcData).get();

        Map<String, Object> tokData = new HashMap<>();
        tokData.put("name", "Tokyo");
        tokData.put("state", null);
        tokData.put("country", "Japan");
        cities.document("TOK").set(tokData).get();

        // Load restaurants in various cities.
        CollectionReference sfRestaurants = db.collection("cities/SF/restaurants");
        CollectionReference laRestaurants = db.collection("cities/LA/restaurants");
        CollectionReference dcRestaurants = db.collection("cities/DC/restaurants");

        DocumentReference rest1 = sfRestaurants.add(Map.of(
                "name", "Golden Gate Pizza",
                "type", "pizza",
                "owner_id", "Mario Rossi")).get();
        DocumentReference rest2 = sfRestaurants.add(Map.of(
                "name", "Bay Area Burger",
                "type", "burger",
                "owner_id", "Sarah Jenkins")).get();
        DocumentReference rest3 = sfRestaurants.add(Map.of(
                "name", "Sunset Taco",
                "type", "mexican",
                "owner_id", "Edward")).get();

        DocumentReference rest4 = laRestaurants.add(Map.of(
                "name", "Hollywood Sushi",
                "type", "sushi",
                "owner_id", "Ken Kenji")).get();
        DocumentReference rest5 = laRestaurants.add(Map.of(
                "name", "Venice Pizza",
                "type", "pizza",
                "owner_id", "Luigi Romano")).get();

        DocumentReference rest6 = dcRestaurants.add(Map.of(
                "name", "Capitol Tacos",
                "type", "mexican",
                "owner_id", "Maria Garcia")).get();
        DocumentReference rest7 = dcRestaurants.add(Map.of(
                "name", "Georgetown Coffee",
                "type", "cafe",
                "owner_id", "David Kim")).get();

        // Load collection of reviews.
        CollectionReference reviews = db.collection("reviews");

        reviews.add(Map.of("restaurant", rest1, "rating", 5, "reviewer_id", "Alice")).get();
        reviews.add(Map.of("restaurant", rest1, "rating", 4, "reviewer_id", "Bob")).get();
        reviews.add(Map.of("restaurant", rest2, "rating", 4, "reviewer_id", "Charlie")).get();
        reviews.add(Map.of("restaurant", rest3, "rating", 5, "reviewer_id", "Diana")).get();
        reviews.add(Map.of("restaurant", rest3, "rating", 4, "reviewer_id", "Edward")).get();
        reviews.add(Map.of("restaurant", rest3, "rating", 4, "reviewer_id", "Fiona")).get();
        // rest4 has 0 reviews
        reviews.add(Map.of("restaurant", rest5, "rating", 3, "reviewer_id", "George")).get();
        reviews.add(Map.of("restaurant", rest6, "rating", 5, "reviewer_id", "Hannah")).get();
        reviews.add(Map.of("restaurant", rest6, "rating", 4, "reviewer_id", "Ian")).get();
        reviews.add(Map.of("restaurant", rest7, "rating", 5, "reviewer_id", "Julia")).get();
        // [END pipeline_join_test_data]

        // [START pipeline_join_lookup]
        Pipeline.Snapshot lookupResults = db.pipeline()
                .collectionGroup("reviews")
                .define(field("restaurant").as("restaurant_name"))
                .addFields(
                        db.pipeline()
                                .collectionGroup("restaurants")
                                .where(field("__name__").equal(variable("restaurant_name")))
                                .select("name", "type")
                                .toScalarExpression()
                                .as("restaurant"))
                .execute()
                .get();
        // [END pipeline_join_lookup]
        System.out.println(lookupResults);

        // [START pipeline_join_array]
        Pipeline.Snapshot arrayResults = db.pipeline()
                .collectionGroup("restaurants")
                .where(field("type").equal("pizza"))
                .define(field("__name__").as("restaurant_name"))
                .select(
                        field("name"),
                        db.pipeline()
                                .collectionGroup("reviews")
                                .where(field("restaurant").equal(variable("restaurant_name")))
                                .select("rating", "reviewer_id")
                                .toArrayExpression()
                                .as("reviews"))
                .execute()
                .get();
        // [END pipeline_join_array]
        System.out.println(arrayResults);

        // [START pipeline_join_aggregate]
        Pipeline.Snapshot aggregateResults = db.pipeline()
                .collectionGroup("restaurants")
                .where(field("type").equal("pizza"))
                .define(field("__name__").as("restaurant_name"))
                .select(
                        field("name"),
                        db.pipeline()
                                .collectionGroup("reviews")
                                .where(field("restaurant").equal(variable("restaurant_name")))
                                .aggregate(average("rating").as("avg_rating"))
                                .toScalarExpression()
                                .as("avg_rating"))
                .execute()
                .get();
        // [END pipeline_join_aggregate]
        System.out.println(aggregateResults);

        // [START pipeline_join_limit]
        Pipeline.Snapshot limitResults = db.pipeline()
                .collectionGroup("restaurants")
                .define(field("__name__").as("restaurant_name"))
                .select(
                        field("name"),
                        db.pipeline()
                                .collectionGroup("reviews")
                                .where(field("restaurant").equal(variable("restaurant_name")))
                                .sort(field("rating").descending())
                                .limit(2)
                                .select("rating", "reviewer_id")
                                .toArrayExpression()
                                .as("top_reviews"))
                .execute()
                .get();
        // [END pipeline_join_limit]
        System.out.println(limitResults);

        // [START pipeline_join_subcollection]
        Pipeline.Snapshot subcollectionResults = db.pipeline()
                .collection("cities")
                .addFields(
                        PipelineSource.subcollection("restaurants")
                                .toArrayExpression()
                                .length()
                                .as("restaurant_count"))
                .execute()
                .get();
        // [END pipeline_join_subcollection]
        System.out.println(subcollectionResults);

        // [START pipeline_join_multi_field]
        Pipeline.Snapshot multiFieldResults = db.pipeline()
                .collectionGroup("restaurants")
                .define(
                        field("owner_id").as("owner_id"),
                        field("__name__").as("__name__"))
                .where(
                        db.pipeline()
                                .collectionGroup("reviews")
                                .where(field("restaurant").equal(variable("__name__")))
                                .where(field("reviewer_id").equal(variable("owner_id")))
                                .aggregate(countAll().as("c"))
                                .toScalarExpression()
                                .greaterThan(0))
                .execute()
                .get();
        // [END pipeline_join_multi_field]
        System.out.println(multiFieldResults);

        // [START pipeline_join_anti]
        Pipeline.Snapshot antiResults = db.pipeline()
                .collectionGroup("restaurants")
                .define(field("__name__").as("restaurant_name"))
                .where(
                        db.pipeline()
                                .collectionGroup("reviews")
                                .where(field("restaurant").equal(variable("restaurant_name")))
                                .aggregate(countAll().as("review_count"))
                                .toScalarExpression()
                                .equal(0))
                .execute()
                .get();
        // [END pipeline_join_anti]
        System.out.println(antiResults);

        // [START pipeline_join_unnest]
        Pipeline.Snapshot unnestResults = db.pipeline()
                .collectionGroup("restaurants")
                .where(field("type").equal("pizza"))
                .define(field("__name__").as("restaurant_name"))
                .unnest(
                        db.pipeline()
                                .collectionGroup("reviews")
                                .where(field("restaurant").equal(variable("restaurant_name")))
                                .select("rating", "reviewer_id")
                                .toArrayExpression()
                                .as("review"))
                .execute()
                .get();
        // [END pipeline_join_unnest]
        System.out.println(unnestResults);

        // [START pipeline_join_uncorrelated]
        Pipeline.Snapshot uncorrelatedResults = db.pipeline()
                .collection("reviews")
                // Average review rating is 4.3
                .where(
                        field("rating").greaterThan(
                                db.pipeline()
                                        .collection("reviews")
                                        .aggregate(average("rating").as("avg"))
                                        .toScalarExpression()))
                .select("rating", "reviewer_id")
                .execute()
                .get();
        // [END pipeline_join_uncorrelated]
        System.out.println(uncorrelatedResults);
    }

    @SuppressWarnings("unchecked")
    public static void literalsExamples(Firestore db) throws ExecutionException, InterruptedException {
        // [START literals_example]
        Pipeline.Snapshot results = db.pipeline()
                .literals(
                        Map.of("name", "joe", "age", 10),
                        Map.of("name", "bob", "age", 30),
                        Map.of("name", "alice", "age", 40))
                .where(field("age").lessThan(35))
                .execute()
                .get();
        // [END literals_example]
        System.out.println(results);

        // [START literals_expressions]
        Pipeline.Snapshot exprResults = db.pipeline()
                .literals(
                        Map.of("x", constant("foo-bar-baz").length()),
                        Map.of("x", constant("bar").length()))
                .execute()
                .get();
        // [END literals_expressions]
        System.out.println(exprResults);
    }

    public static void dmlExamples(Firestore db) throws ExecutionException, InterruptedException {
        // [START update_dml]
        Pipeline.Snapshot snapshot = db.pipeline()
                .collectionGroup("users")
                .where(not(exists(field("preferences.color"))))
                .addFields(nullValue().as("preferences.color"))
                .removeFields("color")
                .update()
                .execute()
                .get();
        // [END update_dml]
        System.out.println(snapshot);

        // [START delete_dml]
        Pipeline deletePipeline = db.pipeline()
                .collectionGroup("users")
                .where(field("address.country").equal("USA"))
                .where(field("__create_time__").timestampAdd("day", 10).lessThan(currentTimestamp()))
                .delete();
        deletePipeline.execute().get();
        // [END delete_dml]

        // [START delete_full_collection]
        db.pipeline()
                .collection("/users")
                .delete()
                .execute()
                .get();
        // [END delete_full_collection]

        // [START dml_transaction]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("users")
                    .where(equal(field("active"), false))
                    .addFields(constant("inactive").as("status"))
                    .update();

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END dml_transaction]

        // [START dml_atomic_option]
        db.pipeline()
                .collection("users")
                .where(equal(field("active"), false))
                .addFields(constant("inactive").as("status"))
                .update()
                .execute(new PipelineExecuteOptions().withAtomic(true))
                .get();
        // [END dml_atomic_option]
    }

    @SuppressWarnings("unchecked")
    public static void insertExamples(Firestore db) throws ExecutionException, InterruptedException {
        // [START insert_static_documents]
        db.pipeline()
                .literals(
                        Map.of("id", "user1", "name", "Alice", "age", 30),
                        Map.of("id", "user2", "name", "Bob", "age", 25))
                .insert(
                        new Insert()
                                .withCollection("users")
                                .withDocumentIdExpression(field("id")))
                .execute()
                .get();
        // [END insert_static_documents]

        // [START insert_evaluated_doc_id]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("users")
                    .distinct(field("name"))
                    .insert(
                            new Insert()
                                    .withCollection("new_users")
                                    .withDocumentIdExpression(stringConcat(constant("new_"), field("name"))));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END insert_evaluated_doc_id]

        // [START insert_document_references]
        db.pipeline()
                .literals(
                        Map.of("__name__", db.document("users/doc1"), "name", "Alice"),
                        Map.of("__name__", db.document("users/doc2"), "name", "Bob"))
                .insert()
                .execute()
                .get();
        // [END insert_document_references]

        // [START insert_auto_generated_ids]
        db.pipeline()
                .literals(
                        Map.of("name", "Alice", "role", "admin"),
                        Map.of("name", "Bob", "role", "viewer"))
                .insert(new Insert().withCollection("users"))
                .execute()
                .get();
        // [END insert_auto_generated_ids]

        // [START insert_subcollection]
        db.pipeline()
                .literals(
                        Map.of("title", "Hello World", "published", true))
                .insert(new Insert().withCollection("users/u1/posts"))
                .execute()
                .get();
        // [END insert_subcollection]

        // [START insert_different_collection]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("source_coll")
                    .where(equal(field("active"), true))
                    .addFields(constant("val").as("new_field"))
                    .insert(new Insert().withCollection("target_coll"));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END insert_different_collection]

        // [START insert_advanced_queries]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("products")
                    .search(Search.withQuery("shoes"))
                    .insert(new Insert().withCollection("matched_products"));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END insert_advanced_queries]
    }

    public static void updateAggregationSubqueryExample(Firestore db)
            throws ExecutionException, InterruptedException {
        // [START update_aggregation_subquery]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("users")
                    .where(
                            equal(
                                    field("score"),
                                    db.pipeline()
                                            .collection("users")
                                            .aggregate(maximum("score").as("max"))
                                            .toScalarExpression()))
                    .update(constant(true).as("is_top_scorer"));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END update_aggregation_subquery]
    }

    @SuppressWarnings("unchecked")
    public static void upsertExamples(Firestore db) throws ExecutionException, InterruptedException {
        // [START upsert_migrate_users]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("users")
                    .where(greaterThanOrEqual(field("age"), 25))
                    .addFields(constant(true).as("processed"))
                    .upsert(
                            new Upsert()
                                    .withCollection("new_users")
                                    .withDocumentIdExpression(field("name")));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END upsert_migrate_users]

        // [START upsert_in_place]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("users")
                    .upsert(constant(31).as("age"));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END upsert_in_place]

        // [START upsert_different_collection_explicit_id]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("source_coll")
                    .upsert(
                            new Upsert(constant(31).as("age"))
                                    .withCollection("target_coll")
                                    .withDocumentIdExpression(constant("new_doc_id")));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END upsert_different_collection_explicit_id]

        // [START upsert_different_collection_existing_id]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("source_coll")
                    .upsert(
                            new Upsert(constant("updated_val").as("status"))
                                    .withCollection("target_coll"));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END upsert_different_collection_existing_id]

        // [START upsert_document_references]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .literals(
                            Map.of("__name__", db.document("users/doc1"), "name", "Alice"),
                            Map.of("__name__", db.document("users/doc2"), "name", "Bob"))
                    .upsert();

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END upsert_document_references]

        // [START upsert_auto_generated_ids]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .literals(
                            Map.of("name", "Alice"),
                            Map.of("name", "Bob"))
                    .upsert(
                            new Upsert(constant("active").as("status"))
                                    .withCollection("users"));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END upsert_auto_generated_ids]

        // [START upsert_subcollection]
        db.pipeline()
                .literals(
                        Map.of("__name__", db.document("users/u1/posts/p1"), "title", "Updated Post"))
                .upsert(new Upsert().withCollection("users/u1/posts"))
                .execute(new PipelineExecuteOptions().withAtomic(true))
                .get();
        // [END upsert_subcollection]

        // [START upsert_advanced_queries]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("products")
                    .search(Search.withQuery("shoes"))
                    .upsert(constant(true).as("is_shoes"));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END upsert_advanced_queries]

        // [START upsert_aggregation_subquery]
        db.runTransaction(transaction -> {
            Pipeline pipeline = db.pipeline()
                    .collection("users")
                    .where(
                            equal(
                                    field("score"),
                                    db.pipeline()
                                            .collection("users")
                                            .aggregate(maximum("score").as("max"))
                                            .toScalarExpression()))
                    .upsert(constant(true).as("is_top_scorer"));

            transaction.execute(pipeline).get();
            return null;
        }).get();
        // [END upsert_aggregation_subquery]
    }

    public static void rulesPipelinesExamples(Firestore db)
            throws ExecutionException, InterruptedException {
        // [START rules_pipelines_filter_before]
        Pipeline.Snapshot beforeResults = db.pipeline()
                .collection("FeatureRequests")
                .where(field("status").equal("open"))
                .addFields(constant(true).as("isArchived"))
                .execute()
                .get();
        // [END rules_pipelines_filter_before]
        System.out.println(beforeResults);

        // [START rules_pipelines_filter_after]
        Pipeline.Snapshot afterResults = db.pipeline()
                .collection("FeatureRequests")
                .addFields(constant(true).as("isArchived"))
                .where(field("status").equal("open"))
                .execute()
                .get();
        // [END rules_pipelines_filter_after]
        System.out.println(afterResults);
    }

}
