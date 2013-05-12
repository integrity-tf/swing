/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JList;
import javax.swing.ListModel;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture provides access to {@link JList} instances.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingListFixture extends AbstractSwingFixture implements CustomProposalFixture {

	/**
	 * The last position; this is used in case of repeated invocations because of tabletests to track which position
	 * must be checked next.
	 */
	protected int nextPosition;

	/**
	 * Returns the current element at the given position in the given list, starting at the top. Can be used either as a
	 * call fixture method to retrieve the text into a variable, or as a test fixture to check the text against a
	 * reference text.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @param aListPosition
	 *            the position in the list (one-based!), omit for automatic position calculation in tabletests
	 * @return the element in the list at the given position, or null if the position is larger than the available
	 *         entries
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the element at position $position$ in list '$name$'", descriptionTest = "Check the element at position $position$ in list '$name$'")
	public Object getListEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "position") Integer aListPosition) throws AmbiguousComponentPathException,
			EventQueueTimeoutException, InvalidComponentPathException {
		return getListContent(aComponentPath, aListPosition);
	}

	/**
	 * Enters the given text into the text field provided.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the number of entries in list '$text$'", descriptionTest = "Check the number of entries in list '$text$'")
	public Integer getListEntryCount(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		return findComponentGuarded(aComponentPath, JList.class, null).getModel().getSize();
	}

	/**
	 * Fetches the content from the given list.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aPosition
	 *            the position in the list (one-based!)
	 * @return the object
	 * @throws AmbiguousComponentPathException
	 * @throws InvalidComponentPathException
	 */
	protected Object getListContent(String aComponentPath, Integer aPosition) throws AmbiguousComponentPathException,
			InvalidComponentPathException {
		JList tempList = findComponentGuarded(aComponentPath, JList.class, null);
		ListModel tempModel = tempList.getModel();

		int tempPosition = (aPosition != null) ? aPosition : (++nextPosition);
		if (tempPosition <= 0) {
			throw new IndexOutOfBoundsException("List positions below and including 0 are invalid.");
		}

		int tempListSize = tempModel.getSize();
		if (tempPosition > tempListSize) {
			return null;
		} else {
			int tempActualPosition = tempPosition - 1;
			return tempModel.getElementAt(tempActualPosition);
		}
	}

}
