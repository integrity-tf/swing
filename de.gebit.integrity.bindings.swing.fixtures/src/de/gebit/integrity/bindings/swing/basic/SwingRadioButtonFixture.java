/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JRadioButton;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.exceptions.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.exceptions.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.exceptions.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture deals with {@link JRadioButton} instances.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingRadioButtonFixture extends AbstractSwingFixture<JRadioButton> implements CustomProposalFixture {

	/**
	 * Returns the text on the provided radio button. Can be used either as a test or a call fixture, in order to either
	 * compare the text with a given expected text or return the current text for storage in a variable.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @return the text currently displayed on the label
	 */
	@FixtureMethod(descriptionCall = "Get the text displayed on radio button '$name$'", descriptionTest = "Check the text displayed on radio button '$name$'")
	public String getText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getText();
	}

	/**
	 * Sets the text on a specific radio button.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @param aText
	 *            the new text to be displayed on the label
	 */
	@FixtureMethod(description = "Set the text displayed on label '$name$' to '$text'")
	public void setText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "text") final String aText) throws AmbiguousComponentPathException,
			InvalidComponentPathException, EventQueueTimeoutException {
		final JRadioButton tempRadioButton = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempRadioButton.setText(aText);
			}
		});
	}

	/**
	 * Checks (turns on) the given radio button by simulating a click on the button.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 */
	@FixtureMethod(descriptionCall = "Check the radio button '$name$'")
	public void select(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		final JRadioButton tempRadioButton = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempRadioButton.doClick();
			}
		});
	}

	/**
	 * Returns the current state (checked / not checked) of the given radio button. Can be used either as a test or call
	 * fixture method, in order to verify the state of a button or retrieve the state into a variable.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @return true if the button is checked, false if not
	 */
	@FixtureMethod(descriptionCall = "Get the state of the radio button '$name$'", descriptionTest = "Check the state of the radio button '$name'")
	public boolean isSelected(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).isSelected();
	}

}
