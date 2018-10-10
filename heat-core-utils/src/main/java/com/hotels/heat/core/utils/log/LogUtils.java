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

import com.hotels.heat.core.runner.TestBaseRunner;
import org.testng.ITestContext;

/**
 * This class contains utilities for logging.
 */
public final class LogUtils {

    private static final String NO_DATA_AVAILABLE = "NO_DATA_AVAILABLE";
    private ITestContext context;
    private String suiteName;
    private String testCaseId;
    private String testCaseStepId;
    private static LogUtils logUtils;

    public LogUtils(ITestContext context) {
        this.context = context;
        suiteName = this.context.getName();
        testCaseId = this.context.getAttribute(TestBaseRunner.CONTEXT_TEST_CASE_ID) != null ? this.context.getAttribute(TestBaseRunner.CONTEXT_TEST_CASE_ID).toString() : NO_DATA_AVAILABLE;
        testCaseStepId = this.context.getAttribute(TestBaseRunner.CONTEXT_TEST_CASE_STEP_ID) != null ? this.context.getAttribute(TestBaseRunner.CONTEXT_TEST_CASE_STEP_ID).toString() : NO_DATA_AVAILABLE;
    }

    /**
     * Singleton implementation for the object.
     * @return the singleton instance of the object
     */
    public static synchronized LogUtils getInstance(ITestContext context) {
        if (logUtils == null) {
            logUtils = new LogUtils(context);
        }
        return logUtils;
    }

    public String getCurrentTestDescription() {
        String descriptionString = "[" + suiteName;
        if (!testCaseId.equals(NO_DATA_AVAILABLE)) {
            descriptionString += "." + testCaseId;
        }
        if (!testCaseStepId.equals(NO_DATA_AVAILABLE)) {
            descriptionString += "][ STEP #" + testCaseStepId;
        }
        descriptionString += "]";
        return descriptionString;
    }

}
