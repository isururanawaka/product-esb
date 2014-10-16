package org.wso2.carbon.esb.json.test;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


import java.io.File;


public class ESBJAVA_3380TestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private final SimpleHttpClient httpClient = new SimpleHttpClient();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB",
                TestUserMode.SUPER_TENANT_ADMIN));
        serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation() + File.separator
                + "json" + File.separator + "synapse.properties"));
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/json/jsonproxy.xml");


    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL})
    @Test(groups = "wso2.esb", description = "disabling auto primitive option in synapse properties " ,enabled=false)
    public void testDisablingAutoConversionToScientificNotationInJsonStreamFormatter() throws Exception {
        String payload =
                "      <Person>" +
                        "         <ID>12999E105</ID>" +
                        "      </Person>";

        HttpResponse response = httpClient.doPost(getProxyServiceURLHttp("JsonProxy"),
                null, payload, "application/xml");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        response.getEntity().writeTo(bos);
        String exPayload = new String(bos.toByteArray());
        String val = "{" + "\"" + "Person" + "\"" + ":" + "{" + "\"ID\"" + ":" + "\"" + "12999E105" + "\"" + "}}";
        Assert.assertEquals(val, exPayload);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
