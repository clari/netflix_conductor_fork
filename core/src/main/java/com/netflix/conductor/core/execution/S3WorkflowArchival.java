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

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.run.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.s3.AmazonS3;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class S3WorkflowArchival implements WorkflowArchiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3WorkflowArchival.class);

    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;
    private final String bucketURI;
    private final int prefixValue;

    public S3WorkflowArchival(AmazonS3 s3Client, ObjectMapper objectMapper, String bucketURI, int prefixValue) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
        this.bucketURI = bucketURI.charAt(bucketURI.length() - 1) == '/' ? bucketURI : bucketURI + '/';
        this.prefixValue = prefixValue;
    }

    @Override
    public void archiveWorkflow(Workflow workflow) {

        String fileName = workflow.getWorkflowId() + ".json";
        String filePathPrefix = workflow.getWorkflowId().substring(0, prefixValue);
        String fullFilePath = filePathPrefix + '/' + fileName;

        try {
            // Upload workflow as a json file to s3
            s3Client.putObject(bucketURI, fullFilePath, objectMapper.writeValueAsString(workflow));
            LOGGER.info("Successfully archived workflow {} to S3 bucket {} as file {}", workflow.getWorkflowId(), bucketURI, fullFilePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getArchivedWorkflow(String workflowId) {

        String fileName = workflowId + ".json";
        String filePathPrefix = workflowId.substring(0, prefixValue);
        String fullFilePath = filePathPrefix + '/' + fileName;

        try (InputStream is = s3Client.getObject(bucketURI, fullFilePath).getObjectContent()) {
            return IOUtils.toString(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
