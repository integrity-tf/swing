/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JComponent;
import javax.swing.JTextField;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture provides access to {@link JTextField} instances.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingTextFieldFixture extends AbstractSwingFixture<JTextField> implements CustomProposalFixture {

	/**
	 * Returns the current text in the given text field. Can be used either as a call fixture method to retrieve the
	 * text into a variable, or as a test fixture to check the text against a reference text.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @return the text in the text field
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the text currently entered in text field '$name$'", descriptionTest = "Check the text currently entered in text field '$name$'")
	public String getTextFieldContent(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		return findComponentGuarded(aComponentPath).getText();
	}

	/**
	 * Enters the given text into the text field provided.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @param aText
	 *            the text in the text field
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Enter '$text$' in text field '$name$'")
	public void setTextFieldContent(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "text") final String aText) throws AmbiguousComponentPathException,
			EventQueueTimeoutException, InvalidComponentPathException {
		final JTextField tempField = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempField.setText(aText);
			}
		});
	}

	/**
	 * Returns the editability state for the specified control.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @return true if the control is editable, false if not
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the editability state for control '$name$'", descriptionTest = "Check the editability state of control '$name$'")
	public Boolean isEditable(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
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

}
