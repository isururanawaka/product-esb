package org.wso2.carbon.esb.mediationstats.test;
/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

public class MediationStatEnableTestCase extends ESBIntegrationTest {
	private final String resourceFolderPath =
			getESBResourceLocation() + File.separator +
			"localEntryConfig" + File.separator;
	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		ServerConfigurationManager serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		serverConfigurationManager.applyConfiguration(new File(resourceFolderPath + "carbon.xml"));
		super.init();
		loadSampleESBConfiguration(150);
	}

	@Test(groups = { "wso2.esb" }, description = "Introduction to Proxy Services")
	public void testFileProcessing() throws Exception {
		OMElement response = axis2Client.sendSimpleStockQuoteRequest(
				getProxyServiceURLHttp("StockQuoteProxy"), null, "WSO2");

		Assert.assertTrue(response.toString().contains("GetQuoteResponse"),
		                  "GetQuoteResponse not found");
		Assert.assertTrue(response.toString().contains("WSO2 Company"), "WSO2 Company not found");
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}
}
