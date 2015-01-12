package org.wso2.carbon.esb.certificationvalidate.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.catalina.startup.Tomcat;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.generic.MutualSSLClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vijithae on 12/8/14.
 */
public class SecureConnectionTestCase extends ESBIntegrationTest {
	private String trustStoreName = "client-truststore.jks";
	private String keyStoreName = "wso2carbon.jks";
	private String keyStorePassword = "wso2carbon";
	private String proxyService = "TestProxy";
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
		System.setProperty("javax.net.ssl.keyStore",  new File(
				CarbonUtils.getCarbonHome() + separator + "repository" + separator + "resources" + separator +
				"security" + separator + "wso2carbon.jks").getAbsolutePath());
		System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");

		//Set key store, this must contain the user private key
		//here we have use both trust store and key store as the same key store
		// But you can use a separate key store for key store an trust store.
		System.setProperty("javax.net.ssl.keyStore",  new File(
				CarbonUtils.getCarbonHome() + separator + "repository" + separator + "resources" + separator +
				"security" + separator + "wso2carbon.jks").getAbsolutePath());
		System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");

		super.init();
		loadESBConfigurationFromClasspath("/artifacts/ESB/nhttp/transport/certificationvalidate/simple_proxy.xml");
		//Set trust store, you need to import server's certificate of CA certificate chain in to this
		//key store




		AbstractSecureConnectionValidate abstractSecureConnectionValidate = new AbstractSecureConnectionValidate();
		abstractSecureConnectionValidate.createTomcat();
		Thread.sleep(1000);



		// Re-initializing server configuration
		//super.init();

		// Deploying the artifact defined in the ssl_check.xml

		//LogViewerClient logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
	}

	@Test(groups = "wso2.esb", description = "To check the SSL certificate failure redirect to fault sequence ")
	public void testSecureBackendCommunication  () throws Exception {

//		//load key store file
//		MutualSSLClient.loadKeyStore(FrameworkPathUtil.getSystemResourceLocation()
//		                             + File.separator + "keystores" + File.separator + "stratos"+ File.separator +
//		                             keyStoreName, keyStorePassword);
//
//		//load trust store file
//		MutualSSLClient.loadTrustStore(FrameworkPathUtil.getSystemResourceLocation()
//		                               + File.separator + "keystores" + File.separator + "stratos"+ File.separator +
//		                               trustStoreName, keyStorePassword);
//
//		//create ssl socket factory instance with given key/trust stores
//		MutualSSLClient.initMutualSSLConnection();



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
