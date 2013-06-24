/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JTextField;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.exceptions.InvalidActionException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * Abstract base class for fixtures accessing {@link JTextField}.
 * 
 * @param <C>
 *            the component type
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public abstract class AbstractSwingTextFieldFixture<C extends JTextField> extends AbstractSwingFixture<C> implements
		CustomProposalFixture {

	/**
	 * Returns the editability state for the specified control.
	 * 
	 * @param aComponentPath
	 *            the component path
	 * @return true if the control is editable, false if not
	 */
	@FixtureMethod(descriptionCall = "Get the editability state for control '$name$'", descriptionTest = "Check the editability state of control '$name$'")
	public Boolean isEditable(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).isEditable();
	}

	/**
	 * Returns the current text in the given text field. Can be used either as a call fixture method to retrieve the
	 * text into a variable, or as a test fixture to check the text against a reference text.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @return the text in the text field
	 */
	@FixtureMethod(descriptionCall = "Get the text currently entered in text field '$name$'", descriptionTest = "Check the text currently entered in text field '$name$'")
	public String getText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getText();
	}

	/**
	 * Enters the given text into the text field provided.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @param aText
	 *            the text in the text field
	 */
	@FixtureMethod(descriptionCall = "Enter '$text$' in text field '$name$'")
	public void setText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "text") final String aText) {
		final JTextField tempField = findComponentGuarded(aComponentPath);

		checkComponentEnabled(tempField);
		if (!tempField.isEditable()) {
			throw new InvalidActionException("The text field is not editable.");
		}

		// First, request the focus to be placed in the text field, to simulate user behavior of first focusing, then
		// entering text.
		focusComponent(tempField);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempField.setText(aText);
			}
		});

		// Switching the focus away is not part of this fixture method, since that isn't done consciously by the user as
		// well, but it simply happens on the next action.
	}

	/**
	 * Returns the current model in the given text field. Can be used either as a call fixture method to retrieve the
	 * model into a variable, or as a test fixture to check the text against a reference model.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @return the text in the text field
	 */
	@FixtureMethod(descriptionCall = "Get the text currently entered in text field '$name$'", descriptionTest = "Check the text currently entered in text field '$name$'")
	public Object getValue(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getText();
	}

	/**
	 * Enters the given value into the text field provided. For Swing, this must be a String, since Swing doesn't
	 * support model/view distinction for text fields.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @param aValue
	 *            the value for the text field
	 */
	@FixtureMethod(descriptionCall = "Enter '$text$' in text field '$name$'")
	public void setValue(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "text") final Object aValue) {
		if (!(aValue instanceof String)) {
			throw new IllegalArgumentException("The Swing TextField does not support models different from String.");
		}

		final JTextField tempField = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempField.setText((String) aValue);
			}
		});
	}

}
