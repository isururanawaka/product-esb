package org.wso2.carbon.esb.certificationvalidate.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.catalina.startup.Tomcat;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * Created by vijithae on 12/8/14.
 */
public class SecureConnectionTestCase extends ESBIntegrationTest {
	private final Tomcat tomcat = new Tomcat();
	private ServerConfigurationManager serverConfigurationManager;
	private final String separator = File.separator;
	private final String resourceFolderPath =
			getESBResourceLocation() + separator +
			"nhttp" + separator + "transport" + separator + "certificationvalidate" + separator;

	private LogViewerClient logViewer;

	@BeforeClass(alwaysRun = true)
	public void deployService() throws Exception {
		// Initializing server configuration
		super.init();
		//loadSampleESBConfiguration(150);
		serverConfigurationManager =
				new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		serverConfigurationManager.applyConfiguration(new File(resourceFolderPath + "axis2.xml"));
		//serverConfigurationManager.applyConfiguration(new File(resourceFolderPath + "carbon.xml"));

		super.init();
		loadESBConfigurationFromClasspath("/artifacts/ESB/nhttp/transport/certificationvalidate/simple_proxy.xml");



		AbstractSecureConnectionValidate abstractSecureConnectionValidate = new AbstractSecureConnectionValidate();
		abstractSecureConnectionValidate.createTomcat();
		Thread.sleep(1000);


		// Re-initializing server configuration
		//super.init();

		// Deploying the artifact defined in the ssl_check.xml

		//LogViewerClient logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
	}

	@Test(groups = "wso2.esb", description = "To check the SSL certificate failure redirect to fault sequence ")
	public void testSSLHandlingSequence() throws Exception {

		OMElement response =  axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttps("SampleProxy"), null,
		                                                  "WSO2");
		System.out.println(response.toString());
		Assert.assertTrue(response.toString().contains("WSO2"), "Asserting response for string 'WSO2'");
	}

	@AfterClass(alwaysRun = true)
	public void unDeployService() throws Exception {
		// Undeploying deployed artifact
		super.cleanup();
		tomcat.stop();
		tomcat.destroy();
	}
}
