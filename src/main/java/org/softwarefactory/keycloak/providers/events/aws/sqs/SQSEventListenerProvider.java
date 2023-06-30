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

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;

import java.lang.Exception;

public class SQSEventListenerProvider implements EventListenerProvider {
    private String queueUrl;
    public static final String publisherId = "keycloak";

    private final AmazonSQS sqs;

    private KeycloakSession session;

    public SQSEventListenerProvider(String queueUrl, AmazonSQS sqs, KeycloakSession session) {
        this.queueUrl = queueUrl;
        this.sqs = sqs;
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
                    
            if(!event.getResourceType().equals(ResourceType.USER) && !event.getResourceType().equals(ResourceType.REALM_ROLE_MAPPING)) {
                return;
            }

            String stringEvent = toString(event);
           
            try {
                SendMessageRequest message = new SendMessageRequest()
                        .withQueueUrl(this.queueUrl)
                        .withMessageBody(stringEvent)
                        .withDelaySeconds(5);

                sqs.sendMessage(message);
            } catch(Exception e) {
                // ?
                System.out.println("Error while trying to push event to queue. " + e.toString());
                e.printStackTrace();
                return;
            }
    }

    private String toString(AdminEvent event) {
        StringBuilder sb = new StringBuilder();

        String resourcePath = event.getResourcePath();

        sb.append("{'operation': '");
        sb.append(event.getOperationType());
        sb.append("', 'resourceType': '");
        sb.append(event.getResourceType());
        sb.append("', 'realm': '");
        sb.append(this.session.getContext().getRealm().getName());
        sb.append("', 'userId': '");
        
        if(resourcePath.indexOf("/", 6) > -1) {
            sb.append(resourcePath.substring(6, 6 + resourcePath.substring(6).indexOf("/")));
        }
        else {
            sb.append(resourcePath.substring(6));
        } 

        sb.append("'");
        sb.append("}}");

        return sb.toString();
    }

    @Override
    public void close() {

    }

}
