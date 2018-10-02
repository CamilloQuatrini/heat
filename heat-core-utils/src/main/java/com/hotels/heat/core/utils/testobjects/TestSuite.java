/**
 * Copyright (C) 2015-2018 Expedia Inc.
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
package com.hotels.heat.core.utils.testobjects;

import java.util.*;

public final class TestSuite {

    private String name = "TEST SUITE NAME DEFAULT";
    private String jsonFilePath = "JSON FILE PATH DEFAULT";
    private String propertyFilePath = "PROP FILE PATH DEFAULT";
    private Status testSuiteStatus = Status.NOT_PERFORMED;
    private Map<String, TestCase> testCases; // testCaseId String - TestCase object
    private List<String> enabledEnvironments = new ArrayList<>();
    private static TestSuite testSuite;

    public TestSuite() {
        testCases = new HashMap<>();
    }

    /**
     * Singleton implementation for the object.
     * @return the singleton instance of the object
     */
    public static synchronized TestSuite getInstance() {
        if (testSuite == null) {
            testSuite = new TestSuite();
        }
        return testSuite;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setStatus(Status suiteStatus) {
        this.testSuiteStatus = suiteStatus;
    }

    public Status getStatus() {
        return this.testSuiteStatus;
    }

    public void setJsonFilePath(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public String getJsonFilePath() {
        return this.jsonFilePath;
    }

    public void setPropFilePath(String propFilePath) {
        this.propertyFilePath = propFilePath;
    }

    public String getPropFilePath() {
        return this.propertyFilePath;
    }

    public void setTestCases(Map<String, TestCase> testCases) {
        this.testCases = testCases;
    }

    public void addTestCase(TestCase testCase) {
        testCases.put(testCase.getId(), testCase);
    }

    public TestCase getTestCaseFromId(String id) {
        return testCases.get(id);
    }

    public Map<String, TestCase> getTestCases() {
        return this.testCases;
    }

    public void setEnabledEnvironments(String environments) {
        this.enabledEnvironments = Arrays.asList(environments.split(","));
    }

    public List<String> getEnabledEnvironments() {
        return this.enabledEnvironments;
    }


}
