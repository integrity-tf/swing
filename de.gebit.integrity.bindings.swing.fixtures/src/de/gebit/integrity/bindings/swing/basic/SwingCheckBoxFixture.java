/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JCheckBox;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.exceptions.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.exceptions.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.exceptions.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture deals with {@link JCheckBox} instances.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingCheckBoxFixture extends AbstractSwingFixture<JCheckBox> implements CustomProposalFixture {

	/**
	 * Returns the text on the provided checkbox. Can be used either as a test or a call fixture, in order to either
	 * compare the text with a given expected text or return the current text for storage in a variable.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @return the text currently in the checkbox
	 */
	@FixtureMethod(descriptionCall = "Get the text displayed on button '$name$'", descriptionTest = "Check the text displayed on button '$name$'")
	public String getText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getText();
	}

	/**
	 * Sets the text on a specified checkbox.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @param aText
	 *            the new text to be displayed on the checkbox
	 */
	@FixtureMethod(description = "Set the text displayed on button '$name$' to '$text'")
	public void setText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "text") final String aText) throws AmbiguousComponentPathException,
			InvalidComponentPathException, EventQueueTimeoutException {
		final JCheckBox tempCheckBox = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempCheckBox.setText(aText);
			}
		});
	}

	/**
	 * Checks (turns on) the given checkbox.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 */
	@FixtureMethod(descriptionCall = "Check the checkbox '$name$'")
	public void check(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		final JCheckBox tempBox = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempBox.setSelected(true);
			}
		});
	}

	/**
	 * Unchecks (turns off) the given checkbox.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 */
	@FixtureMethod(descriptionCall = "Uncheck the checkbox '$name$'")
	public void uncheck(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		final JCheckBox tempBox = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempBox.setSelected(false);
			}
		});
	}

	/**
	 * Toggles the given checkbox (if checked, it becomes unchecked, and vice versa).
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 */
	@FixtureMethod(descriptionCall = "Toggle the radio button '$name$'")
	public void toggle(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		final JCheckBox tempBox = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempBox.setSelected(!tempBox.isSelected());
			}
		});
	}

	/**
	 * Returns the current state (checked / not checked) of the given checkbox. Can be used either as a test or call
	 * fixture method, in order to verify the state of a box or retrieve the state into a variable.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @return true if the button is checked, false if not
	 */
	@FixtureMethod(descriptionCall = "Get the state of the checkbox '$name$'", descriptionTest = "Check the state of the checkbox '$name'")
	public boolean isChecked(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).isSelected();
	}

}
