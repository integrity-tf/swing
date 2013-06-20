/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.launch;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JFrame;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;
import de.gebit.integrity.runner.fixtures.JavaApplicationLaunchFixture;

/**
 * Swing applications are a bit...special. I need a special launching fixture for those...
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingApplicationLaunchFixture extends JavaApplicationLaunchFixture {

	/**
	 * The default timeout to wait for the applications' frame to appear.
	 */
	private static final long DEFAULT_FRAME_TIMEOUT = 30000;

	/**
	 * The default timeout to wait for a requested component to appear.
	 */
	private static final long DEFAULT_COMPONENT_TIMEOUT = 30000;

	/**
	 * The default number of frames to wait for until the application is considered "alive".
	 */
	private static final int DEFAULT_FRAME_COUNT = 1;

	/**
	 * Launches the provided application by calling the static main method of the application class and waiting the
	 * provided number of milliseconds to give the application some time to fully start up. This start method is
	 * timing-sensitive and should only be used as a last resort, if calling {@link #launch(String, String[])} and
	 * {@link #launchAndWaitForComponent(String, String[], String)} are not sufficient!
	 * 
	 * @param aMainClassName
	 *            the name of the application class
	 * @param someArguments
	 *            the arguments to provide to the main method
	 * @param aTimeToWait
	 *            the number of milliseconds to wait
	 * @return an application wrapper instance which can optionally be saved to handle multiple applications
	 * @throws Throwable
	 */
	@FixtureMethod(description = "Launch Java Swing Application '$mainClass$' and wait $time$ milliseconds.")
	public ApplicationWrapper launchAndWait(@FixtureParameter(name = "mainClass") String aMainClassName,
			@FixtureParameter(name = "arguments") String[] someArguments,
			@FixtureParameter(name = "time") int aTimeToWait) throws Throwable {
		ApplicationWrapper tempWrapper = super.launch(aMainClassName, someArguments);

		if (aTimeToWait > 0) {
			try {
				Thread.sleep(aTimeToWait);
			} catch (InterruptedException exc) {
				// ignore
			}
		}

		return tempWrapper;
	}

	/**
	 * Launches the provided application by calling the static main method of the application class and waiting for a
	 * specified component to appear.
	 * 
	 * @param aMainClassName
	 *            the name of the application class
	 * @param someArguments
	 *            the arguments to provide to the main method
	 * @param aComponentPath
	 *            the component to wait for
	 * @return an application wrapper instance which can optionally be saved to handle multiple applications
	 * @throws Throwable
	 */
	@FixtureMethod(description = "Launch Java Swing Application '$mainClass$' and wait for component '$component$' to appear.")
	public ApplicationWrapper launchAndWaitForComponent(@FixtureParameter(name = "mainClass") String aMainClassName,
			@FixtureParameter(name = "arguments") String[] someArguments,
			@FixtureParameter(name = "component") String aComponentPath) throws Throwable {
		ApplicationWrapper tempWrapper = super.launch(aMainClassName, someArguments);

		if (aComponentPath != null) {
			ComponentWaiter tempWaiter = new ComponentWaiter(getComponentTimeout(), aComponentPath);
			tempWaiter.start();
			tempWaiter.wasSuccessful();
		}

		return tempWrapper;
	}

	@Override
	protected boolean checkWrapper(ApplicationWrapper aWrapper) throws Throwable {
		FrameWaiter tempWaiter = new FrameWaiter(getFrameTimeout(), getFrameCount());

		tempWaiter.start();
		return tempWaiter.wasSuccessful();
	}

	@Override
	protected boolean isAliveInternal(ApplicationWrapper aWrapper) {
		// A Swing application is alive if its AWT Eventqueue is alive
		Thread[] tempArray = new Thread[1000];
		Thread.enumerate(tempArray);

		for (Thread tempThread : tempArray) {
			if (tempThread.getName().startsWith("AWT-EventQueue-")) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean killInternal(ApplicationWrapper aWrapper) {
		// A Swing application is killed by closing all frames. This of course
		// leaves the Integrity thread, but
		// that must be allowed to kill itself after finishing all tests and
		// writing all results.
		for (Window tempWindow : Window.getWindows()) {
			if (tempWindow instanceof JFrame) {
				((JFrame) tempWindow).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
			tempWindow.dispose();
		}

		return super.killInternal(aWrapper);
	}

	protected long getFrameTimeout() {
		return DEFAULT_FRAME_TIMEOUT;
	}

	protected long getComponentTimeout() {
		return DEFAULT_COMPONENT_TIMEOUT;
	}

	protected int getFrameCount() {
		return DEFAULT_FRAME_COUNT;
	}

	/**
	 * Waits for the applications' frame(s) to come up after launch.
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	protected class FrameWaiter extends Thread {

		/**
		 * The nanoseconds to wait for.
		 */
		private long timeoutNanos;

		/**
		 * The number of frames to wait for.
		 */
		private int numberOfFrames;

		/**
		 * Whether the waiting is considered successful.
		 */
		private boolean waitSuccessful;

		/**
		 * Creates an instance.
		 */
		public FrameWaiter(long aTimeout, int aNumberOfFrames) {
			timeoutNanos = aTimeout * 1000000L;
			numberOfFrames = aNumberOfFrames;
		}

		@Override
		public void run() {
			long tempStart = System.nanoTime();

			while (tempStart + timeoutNanos > System.nanoTime()) {
				if (areFramesVisible() && isEventThreadReactive()) {
					waitSuccessful = true;
					return;
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException exc) {
					// ignored
				}
			}

			waitSuccessful = false;
		}

		/**
		 * Checks whether enough frames have become visible.
		 * 
		 * @return true or false
		 */
		protected boolean areFramesVisible() {
			int tempVisibleCount = 0;

			for (Frame tempFrame : Frame.getFrames()) {
				if (tempFrame.isVisible()) {
					tempVisibleCount++;
				}
			}

			return (tempVisibleCount >= numberOfFrames);
		}

		/**
		 * Wait for the thread to exit and check the success.
		 * 
		 * @return
		 */
		public boolean wasSuccessful() {
			try {
				join();
			} catch (InterruptedException exc) {
				// ignore
			}

			return waitSuccessful;
		}

	}

	/**
	 * Waits for a specific component to come up after launch.
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	protected class ComponentWaiter extends Thread {

		/**
		 * The nanoseconds to wait for.
		 */
		private long timeoutNanos;

		/**
		 * The components' path.
		 */
		private String componentPath;

		/**
		 * Whether the waiting is considered successful.
		 */
		private boolean waitSuccessful;

		/**
		 * Creates an instance.
		 */
		public ComponentWaiter(long aTimeout, String aComponentPath) {
			timeoutNanos = aTimeout * 1000000L;
			componentPath = aComponentPath;
		}

		@Override
		public void run() {
			long tempStart = System.nanoTime();

			while (tempStart + timeoutNanos > System.nanoTime()) {
				if (componentExists() && isEventThreadReactive()) {
					waitSuccessful = true;
					return;
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException exc) {
					// ignored
				}
			}

			waitSuccessful = false;
		}

		/**
		 * Checks whether the component to be found exists.
		 * 
		 * @return true or false
		 */
		protected boolean componentExists() {
			List<Component> tempList = new AbstractSwingComponentHandler() {
			}.findComponents(componentPath, null, null);

			return !(tempList.isEmpty());
		}

		/**
		 * Wait for the thread to exit and check the success.
		 * 
		 * @return
		 */
		public boolean wasSuccessful() {
			try {
				join();
			} catch (InterruptedException exc) {
				// ignore
			}

			return waitSuccessful;
		}

	}

	/**
	 * Checks whether the AWT event thread is reacting and processing messages.
	 * 
	 * @return true if it is processing messages, false if not.
	 */
	protected boolean isEventThreadReactive() {
		try {
			EventQueue.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					return;
				}
			});

			return true;
		} catch (InterruptedException exc) {
			exc.printStackTrace();
		} catch (InvocationTargetException exc) {
			exc.printStackTrace();
		}

		return false;
	}

}
