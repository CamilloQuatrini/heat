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
package com.hotels.heat.core.runner;

import java.util.*;

import com.hotels.heat.core.utils.TestCaseUtils;
import com.hotels.heat.core.utils.log.LogUtils;
import com.hotels.heat.core.utils.testobjects.Status;
import com.hotels.heat.core.utils.testobjects.TestCase;
import com.hotels.heat.core.utils.testobjects.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.hotels.heat.core.handlers.PlaceholderHandler;
import com.hotels.heat.core.handlers.TestCaseMapHandler;
import com.hotels.heat.core.handlers.TestSuiteHandler;
import com.hotels.heat.core.heatspecificchecks.SpecificChecks;
import com.hotels.heat.core.utils.RunnerInterface;
import com.hotels.heat.core.utils.log.LoggingUtils;

import io.restassured.response.Response;


/**
 * Generic Test Runner class.
 */
public class TestBaseRunner implements RunnerInterface {

    public static final String CONTEXT_PROPERTY_FILE_PATH = "PROPERTY_FILE_PATH";
    public static final String CONTEXT_WEBAPP_NAME = "WEBAPP_NAME";
    public static final String CONTEXT_ENABLED_ENVIRONMENTS = "ENABLED_ENVIRONMENTS";
    public static final String CONTEXT_SUITE_JSON_FILE_PATH = "SUITE_JSON_FILE_PATH";
    public static final String ENABLED_ENVIRONMENT_STRING_SEPARATOR = ",";
    public static final String CONTEXT_TEST_CASES_LIST = "TEST_CASES_LIST";
    public static final String CONTEXT_SUITE_STATUS = "SUITE_STATUS";
    public static final String CONTEXT_TEST_CASE_ID = "TEST_CASE_ID";
    public static final String CONTEXT_TEST_CASE_STEP_ID = "TEST_CASE_STEP_ID";
    public static final String CONTEXT_ENVIRONMENT_UNDER_TEST = "ENVIRONMENT_UNDER_TEST";
    public static final String CONTEXT_HTTP_METHOD = "HTTP_METHOD";
    public static final String CONTEXT_TEST_SUITE_DESCRIPTION = "TEST_SUITE_DESCRIPTION";
    public static final String CONTEXT_BEFORE_SUITE_VARIABLES = "BEFORE_SUITE_VARIABLES";
    public static final String CONTEXT_JSON_SCHEMAS = "JSON_SCHEMAS";
    public static final String CONTEXT_WEBAPP_UNDER_TEST = "WEBAPP_UNDER_TEST";

    private final Logger logger = LoggerFactory.getLogger(TestBaseRunner.class);

    public static final String SYSPROP_DEFAULT_ENVIRONMENT = "defaultEnvironment";
    public static final String SYSPROP_HEAT_TEST = "heatTest";

    static {
        LoggerFactory.getLogger(TestBaseRunner.class).info(
                "\n\n"
                + "   ooooo   ooooo oooooooooooo       .o.       ooooooooooooo \n" +
                  "   `888'   `888' `888'     `8      .888.      8'   888   `8 \n" +
                  "    888     888   888             .8\"888.          888      \n" +
                  "    888ooooo888   888oooo8       .8' `888.         888      \n" +
                  "    888     888   888    \"      .88ooo8888.        888      \n" +
                  "    888     888   888       o  .8'     `888.       888      \n" +
                  "   o888o   o888o o888ooooood8 o88o     o8888o     o888o     \n" +
                  "  ----------------------------------------------------------\n" +
                  "          Hotels.com Engine Architecture for Testing \n" +
                  "  ----------------------------------------------------------\n"
        );

        LoggerFactory.getLogger(TestBaseRunner.class).info(
                "\n"
                + "+-----------------------------------------------------------------------+\n"
                + "| Environment under test : '{}'\n"
                + "+-----------------------------------------------------------------------+\n"
                + "| Requested Log Level: : '{}'\n"
                + "| Specific test requested: '{}'\n"
                + "+-----------------------------------------------------------------------+\n",
                System.getProperty("environment", System.getProperty(SYSPROP_DEFAULT_ENVIRONMENT)),
                System.getProperty("logLevel", "INFO"),
                System.getProperty("heatTest", "All Tests"));

    }


    public static final String ENV_PROP_FILE_PATH = "envPropFilePath";
    public static final String WEBAPP_NAME = "webappName";
    public static final String NO_INPUT_WEBAPP_NAME = "noInputWebappName";

    public static final String INPUT_JSON_PATH = "inputJsonPath";
    public static final String ENABLED_ENVIRONMENTS = "enabledEnvironments";


