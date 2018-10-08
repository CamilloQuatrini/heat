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
package com.hotels.heat.core.utils.log;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.hotels.heat.core.runner.TestBaseRunner;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;


/**
 * This class contains utilities for logging.
 */
public class LogUtils extends LoggerFactory {

    private ITestContext context;
    private String testID;

    private String testCaseDetails;
    private Integer flowStep;
    private String logLevel;
    private Class className;

    public LogUtils() {
        this.testCaseDetails = "";
        this.setLogLevel();
        this.logLevel = Level.INFO.toString();
    }

    /**
     * This method sets the log level (logback).
     */
    public void setLogLevel() {
        logLevel = System.getProperty("logLevel", Level.INFO.toString());
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        Level logLevelSetting = Level.toLevel(logLevel.toUpperCase());
        root.setLevel(logLevelSetting);
    }

    public void setTestContext(ITestContext context) {
        this.context = context;
    }

    public void setTestCaseId(String testID) {
        this.testID = testID;
    }

    public void resetTestCaseId() {
        this.testID = null;
    }


    public void setFlowStep(Integer flowStepInput) {
        flowStep = flowStepInput;
    }

    public void resetFlowStep() {
        flowStep = null;
    }

    public String getTestCaseDetails() {
        testCaseDetails = "[" + (context != null ? context.getName() : "") + "] ";
        if (testID != null) {
            testCaseDetails = "[" + context.getName() + TestBaseRunner.TESTCASE_ID_SEPARATOR + testID + "]";
        }

        testCaseDetails += " ";
        if (flowStep != null) {
            testCaseDetails += "[FLOW STEP #" + flowStep + "] ";
        }
        return testCaseDetails;
    }



    public void info(String message) { log(Level.INFO, message); }

    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    public void trace(String message) {
        log(Level.TRACE, message);
    }

    public void error(String message) { log(Level.ERROR, message); }

    public void warn(String message) {
        log(Level.WARN, message);
    }


    /*private void log(Level mode, String message, Object... params) {
        try {
            org.slf4j.Logger logger = LoggerFactory.getLogger(this.className);
            Class[] cArg = new Class[2];
            cArg[0] = Level.class;
            cArg[1] = Object[].class;
            Method loggerMethod = logger.getClass().getMethod(mode, cArg);
            loggerMethod.invoke(logger, message, params);
        } catch (Exception oEx) {
            throw new HeatException(oEx.getClass()
                    + " / cause: '" + oEx.getCause() + "' / message: '" + oEx.getLocalizedMessage() + "'");
        }
    }*/

}
