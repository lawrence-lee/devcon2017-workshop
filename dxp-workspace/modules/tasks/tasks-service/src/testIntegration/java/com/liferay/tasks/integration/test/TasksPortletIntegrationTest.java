package com.liferay.tasks.integration.test;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.portal.kernel.exception.PortalException;

import aQute.remote.util.JMXBundleDeployer;

@RunWith(Arquillian.class)
public class TasksPortletIntegrationTest {

	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(System.getProperty("jarFile"));
		
		final File tasksApiJar = new File(System.getProperty("tasksApiJarFile"));

		new JMXBundleDeployer().deploy(_tasksApiJarBSN, tasksApiJar);

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}
	
	@AfterClass
	public static void cleanUpDependencies() throws Exception {
		new JMXBundleDeployer().uninstall(_tasksApiJarBSN);
	}
	

	@Test
	public void testSampleIntegration() throws PortalException {
		//need to execute ./gradlew --info to see sysout
		
		System.out.println("The Integration test ran successfully!");
	}
	
	private static String _tasksApiJarBSN = "com.liferay.tasks.api";
}
