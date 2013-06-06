/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import java.util.ArrayList;
import java.util.List;

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
public class SwingListFixture extends AbstractSwingFixture<JList> implements CustomProposalFixture {

	/**
	 * The parameter name for entry text.
	 */
	public static final String ENTRY_TEXT_PARAMETER_NAME = "text";

	/**
	 * The parameter name for entry position.
	 */
	public static final String ENTRY_POSITION_PARAMETER_NAME = "position";

	/**
	 * These methods accept entry texts as their expected result.
	 */
	public static final String[] METHODS_WITH_ENTRY_TEXT_RESULTS = { "getSelectedEntries", "getListEntry" };

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
			@FixtureParameter(name = ENTRY_POSITION_PARAMETER_NAME) Integer aListPosition)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		JList tempList = findComponentGuarded(aComponentPath);
		ListModel tempModel = tempList.getModel();

		int tempPosition = (aListPosition != null) ? aListPosition : (++nextPosition);
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
		return findComponentGuarded(aComponentPath).getModel().getSize();
	}

	/**
	 * Selects a given entry or multiple entries (in case of multi-select lists) at a specified position or with a given
	 * text from the list. You only need to provide one: either a position or a text.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @param someEntryPositions
	 *            The position(s) of the entry to be selected (one-based!)
	 * @param someEntryTexts
	 *            The text(s) of the entry to be selected
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(description = "Select the entry {" + ENTRY_POSITION_PARAMETER_NAME + "?at position $"
			+ ENTRY_POSITION_PARAMETER_NAME + "$}{" + ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} in list '$name$'")
	public void selectEntries(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_POSITION_PARAMETER_NAME) Integer[] someEntryPositions,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String[] someEntryTexts)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JList tempList = findComponentGuarded(aComponentPath);

		List<Integer> tempEntriesToSelect = new ArrayList<Integer>();
		if (someEntryPositions != null) {
			for (Integer tempEntryPosition : someEntryPositions) {
				tempEntriesToSelect.add(findItemIndexGuarded(tempList, tempEntryPosition, null));
			}
		}
		if (someEntryTexts != null) {
			for (String tempEntryText : someEntryTexts) {
				tempEntriesToSelect.add(findItemIndexGuarded(tempList, null, tempEntryText));
			}
		}

		final int[] tempIndicesToSelect = new int[tempEntriesToSelect.size()];
		int tempCount = 0;
		for (Integer tempIndex : tempEntriesToSelect) {
			tempIndicesToSelect[tempCount] = tempIndex;
			tempCount++;
		}

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempList.setSelectedIndices(tempIndicesToSelect);
			}
		});
	}

	/**
	 * Returns or checks the position of the currently selected entry or entries in case of multi-select lists in the
	 * combo box.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return the positions of the selected entries (one-based) or zero if none is selected
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entrys' position from list '$name$'", descriptionTest = "Checks the position of the selected entry in list '$name$'")
	public int[] getSelectedEntryPositions(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JList tempList = findComponentGuarded(aComponentPath);

		int[] tempIndices = tempList.getSelectedIndices();

		if (tempIndices.length == 0) {
			return new int[] { 0 };
		}

		for (int i = 0; i < tempIndices.length; i++) {
			tempIndices[i]++;
		}

		return tempIndices;
	}

	/**
	 * Returns or checks the currently selected entry (or multiple in case of multi-select lists) in the list.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return the selected entry or entries, or null if none is selected
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entry from list '$name$'", descriptionTest = "Checks the selected entry in list '$name$'")
	public Object[] getSelectedEntries(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		final JList tempList = findComponentGuarded(aComponentPath);

		return tempList.getSelectedValues();
	}

	/**
	 * Checks whether an entry at the given position or with the given text exists in the list. You only need to provide
	 * one: either a position or a text.
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
	@FixtureMethod(descriptionCall = "Returns if an entry {" + ENTRY_POSITION_PARAMETER_NAME + "?at position $"
			+ ENTRY_POSITION_PARAMETER_NAME + "$}{" + ENTRY_TEXT_PARAMETER_NAME
			+ "?'$text$'} exists in combo box '$name$'", descriptionTest = "Checks if an entry {"
			+ ENTRY_POSITION_PARAMETER_NAME + "?at position $" + ENTRY_POSITION_PARAMETER_NAME + "$}{"
			+ ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} exists in combo box '$name$'")
	public boolean entryExists(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_POSITION_PARAMETER_NAME) Integer anEntryPosition,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String anEntryText)
			throws AmbiguousComponentPathException, InvalidComponentPathException {
		JList tempList = findComponentGuarded(aComponentPath);

		if (anEntryPosition != null) {
			return (anEntryPosition > 0 && anEntryPosition <= tempList.getModel().getSize());
		} else {
			return (findItemIndexByText(tempList, anEntryText) != null);
		}
	}

	/**
	 * Finds the index of a given position (one-based) or a given text. This throws exceptions in case no response could
	 * be determined.
	 * 
	 * @param aList
	 * @param anEntryPosition
	 * @param anEntryText
	 * @return the index to select
	 */
	protected int findItemIndexGuarded(JList aList, Integer anEntryPosition, String anEntryText) {
		if (anEntryPosition != null) {
			if (anEntryPosition > aList.getModel().getSize()) {
				throw new IndexOutOfBoundsException("The list only has " + aList.getModel().getSize()
						+ " items, but position " + anEntryPosition + " was requested.");
			} else if (anEntryPosition < 1) {
				throw new IndexOutOfBoundsException("Item positions below 1 are illegal.");
			} else {
				return anEntryPosition - 1;
			}
		} else if (anEntryText != null) {
			Integer tempIndex = findItemIndexByText(aList, anEntryText);
			if (tempIndex == null) {
				StringBuffer tempBuffer = new StringBuffer("The list does not contain the entry '" + anEntryText
						+ "'. Valid entries are: ");
				for (int i = 0; i < aList.getModel().getSize(); i++) {
					if (i > 0) {
						tempBuffer.append(", ");
					}
					tempBuffer.append("'" + aList.getModel().getElementAt(i).toString() + "'");
				}
				throw new IllegalArgumentException(tempBuffer.toString());
			} else {
				return tempIndex;
			}
		}

		throw new IllegalArgumentException("You need to provide either a position or a text.");
	}

	/**
	 * Finds the index of the item with the given text in the given list.
	 * 
	 * @param aList
	 *            the list to search
	 * @param aText
	 *            the text
	 * @return the index or null if no match was found
	 */
	protected Integer findItemIndexByText(JList aList, String aText) {
		for (int i = 0; i < aList.getModel().getSize(); i++) {
			if (aList.getModel().getElementAt(i).toString().equals(aText)) {
				return i;
			}
		}

		return null;
	}

}
