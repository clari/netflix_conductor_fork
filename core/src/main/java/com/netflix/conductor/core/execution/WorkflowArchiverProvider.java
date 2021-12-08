/*
 * Copyright 2020 Netflix, Inc.
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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.dao.IndexDAO;

import javax.inject.Inject;
import javax.inject.Provider;

public class WorkflowArchiverProvider implements Provider<WorkflowArchiver> {

    private final IndexDAO indexDAO;
    private final ObjectMapper objectMapper;
    private final Configuration config;

    @Inject
    WorkflowArchiverProvider(IndexDAO indexDAO, ObjectMapper objectMapper, Configuration config) {
        this.indexDAO = indexDAO;
        this.objectMapper = objectMapper;
        this.config = config;
    }

    @Override
    public WorkflowArchiver get() {
        if (config.getWorkflowArchivalType().equals(Configuration.ArchivalType.S3)) {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(config.getRegion())
                    .build();
            String bucketName = config.getS3ArchivalBucketName();
            int prefixValue = config.getS3ArchivalLocationPrefix();
            return new S3WorkflowArchival(s3Client, objectMapper, bucketName, prefixValue);
        } else {
            return new ElasticsearchWorkflowArchival(indexDAO, objectMapper);
        }
    }
}
