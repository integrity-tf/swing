/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JSlider;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * Abstract base class for fixtures dealing with {@link JSlider}.
 * 
 * @param <C>
 *            the component type
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public abstract class AbstractSwingSliderFixture<C extends JSlider> extends AbstractSwingFixture<C> implements
		CustomProposalFixture {

	/**
	 * Gets the minimum value of the named slider.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 */
	@FixtureMethod(descriptionCall = "Get the minimum value of slider '$name$'", descriptionTest = "Check the minimum value of slider '$name$'")
	public Integer getMinValue(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getMinimum();
	}

	/**
	 * Gets the maximum value of the named slider.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 */
	@FixtureMethod(descriptionCall = "Get the maximum value of slider '$name$'", descriptionTest = "Check the maximum value of slider '$name$'")
	public Integer getMaxValue(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getMaximum();
	}

	/**
	 * Gets the currently selected value of the named slider.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 */
	@FixtureMethod(descriptionCall = "Get the current value of slider '$name$'", descriptionTest = "Check the current value of slider '$name$'")
	public Integer getValue(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getValue();
	}

	/**
	 * Sets the current value of the named slider.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 */
	@FixtureMethod(description = "Set the slider '$name$' to a value of $value$")
	public void setValue(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "value") final Integer aValue) {
		final JSlider tempSlider = findComponentGuarded(aComponentPath);

		checkComponentEnabled(tempSlider);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempSlider.setValue(aValue);
			}
		});
	}

}
