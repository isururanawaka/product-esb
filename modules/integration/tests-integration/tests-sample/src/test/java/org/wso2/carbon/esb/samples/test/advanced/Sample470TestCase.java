/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.samples.test.advanced;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import java.io.File;

import static org.testng.Assert.assertNotNull;

public class Sample470TestCase extends ESBIntegrationTest {
    private boolean matched;
    private LogViewerClient logViewer;
    private ServerConfigurationManager serverManager = null;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        File sourceFile = new File(FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                "ESB" + File.separator + "spring" + File.separator + "log4j.properties");
        serverManager.applyConfiguration(sourceFile);
        super.init();
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        loadSampleESBConfiguration(470);

    }
   /* since this sample uses SimpleURLRegistry*/
    @Test(groups = "wso2.esb", description = "Tests level log")
    public void testSpringBeanAsMediator() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getMainSequenceURL(),getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                 "WSO2");
        assertNotNull(response, "Response message null");
        int beforeLogSize = logViewer.getAllSystemLogs().length;
        LogEvent[] logs = logViewer.getAllSystemLogs();
        for (int i = 0; i < beforeLogSize; i++) {
            if (logs[i].getMessage().contains("Starting Spring Meditor")) {
                matched = true;
            }
        }
        Assert.assertTrue(matched);

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
        serverManager.restoreToLastConfiguration();
    }

}