    /**
     * Method that takes test suites parameters and sets some environment properties.
     * @param propFilePath path of the property file data
     * @param inputWebappName name of the service to test (optional parameter)
     * @param context testNG context
     */
    @BeforeSuite
    @Override
    @Parameters({ENV_PROP_FILE_PATH, WEBAPP_NAME})
    public void beforeTestSuite(String propFilePath,
                                @Optional(NO_INPUT_WEBAPP_NAME) String inputWebappName,
                                ITestContext context) {

        logger.trace("{} beforeTestSuite - start", LogUtils.getInstance(context).getCurrentTestDescription());
        context.setAttribute(CONTEXT_PROPERTY_FILE_PATH, propFilePath);
        context.setAttribute(CONTEXT_WEBAPP_NAME, inputWebappName);
        logger.trace("{} beforeTestSuite - end", LogUtils.getInstance(context).getCurrentTestDescription());
    }

    /**
     * Method that takes tests parameters and sets some environment properties.
     * @param inputJsonParamPath path of the json input file with input data for tests
     * @param enabledEnvironments environments enabled for the specific suite
     * @param context testNG context
     */
    @BeforeTest
    @Override
    @Parameters({INPUT_JSON_PATH, ENABLED_ENVIRONMENTS})
    public void beforeTestCase(String inputJsonParamPath,
        String enabledEnvironments,
        ITestContext context) {

        logger.trace("{} beforeTestCase - start", LogUtils.getInstance(context).getCurrentTestDescription());
        context.setAttribute(CONTEXT_ENABLED_ENVIRONMENTS, Arrays.asList(enabledEnvironments.split(ENABLED_ENVIRONMENT_STRING_SEPARATOR)));
        context.setAttribute(CONTEXT_SUITE_JSON_FILE_PATH, inputJsonParamPath);
        logger.trace("{} beforeTestCase - end", LogUtils.getInstance(context).getCurrentTestDescription());
    }


    /**
     * Method to extract the iterator of test cases.
     * @param context testNG context
     * @return an iterator of the test cases present in the test suite
     */
    @Override
    @DataProvider(name = "provider")
    public Iterator<Object[]> providerJson(ITestContext context) {
        logger.trace("{} providerJson - start", LogUtils.getInstance(context).getCurrentTestDescription());
        List<TestCase> testCasesInTheSuite = new ArrayList();
        context.setAttribute(CONTEXT_TEST_CASES_LIST, testCasesInTheSuite);
        context.setAttribute(CONTEXT_SUITE_STATUS, Status.NOT_PERFORMED);
        logger.trace("{} providerJson - next step is jsonReader", LogUtils.getInstance(context).getCurrentTestDescription());
        TestCaseUtils tcUtils = new TestCaseUtils();
        tcUtils.loadSystemProperties(context);
        Iterator<Object[]> iterator = tcUtils.jsonReader(context);
        return iterator;
    }

    /**
     * Elaboration of test case parameters before any request (method executed as first step in the runner).
     * @param testCaseParams Map containing test case parameters coming from the json input file
     * @param paramsToSkip parameters we need to skip in this phase of placeholder handling
     * @return the same structure as the input parameters but with placeholders resolved
     */
    @Override
    public Map resolvePlaceholdersInTcParams(Map<String, Object> testCaseParams, List<String> paramsToSkip) {
        TestSuiteHandler testSuiteHandler = TestSuiteHandler.getInstance();
        testSuiteHandler.getLogUtils().setTestCaseId(testContext.getAttribute(ATTR_TESTCASE_ID).toString());

        // now we start elaborating the parameters.
        placeholderHandler = new PlaceholderHandler();
        placeholderHandler.setPreloadedVariables(testSuiteHandler.getTestCaseUtils().getBeforeSuiteVariables());

        TestCaseMapHandler tcMapHandler = new TestCaseMapHandler(testCaseParams, placeholderHandler, paramsToSkip);

        return tcMapHandler.retrieveProcessedMap();
    }

    /**
     * Elaboration of test case parameters before any request (method executed as first step in the runner).
     * @param testCaseParams Map containing test case parameters coming from the json input file
     * @return the same structure as the input parameters but with placeholders resolved
     */
    public Map resolvePlaceholdersInTcParams(Map<String, Object> testCaseParams) {
        return resolvePlaceholdersInTcParams(testCaseParams, new ArrayList());
    }

    /**
     * Method to set useful parameters in the context managed by testNG.
     * Parameters that will be set will be: 'testId', 'suiteDescription', 'tcDescription'
     * @param testCaseParams Map containing test case parameters coming from the json input file
     */
    public void setContextAttributes(Map<String, Object> testCaseParams, ITestContext context) {
        String testCaseID = testCaseParams.get(ATTR_TESTCASE_ID).toString();
        testContext.setAttribute(ATTR_TESTCASE_ID, testCaseID);
        String suiteDescription = TestSuiteHandler.getInstance().getTestCaseUtils().getSuiteDescription();
        testContext.setAttribute(SUITE_DESCRIPTION_CTX_ATTR, suiteDescription);
        String testCaseDesc = testCaseParams.get(ATTR_TESTCASE_NAME).toString();
        testContext.setAttribute(TC_DESCRIPTION_CTX_ATTR, testCaseDesc);
    }

