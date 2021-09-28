/*
 * Copyright 2021 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.core.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.netflix.conductor.common.run.Workflow;
import org.apache.bval.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.s3.AmazonS3;

public class S3WorkflowArchival implements WorkflowArchiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3WorkflowArchival.class);

    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;
    private String bucketURI;
    private final int prefixValue;

    public S3WorkflowArchival(AmazonS3 s3Client, ObjectMapper objectMapper, String bucketURI, int prefixValue) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
        this.bucketURI = bucketURI;
        this.prefixValue = prefixValue;
    }

    @Override
    public void archiveWorkflow(Workflow workflow) {

        String fileName = workflow.getWorkflowId() + ".json";
        String filePathPrefix = workflow.getWorkflowId().substring(0, prefixValue);
        bucketURI = bucketURI.charAt(bucketURI.length() - 1) == '/' ? bucketURI + filePathPrefix: bucketURI + '/' + filePathPrefix;

        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(bucketURI), "S3 bucket URI cannot be blank");
            // Upload workflow as a json file to s3
            s3Client.putObject(bucketURI, fileName, objectMapper.writeValueAsString(workflow));
            LOGGER.info("Successfully archived workflow {} to S3 bucket {} as file {}", workflow.getWorkflowId(), bucketURI, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
