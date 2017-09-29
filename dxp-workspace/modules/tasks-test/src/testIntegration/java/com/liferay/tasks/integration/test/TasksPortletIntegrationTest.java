package com.liferay.tasks.integration.test;

import java.io.File;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.tasks.model.TasksEntry;
import com.liferay.tasks.service.TasksEntryLocalServiceUtil;
import com.liferay.tasks.service.TasksEntryServiceUtil;

import aQute.remote.util.JMXBundleDeployer;

@RunWith(Arquillian.class)
public class TasksPortletIntegrationTest {

	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(System.getProperty("jarFile"));
		
		final File tasksApiJar = new File(System.getProperty("tasksApiJarFile"));
		final File tasksServiceJar = new File(
			System.getProperty("tasksServiceJarFile"));

		new JMXBundleDeployer().deploy(_tasksApiJarBSN, tasksApiJar);
		new JMXBundleDeployer().deploy(_tasksServiceJarBSN, tasksServiceJar);

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}
	
	@AfterClass
	public static void cleanUpDependencies() throws Exception {
		new JMXBundleDeployer().uninstall(_tasksApiJarBSN);
		new JMXBundleDeployer().uninstall(_tasksServiceJarBSN);
	}
	
	@Before
	public void setUpTest() throws Exception {
		cleanUpEntrys();
	}
	
	@After
	public void cleanUpData() throws Exception {
		cleanUpEntrys();
	}
	
	@Test
	public void testAddTask() throws PortalException {
		TasksEntry tasksEntry = addTestEntry();
		
		long tasksEntryId = tasksEntry.getTasksEntryId();
		
		TasksEntryLocalServiceUtil.getTasksEntry(tasksEntryId);
			
		Assert.assertTrue(TasksEntryLocalServiceUtil.getTasksEntry(tasksEntryId).getTitle().equals("TaskOne"));
	}
	
	@Test
	public void testDeleteTask() throws PortalException {
		TasksEntry tasksEntry = addTestEntry();
		
		long tasksEntryId = tasksEntry.getTasksEntryId();
		
		TasksEntryLocalServiceUtil.deleteTasksEntry(tasksEntryId);
		
		Assert.assertTrue(TasksEntryLocalServiceUtil.getTasksEntriesCount() == 0);
	}
	
	public static TasksEntry addTestEntry() throws SystemException, PortalException {
		ServiceContext serviceContext = new ServiceContext();
		long assigneeUserId = new Long(1);

		TasksEntry tasksEntry = 				
			TasksEntryServiceUtil.addTasksEntry("TaskOne", 20143, assigneeUserId, 1, 1, 1, 0, 0, true, serviceContext);
			
		return tasksEntry;
		
	}
	
	public static void cleanUpEntrys() throws SystemException, PortalException {
		List<TasksEntry> tasksEntries = TasksEntryLocalServiceUtil.getTasksEntries(-1, -1);
		
		if (!tasksEntries.isEmpty()) {
			for (TasksEntry tasksEntry : tasksEntries) {
				TasksEntryLocalServiceUtil.deleteTasksEntry(tasksEntry);
			}
		}
		
	}
	
	private static String _tasksApiJarBSN = "com.liferay.tasks.api";
	private static String _tasksServiceJarBSN = "com.liferay.tasks.service";	
}
