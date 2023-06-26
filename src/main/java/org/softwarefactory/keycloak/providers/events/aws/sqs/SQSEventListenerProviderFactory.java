/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.softwarefactory.keycloak.providers.events.aws.sqs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.HashSet;
import java.util.Set;

public class SQSEventListenerProviderFactory implements EventListenerProviderFactory {

    private Set<EventType> excludedEvents;
    private Set<OperationType> excludedAdminOperations;
    private String queueUrl;

    private String accessKey;

    private String secretKey;
    private String region;

    private AmazonSQS sqs;

    @Override
    public EventListenerProvider create(KeycloakSession session) {

        return new SQSEventListenerProvider(excludedEvents, excludedAdminOperations, queueUrl, this.sqs);
    }

    @Override
    public void init(Config.Scope config) {
        
        String[] excludesOperations = config.getArray("excludesOperations");
        if (excludesOperations != null) {
            excludedAdminOperations = new HashSet<>();
            for (String e : excludesOperations) {
                excludedAdminOperations.add(OperationType.valueOf(e));
            }
        }
        
        queueUrl = config.get("queueUrl", null);
        secretKey = config.get("secretKey", null);
        accessKey = config.get("accessKey", null);
        region = config.get("region", null);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        AWSCredentialsProvider awsCredentials = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey));

        this.sqs = AmazonSQSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(awsCredentials)
                .build();

        System.out.println("Queue initialized. " + this.queueUrl);
    }
    @Override
    public void close() {
        this.sqs.shutdown();
    }

    @Override
    public String getId() {
        return "sqs";
    }

}