    /**
     * Checks if the test case is skippable or not, basing on the name of the current test suite (if the system parameter 'heatTest' is set in the
     * test running command) and on other suppositions.
     * @param currentTestSuiteName name of the test suite currently in execution
     * @param currentTestCaseId name of the test case currently in execution
     * @param webappName name of the service under test
     * @param webappPath path of the service under test (basing on the environment)
     * @return a boolean that indicates if this test case will be skipped
     */
    public boolean isTestCaseSkippable(String currentTestSuiteName, String currentTestCaseId, String webappName, String webappPath) {
        boolean thisTestIsSkippable;
        TestSuiteHandler testSuiteHandler = TestSuiteHandler.getInstance();

        boolean isParamsValid = testSuiteHandler.getTestCaseUtils().isCommonParametersValid(webappName, webappPath, getInputJsonPath(),
                testSuiteHandler.getLogUtils(), testSuiteHandler.getEnvironmentHandler());
        if (!isParamsValid) {
            thisTestIsSkippable = true; //Skip current test if shared parameters are missing
        } else {
            boolean isCurrentInList = false;
            List<String> heatTestPropertyList = testSuiteHandler.getEnvironmentHandler().getHeatTestPropertyList();
            for (String heatTestProperty : heatTestPropertyList) {

                String[] heatTestPropertySplitted = heatTestPropertySplit(heatTestProperty);
                String suiteNameToRun = heatTestPropertySplitted[0];
                String testCaseIdToRun = heatTestPropertySplitted[1];

                if (suiteNameToRun != null && testCaseIdToRun == null && suiteNameToRun.equalsIgnoreCase(currentTestSuiteName)) {
                    isCurrentInList = true; //Only current suite is specified
                }

                if (suiteNameToRun != null && testCaseIdToRun != null
                    && suiteNameToRun.equalsIgnoreCase(currentTestSuiteName)
                    && testCaseIdToRun.equalsIgnoreCase(currentTestCaseId)) {
                    isCurrentInList = true; //Both suite and test id are specified
                }
            }
            //Skipping current test if there is a list of tests to run (sys property 'heatTest') and the current one is not in that list
            thisTestIsSkippable = !heatTestPropertyList.isEmpty() && !isCurrentInList;
        }
        return thisTestIsSkippable;
    }


    /**
     * Splits the heatTest system property.
     * @param heatTestProperty value of the 'heatTest' system property
     * @return an array of strings containing all the single properties specified in the 'heatTest' system property
     */
    public static String[] heatTestPropertySplit(String heatTestProperty) {
        String[] safeSplit;
        if (heatTestProperty != null) {
            //if an element of that list does not contain ".", it means that it is not a specific test case and the output will be a String[] of two elements
            // and the second one is null (it is a test SUITE)
            if (!heatTestProperty.contains(TESTCASE_ID_SEPARATOR)) {
                safeSplit = new String[]{heatTestProperty, null};
            } else {
                //if the element contains the separator ".", it means that it is a specific test case and the output will be a String[] of two elements not null
                safeSplit = heatTestProperty.split(TESTCASE_ID_SEPARATOR_ESCAPED);
            }
        } else {
            safeSplit = new String[]{null, null};
        }
        return safeSplit;
    }


    /**
     * Executes, if necessary, the specific checks related to the test case that is currently running.
     * @param testCaseParams Map containing test case parameters coming from the json input file
     * @param rspRetrieved Map of the responses retrieved from the just executed requests. If the current modality is 'SingleMode', this map will
     *                     contain only one element, otherwise it will contain as much elements as the single test case requires
     * @param environment environment used for this test
     */
    @Override
    public void specificChecks(Map testCaseParams, Map<String, Response> rspRetrieved, String environment) {
        ServiceLoader.load(SpecificChecks.class).forEach(checks -> checks.process(
                getTestContext().getName(), testCaseParams, rspRetrieved,
                TestSuiteHandler.getInstance().getLogUtils().getTestCaseDetails(),
                TestSuiteHandler.getInstance().getEnvironmentHandler().getEnvironmentUnderTest()
        ));
    }


    public PlaceholderHandler getPlaceholderHandler() {
        return placeholderHandler;
    }

    public String getInputJsonPath() {
        return inputJsonPath;
    }

    public void setInputJsonPath(String inputJsonPath) {
        this.inputJsonPath = inputJsonPath;
    }

    public ITestContext getTestContext() {
        return testContext;
    }

    public LoggingUtils getLogUtils() {
        return TestSuiteHandler.getInstance().getLogUtils();
    }


}
