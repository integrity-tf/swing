/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing;

import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.swing.JComponent;
import javax.swing.JDialog;

import de.gebit.integrity.bindings.swing.exceptions.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.exceptions.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.exceptions.InvalidComponentPathException;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * Abstract base class for Swing component fixtures.
 * 
 * @param <T>
 *            The component type.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public abstract class AbstractSwingFixture<T extends JComponent> extends AbstractSwingComponentHandler {

	//
	// Generally useful fixture methods applicable to all controls
	//

	/**
	 * Returns the enable/disable state for the specified control.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @return true if the control is enabled, false if it is disabled
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the enablement state for control '$name$'", descriptionTest = "Check the enablement state of control '$name$'")
	public Boolean isEnabled(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		return findComponentGuarded(aComponentPath, getComponentClass(), null).isEnabled();
	}

	/**
	 * Sets the enable/disable state of the specified control.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @param anEnabledFlag
	 *            true if the control shall be enabled, false if it shall be disabled
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(description = "Set the enablement state for control '$name$' to '$enabled$'")
	public void setEnabled(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "enabled") final Boolean anEnabledFlag) throws AmbiguousComponentPathException,
			EventQueueTimeoutException, InvalidComponentPathException {
		final JComponent tempComponent = findComponentGuarded(aComponentPath, getComponentClass(), null);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempComponent.setEnabled(anEnabledFlag);
			}
		});
	}

	/**
	 * Returns the visibility state for the specified control.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @return true if the control is visible, false if it is invisible
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the visibility state for control '$name$'", descriptionTest = "Check the visibility state of control '$name$'")
	public Boolean isVisible(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		return findComponentGuarded(aComponentPath, getComponentClass(), null).isVisible();
	}

	/**
	 * Sets the visibility state of the specified control.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @param aVisibilityFlag
	 *            true if the control shall be visible, false if it shall be invisible
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(description = "Set the visibility state for control '$name$' to '$visible$'")
	public void setVisible(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "visible") final Boolean aVisibilityFlag) throws AmbiguousComponentPathException,
			EventQueueTimeoutException, InvalidComponentPathException {
		final JComponent tempComponent = findComponentGuarded(aComponentPath, getComponentClass(), null);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempComponent.setVisible(aVisibilityFlag);
			}
		});
	}

	/**
	 * This must return the actual class of the component for which the sub-fixture is written.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends JComponent> getComponentClass() {
		Class<?> tempClass = getClass();
		Type tempType;

		do {
			tempType = tempClass.getGenericSuperclass();
			if (tempType instanceof ParameterizedType) {
				return (Class<? extends JComponent>) ((ParameterizedType) tempType).getActualTypeArguments()[0];
			} else if (tempType instanceof Class) {
				tempClass = (Class<? extends JComponent>) tempType;
			} else {
				tempType = null;
			}
		} while (tempType != null);

		return null;
	}

	//
	// Utility functionality for all fixtures
	//

	/**
	 * Finds a component matching the given path and the component class returned by {@link #getComponentClass()} in all
	 * open windows. This method will return either one match or throw an exception.
	 * 
	 * @param aComponentPath
	 *            the component path to filter for, or null if no path filtering shall be done
	 * @return a match
	 * @throws AmbiguousComponentPathException
	 *             if there are more than one matching components
	 * @throws InvalidComponentPathException
	 *             if there is not even a single matching component
	 */
	@SuppressWarnings("unchecked")
	public T findComponentGuarded(String aComponentPath) throws AmbiguousComponentPathException,
			InvalidComponentPathException {
		return (T) findComponentGuarded(aComponentPath, getComponentClass(), null);
	}

	/**
	 * The default time to wait for the event thread to become ready.
	 */
	protected static final int DEFAULT_EVENT_QUEUE_WAIT_TIMEOUT = 30000;

	/**
	 * The default number of times to wait for the event thread to become ready.
	 */
	protected static final int DEFAULT_EVENT_QUEUE_WAIT_COUNT = 1;

	/**
	 * Run the provided {@link Runnable} on the event queue.
	 * 
	 * @param aRunnable
	 */
	protected void runOnEventQueue(Runnable aRunnable) {
		EventQueue.invokeLater(aRunnable);
	}

	/**
	 * Run the provided {@link Runnable} on the event queue and then wait for the event queue to settle down.
	 * 
	 * @param aRunnable
	 *            the runnable
	 * @throws EventQueueTimeoutException
	 */
	protected void runOnEventQueueAndWait(Runnable aRunnable) throws EventQueueTimeoutException {
		runOnEventQueue(aRunnable);
		waitForEventQueue();
		// Now the runnable has been processed, but depending on what the
		// runnable did, further events might have been
		// pushed onto the queue (as a result of changing a controls' value, for
		// example). We want to wait for those
		// as well, so we put another wait behind:
		waitForEventQueue();
	}

	/**
	 * Wait for the event queue to process all pending messages.
	 * 
	 * @throws EventQueueTimeoutException
	 */
	protected void waitForEventQueue() throws EventQueueTimeoutException {
		if (!new EventQueueSynchronizer().waitForEventQueueMultipleTimes(getEventQueueWaitTimeout(),
				getEventQueueWaitCount())) {
			throw new EventQueueTimeoutException("Timed out while waiting for event queue ("
					+ getEventQueueWaitTimeout() + " msecs)");
		}
	}

	/**
	 * Wait for the event queue a specified number of times to process all pending messages.
	 * 
	 * @throws EventQueueTimeoutException
	 */
	protected void waitForEventQueueMultipleTimes(int aNumberOfTimes) throws EventQueueTimeoutException {
		if (!new EventQueueSynchronizer().waitForEventQueueMultipleTimes(getEventQueueWaitTimeout(), aNumberOfTimes)) {
			throw new EventQueueTimeoutException("Timed out while waiting for event queue ("
					+ getEventQueueWaitTimeout() + " msecs)");
		}
	}

	/**
	 * Returns the number of times to wait for the event queue. In some cases, one might need to wait multiple times,
	 * for example if events being executed add more events on the event queue (waiting for the queue is performed by
	 * pushing an event on the queue and waiting for it to be processed, so events added after that special event was
	 * added will not be waited for).
	 * 
	 */
	protected int getEventQueueWaitCount() {
		return DEFAULT_EVENT_QUEUE_WAIT_COUNT;
	}

	/**
	 * The timeout to use when waiting for the event queue.
	 */
	protected int getEventQueueWaitTimeout() {
		return DEFAULT_EVENT_QUEUE_WAIT_TIMEOUT;
	}

	/**
	 * Finds the currently focused dialog. Returns null if no dialog is in focus.
	 * 
	 * @return the focused dialog or null
	 */
	protected JDialog findFocusedDialog() {
		Window tempFocused = findFocusedWindow();
		if (tempFocused instanceof JDialog) {
			return (JDialog) tempFocused;
		}

		return null;
	}

	/**
	 * Finds the currently focused dialog. This throws an {@link IllegalStateException} if no focused dialog was found.
	 * 
	 * @return the dialog
	 */
	protected JDialog findFocusedDialogGuarded() {
		JDialog tempDialog = findFocusedDialog();
		if (tempDialog == null) {
			throw new IllegalStateException("No focused dialog was found!");
		} else {
			return tempDialog;
		}
	}

	/**
	 * Finds the focused window. This throws an {@link IllegalStateException} if no focused window was found.
	 * 
	 * @return the window
	 */
	protected Window findFocusedWindowGuarded() {
		Window tempWindow = findFocusedWindow();
		if (tempWindow == null) {
			throw new IllegalStateException("No focused window was found!");
		} else {
			return tempWindow;
		}
	}

	/**
	 * Finds the window currently in focus.
	 * 
	 * @return The window or null if no window is currently in focus
	 */
	protected Window findFocusedWindow() {
		return new FocusWindowFinder().findFocusedWindow();
	}

	/**
	 * Converts an object to a string, but in a null-safe manner. If the object is null, this also returns null (not an
	 * empty string or "null" as a string, but the value null).
	 * 
	 * @param anObject
	 * @return a string or null
	 */
	protected String nullSafeToString(Object anObject) {
		if (anObject == null) {
			return null;
		} else {
			return anObject.toString();
		}
	}

	/**
	 * Finds the window currently in focus.
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	protected static class FocusWindowFinder {

		/**
		 * Used to store the window.
		 */
		private Window window;

		/**
		 * Finds the currently focused window.
		 * 
		 * @return the window or null if none is in focus.
		 */
		public Window findFocusedWindow() {
			try {
				EventQueue.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
					}
				});
			} catch (InterruptedException exc) {
				exc.printStackTrace();
			} catch (InvocationTargetException exc) {
				exc.printStackTrace();
			}

			return window;
		}

	}

}
