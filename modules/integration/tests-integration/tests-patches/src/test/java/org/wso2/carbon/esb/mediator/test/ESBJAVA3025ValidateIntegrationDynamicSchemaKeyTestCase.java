/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class ESBJAVA3025ValidateIntegrationDynamicSchemaKeyTestCase extends ESBIntegrationTest{

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/synapseconfig/filters/validate/synapse_config.xml");
    }

    /**
     * Test Scenario: Add two schemas as local entries. Create a sequence
     * template and add a validate mediator inside it. (Here, the schema key is
     * calculated dynamically by template input parameters.) Add two proxy
     * services that call the same template using two different schema keys.
     * Send a request using "testProxy" and check whether validation happens
     * according to schema "a" Then send a request using "testProxy2" and check
     * whether validation happens according to schema "b"
     *
     * Test artifacts: /synapseconfig/filters/validate/synapse_config.xml
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb")
    public void validateMediatorDynamicSchemaChangeTest() throws Exception {
        String requestPayload1 = "<level1><a><b>222</b></a></level1>";
        String requestPayload2 = "<level1><c><d>333</d></c></level1>";

        OMElement payload1 = AXIOMUtil.stringToOM(requestPayload1);
        OMElement payload2 = AXIOMUtil.stringToOM(requestPayload2);

        OMElement response1 = axis2Client.send(getProxyServiceURLHttp("testProxy"),
                null, "mediate", payload1);
        Assert.assertTrue(response1.toString().contains("ValidateSuccess"),
                "Validate failed with schema a.");

        OMElement response2 = axis2Client.send(
                getProxyServiceURLHttp("testProxy2"), null, "mediate", payload2);
        Assert.assertTrue(response2.toString().contains("ValidateSuccess"),
                "Validate failed with schema b.");
    }

    @AfterClass(alwaysRun = true)
    public void clear() throws Exception {
        super.cleanup();
    }

}