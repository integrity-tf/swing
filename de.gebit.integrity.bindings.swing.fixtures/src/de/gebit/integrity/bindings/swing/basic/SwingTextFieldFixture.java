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
import de.gebit.integrity.bindings.swing.exceptions.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.exceptions.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.exceptions.InvalidActionException;
import de.gebit.integrity.bindings.swing.exceptions.InvalidComponentPathException;
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

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempField.setText(aText);
			}
		});
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
	public Object getModel(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getText();
	}

	/**
	 * Enters the given model into the text field provided. For Swing, this must be a String, since Swing doesn't
	 * support model/view distinction for text fields.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @param aModel
	 *            the model for the text field
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Enter '$text$' in text field '$name$'")
	public void setModel(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "text") final Object aModel) throws AmbiguousComponentPathException,
			EventQueueTimeoutException, InvalidComponentPathException {
		if (!(aModel instanceof String)) {
			throw new IllegalArgumentException("The Swing TextField does not support models different from String.");
		}

		final JTextField tempField = findComponentGuarded(aComponentPath);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempField.setText((String) aModel);
			}
		});
	}

}
