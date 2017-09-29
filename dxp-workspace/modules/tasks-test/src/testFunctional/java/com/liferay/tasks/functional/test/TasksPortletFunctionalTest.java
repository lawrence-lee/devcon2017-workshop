package com.liferay.tasks.functional.test;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.liferay.arquillian.portal.annotation.PortalURL;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.tasks.integration.test.TasksPortletIntegrationTest;

import aQute.remote.util.JMXBundleDeployer;

@RunWith(Arquillian.class)
public class TasksPortletFunctionalTest {
	
	@AfterClass
	public static void cleanUpDependencies() throws Exception {
		new JMXBundleDeployer().uninstall(_tasksApiJarBSN);
		new JMXBundleDeployer().uninstall(_tasksServiceJarBSN);
		new JMXBundleDeployer().uninstall(_tasksWebWarBSN);
	}
	
	@Before
	public void addData() throws Exception {
		TasksPortletIntegrationTest.addTestEntry();
	}
	
	@After
	public void cleanUpData() throws Exception {
		TasksPortletIntegrationTest.cleanUpEntrys();
	}
	
	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(System.getProperty("jarFile"));
		
		final File tasksApiJar = new File(System.getProperty("tasksApiJarFile"));
		final File tasksServiceJar = new File(
			System.getProperty("tasksServiceJarFile"));
		final File tasksWebWar = new File(System.getProperty("tasksWebWarFile"));

		new JMXBundleDeployer().deploy(_tasksApiJarBSN, tasksApiJar);
		new JMXBundleDeployer().deploy(_tasksServiceJarBSN, tasksServiceJar);
		new JMXBundleDeployer().deploy(_tasksWebWarBSN, tasksWebWar);

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}
	
	@RunAsClient
	@Test
	public void testViewTask() throws InterruptedException, PortalException {
		_webDriver.get(_portletURL.toExternalForm());
		
		Assert.assertTrue(
			"Expected: TaskOne, but saw: " + _firstRowField1.getText(),
			_firstRowField1.getText().contains("TaskOne"));
	}
	
	@RunAsClient
	@Test
	public void testAssertTaskEntry() throws PortalException {
		_webDriver.get(_portletURL.toExternalForm());

		WebElement taskOneLink = _webDriver.findElement(By.linkText("TaskOne"));
		
		taskOneLink.click();
				
		_webDriver.switchTo().frame(_taskPortletIFrame);
		
		Assert.assertTrue(
			"Expected: Tasks Entry, but saw: " + _modalTitle.getText(),
			_modalTitle.getText().equals("Tasks Entry"));	
	}
	
	private static String _tasksApiJarBSN = "tasks-api";
	private static String _tasksServiceJarBSN = "tasks-service";
	private static String _tasksWebWarBSN = "tasks-portlet";

	
	@FindBy(xpath = "//h3[@class='modal-title']")
	private WebElement _modalTitle;
	
	@FindBy(xpath = "//div[contains(@id,'1_WAR_tasksportlet')]/table//..//tr/td[1]")
	private WebElement _firstRowField1;
	
	@FindBy(xpath = "//iframe[@id='_1_WAR_tasksportlet_Dialog_iframe_']")
	private WebElement _taskPortletIFrame;
	
	@Drone
	private WebDriver _webDriver;
	
	@PortalURL("1_WAR_tasksportlet")
	private URL _portletURL;

}
