package com.liferay.tasks.functional.test;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.liferay.arquillian.portal.annotation.PortalURL;
import com.liferay.portal.kernel.exception.PortalException;

import aQute.remote.util.JMXBundleDeployer;

@RunWith(Arquillian.class)
public class TasksPortletFunctionalTest {
	
	@AfterClass
	public static void cleanUpDependencies() throws Exception {
		new JMXBundleDeployer().uninstall(_tasksApiJarBSN);
		new JMXBundleDeployer().uninstall(_tasksWebWarBSN);
	}

	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(System.getProperty("jarFile"));
		final File tasksApiJar = new File(System.getProperty("tasksApiJarFile"));

		new JMXBundleDeployer().deploy(_tasksApiJarBSN, tasksApiJar);
		
		final File _projectDir = new File(System.getProperty("projectDir"));
		final File _warsDir = new File(_projectDir, "wars");
		final File _projectPath = new File(_warsDir, "tasks-web");
				
		final ProcessBuilder processBuilder = new ProcessBuilder(
			_projectDir.getAbsolutePath() + "/gradlew", "deploy", "-p" + _projectPath.getAbsolutePath());

	    final Process process = processBuilder.start();

	    process.waitFor();

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}
	
	@RunAsClient
	@Test
	public void testAddPortlet() throws InterruptedException, PortalException {
		_webDriver.get(_portletURL.toExternalForm());
		
		Assert.assertTrue(_tasksPortlet.isDisplayed());
	}
	
	private static String _tasksApiJarBSN = "com.liferay.tasks.api";
	private static String _tasksWebWarBSN = "tasks-portlet";

	@FindBy(xpath = "//div[contains(@id,'1_WAR_tasksportlet')]")
	private WebElement _tasksPortlet;
	
	@Drone
	private WebDriver _webDriver;
	
	@PortalURL("1_WAR_tasksportlet")
	private URL _portletURL;

}
