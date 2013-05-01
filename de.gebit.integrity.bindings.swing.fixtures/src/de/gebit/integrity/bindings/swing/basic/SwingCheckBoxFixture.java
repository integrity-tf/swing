/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture deals with {@link JRadioButton} instances.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingCheckBoxFixture extends AbstractSwingFixture implements CustomProposalFixture {

	/**
	 * Checks (turns on) the given checkbox.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Check the checkbox '$name$'")
	public void checkCheckBox(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JCheckBox tempBox = findComponentGuarded(aComponentPath, JCheckBox.class, null);

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
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Uncheck the checkbox '$name$'")
	public void uncheckCheckBox(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JCheckBox tempBox = findComponentGuarded(aComponentPath, JCheckBox.class, null);

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
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Toggle the radio button '$name$'")
	public void toggleCheckBox(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JCheckBox tempBox = findComponentGuarded(aComponentPath, JCheckBox.class, null);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempBox.setSelected(!tempBox.isSelected());
			}
		});
	}

	/**
	 * Checks or unchecks the given checkbox, depending on the parameter provided.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Set the checkboxes' '$name$' checked state to $checked$")
	public void setCheckBox(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "checked") Boolean aCheckedFlag) throws AmbiguousComponentPathException,
			EventQueueTimeoutException, InvalidComponentPathException {
		if (Boolean.TRUE.equals(aCheckedFlag)) {
			checkCheckBox(aComponentPath);
		} else {
			uncheckCheckBox(aComponentPath);
		}
	}

	/**
	 * Returns the current state (checked / not checked) of the given checkbox. Can be used either as a test or call
	 * fixture method, in order to verify the state of a box or retrieve the state into a variable.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @return true if the button is checked, false if not
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the state of the checkbox '$name$'", descriptionTest = "Check the state of the checkbox '$name'")
	public boolean getCheckBoxState(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		return findComponentGuarded(aComponentPath, JRadioButton.class, null).isSelected();
	}

}
