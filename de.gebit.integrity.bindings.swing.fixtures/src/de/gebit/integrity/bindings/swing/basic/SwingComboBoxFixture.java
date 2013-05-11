/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JComboBox;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture provides access to {@link JComboBox} components.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingComboBoxFixture extends AbstractSwingFixture implements CustomProposalFixture {

	/**
	 * The parameter name for entry text.
	 */
	public static final String ENTRY_TEXT_PARAMETER_NAME = "text";

	/**
	 * These methods accept entry texts as their expected result.
	 */
	public static final String[] METHODS_WITH_ENTRY_TEXT_RESULTS = { "getSelectedEntry", "getEntry" };

	/**
	 * Internal position used for tabletests.
	 */
	protected int position;

	/**
	 * Selects a given entry at a specified position or with a given text from the combo box. You only need to provide
	 * one: either a position or a text.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @param anEntryPosition
	 *            The position of the entry to be selected (one-based!)
	 * @param anEntryText
	 *            The text of the entry to be selected
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(description = "Select the entry at position $position$ in combo box '$name$'")
	public void selectEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "position") Integer anEntryPosition,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String anEntryText)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JComboBox tempComboBox = findComponentGuarded(aComponentPath, JComboBox.class, null);

		final int tempIndexToSelect = findItemIndexGuarded(tempComboBox, anEntryPosition, anEntryText);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempComboBox.setSelectedIndex(tempIndexToSelect);
			}
		});
	}

	/**
	 * Returns or checks the position of the currently selected entry in the combo box.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return the position of the selected entry (one-based) or zero if none is selected
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entrys' position from combo box '$name$'", descriptionTest = "Checks the position of the selected entry in combo box '$name$'")
	public int getSelectedEntryPosition(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JComboBox tempComboBox = findComponentGuarded(aComponentPath, JComboBox.class, null);

		return tempComboBox.getSelectedIndex() + 1;
	}

	/**
	 * Returns or checks the currently selected entry in the combo box.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return the selected entry, or null if none is selected
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entry from combo box '$name$'", descriptionTest = "Checks the selected entry in combo box '$name$'")
	public Object getSelectedEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JComboBox tempComboBox = findComponentGuarded(aComponentPath, JComboBox.class, null);

		return tempComboBox.getSelectedItem();
	}

	/**
	 * Checks whether an entry at the given position or with the given text exists in the combo box list. You only need
	 * to provide one: either a position or a text.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @param anEntryPosition
	 *            The position of the entry to be checked (one-based!)
	 * @param anEntryText
	 *            The text of the entry to be checked
	 * @return true if the entry exists, false if it does not exist
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Returns if an entry {position?at position $position$}{"
			+ ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} exists in combo box '$name$'", descriptionTest = "Checks if an entry {position?at position $position$}{"
			+ ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} exists in combo box '$name$'")
	public boolean entryExists(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "position") Integer anEntryPosition,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String anEntryText)
			throws AmbiguousComponentPathException, InvalidComponentPathException {
		JComboBox tempComboBox = findComponentGuarded(aComponentPath, JComboBox.class, null);

		if (anEntryPosition != null) {
			return (anEntryPosition > 0 && anEntryPosition <= tempComboBox.getItemCount());
		} else {
			return (findItemIndexByText(tempComboBox, anEntryText) != null);
		}
	}

	/**
	 * Fetches a given entry at a specified position or with a given text from the combo box. You only need to provide
	 * one: either a position or a text.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @param anEntryPosition
	 *            The position of the entry to be fetched (one-based!)
	 * @return the entry
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Fetches the entry {position?at position $position$}{" + ENTRY_TEXT_PARAMETER_NAME
			+ "?'$text$'} from combo box '$name$'", descriptionTest = "Checks the entry {position?at position $position$}{"
			+ ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} in combo box '$name$'")
	public Object getEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "position") Integer anEntryPosition,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String anEntryText)
			throws AmbiguousComponentPathException, InvalidComponentPathException {
		JComboBox tempComboBox = findComponentGuarded(aComponentPath, JComboBox.class, null);

		Integer tempEntryPositionToUse = anEntryPosition;
		if (anEntryText == null && anEntryPosition == null) {
			// This enables tabletests in which the position is not explicitly stated
			position++;
			tempEntryPositionToUse = position;
		}

		int tempIndexToFetch = findItemIndexGuarded(tempComboBox, tempEntryPositionToUse, anEntryText);

		return tempComboBox.getItemAt(tempIndexToFetch);
	}

	/**
	 * Finds the index of a given position (one-based) or a given text. This throws exceptions in case no response could
	 * be determined.
	 * 
	 * @param aBox
	 * @param anEntryPosition
	 * @param anEntryText
	 * @return the index to select
	 */
	protected int findItemIndexGuarded(JComboBox aBox, Integer anEntryPosition, String anEntryText) {
		if (anEntryPosition != null) {
			if (anEntryPosition > aBox.getItemCount()) {
				throw new IndexOutOfBoundsException("The combo box only has " + aBox.getItemCount()
						+ " items, but position " + anEntryPosition + " was requested.");
			} else if (anEntryPosition < 1) {
				throw new IndexOutOfBoundsException("Item positions below 1 are illegal.");
			} else {
				return anEntryPosition - 1;
			}
		} else if (anEntryText != null) {
			Integer tempIndex = findItemIndexByText(aBox, anEntryText);
			if (tempIndex == null) {
				StringBuffer tempBuffer = new StringBuffer("The combo box does not contain the entry '" + anEntryText
						+ "'. Valid entries are: ");
				for (int i = 0; i < aBox.getItemCount(); i++) {
					if (i > 0) {
						tempBuffer.append(", ");
					}
					tempBuffer.append("'" + aBox.getItemAt(i).toString() + "'");
				}
				throw new IllegalArgumentException(tempBuffer.toString());
			} else {
				return tempIndex;
			}
		}

		throw new IllegalArgumentException("You need to provide either a position or a text.");
	}

	/**
	 * Finds the index of the item with the given text in the given combo box.
	 * 
	 * @param aBox
	 *            the box to search
	 * @param aText
	 *            the text
	 * @return the index or null if no match was found
	 */
	protected Integer findItemIndexByText(JComboBox aBox, String aText) {
		for (int i = 0; i < aBox.getItemCount(); i++) {
			if (aBox.getItemAt(i).toString().equals(aText)) {
				return i;
			}
		}

		return null;
	}

}
