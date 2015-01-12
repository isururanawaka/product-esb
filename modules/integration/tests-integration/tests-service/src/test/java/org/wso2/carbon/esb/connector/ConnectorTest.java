/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.connector;

import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;

public class ConnectorTest extends ESBIntegrationTest {

    private String repoLocation = TestConfigurationProvider.getResourceLocation() +
                                  File.separator + "artifacts" + File.separator + "ESB" +
                                  File.separator + "connector" + File.separator;

    private String salesforceConnectorFileName = "echo-connector-1.0.0.zip";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadConnector(repoLocation, salesforceConnectorFileName);
        Thread.sleep(30000);
        updateConnectorStatus("{org.wso2.carbon.connectors}echo", "echo", "org.wso2.carbon.connectors", "enabled");
    }

    @Test(groups = {"wso2.esb"}, description = "Echo Connector integration test.")
    public void testEchoConnector() throws Exception {
        String[] imports = getAllImports();
        boolean passed = false;
        for (String impor : imports) {
            if (impor.equals("{org.wso2.carbon.connectors}echo")) {
                passed = true;
            }
        }
        Assert.assertTrue(passed);
    }

    @Override
    protected void cleanup() throws Exception {
        super.cleanup();
        deleteLibrary("{org.wso2.carbon.connectors}echo");
    }

}
