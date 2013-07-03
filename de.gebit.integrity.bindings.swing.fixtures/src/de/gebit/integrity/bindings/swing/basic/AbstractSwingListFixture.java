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
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * Abstract superclass for fixtures accessing {@link JList}.
 * 
 * @param <C>
 *            the component type
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public abstract class AbstractSwingListFixture<C extends JList> extends AbstractSwingFixture<C> implements
		CustomProposalFixture {

	/**
	 * The parameter name for entry text.
	 */
	public static final String ENTRY_TEXT_PARAMETER_NAME = "text";

	/**
	 * The parameter name for entry position.
	 */
	public static final String ENTRY_INDEX_PARAMETER_NAME = "index";

	/**
	 * These methods accept entry texts as their expected result.
	 */
	public static final String[] METHODS_WITH_ENTRY_TEXT_RESULTS = { "getSelectedEntries", "getListEntry" };

	/**
	 * The last position; this is used in case of repeated invocations because of tabletests to track which position
	 * must be checked next.
	 */
	protected int nextIndex;

	/**
	 * Returns the current element at the given position in the given list, starting at the top. Can be used either as a
	 * call fixture method to retrieve the text into a variable, or as a test fixture to check the text against a
	 * reference text.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @param aListIndex
	 *            the position in the list (one-based!), you can omit this for automatic position calculation in
	 *            tabletests
	 * @return the element in the list at the given position, or null if the position is larger than the available
	 *         entries
	 */
	@FixtureMethod(descriptionCall = "Get the element at position $" + ENTRY_INDEX_PARAMETER_NAME
			+ "$ in list '$name$'", descriptionTest = "Check the element at position $" + ENTRY_INDEX_PARAMETER_NAME
			+ "$ in list '$name$'")
	public Object getEntryValue(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_INDEX_PARAMETER_NAME) Integer aListIndex) {
		JList tempList = findComponentGuarded(aComponentPath);
		ListModel tempModel = tempList.getModel();

		int tempPosition = (aListIndex != null) ? aListIndex : (++nextIndex);
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
	 * Returns the text of the element at the given position in the given list, starting at the top. Can be used either
	 * as a call fixture method to retrieve the text into a variable, or as a test fixture to check the text against a
	 * reference text.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 * @param aListIndex
	 *            the position in the list (one-based!), you can omit this for automatic position calculation in
	 *            tabletests
	 * @return the text of the element in the list at the given position, or null if the position is larger than the
	 *         available entries
	 */
	@FixtureMethod(descriptionCall = "Get the text at position $" + ENTRY_INDEX_PARAMETER_NAME + "$ in list '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'", descriptionTest = "Check the text at position $"
			+ ENTRY_INDEX_PARAMETER_NAME + "$ in list '$name$'")
	public Object getEntryText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_INDEX_PARAMETER_NAME) Integer aListIndex) {
		Object tempEntry = getEntryValue(aComponentPath, aListIndex);

		if (tempEntry == null) {
			return null;
		} else {
			return tempEntry.toString();
		}
	}

	/**
	 * Fetches the entry count of the specified list.
	 * 
	 * @param aComponentPath
	 *            the path of the component
	 */
	@FixtureMethod(descriptionCall = "Get the number of entries in list '$" + COMPONENT_PATH_PARAMETER_NAME + "$'", descriptionTest = "Check the number of entries in list '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'")
	public Integer getEntryCount(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		return findComponentGuarded(aComponentPath).getModel().getSize();
	}

	/**
	 * Selects a given entry or multiple entries (in case of multi-select lists) at a specified position or with a given
	 * text from the list. You only need to provide one: either a position or a text.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @param someEntryIndices
	 *            The position(s) of the entry to be selected (one-based!)
	 * @param someEntryTexts
	 *            The text(s) of the entry to be selected
	 */
	@FixtureMethod(description = "Select the entries {" + ENTRY_INDEX_PARAMETER_NAME + "?at index $"
			+ ENTRY_INDEX_PARAMETER_NAME + "$}{" + ENTRY_TEXT_PARAMETER_NAME + "?'$" + ENTRY_TEXT_PARAMETER_NAME
			+ "$'} in list '$name$'")
	public void select(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_INDEX_PARAMETER_NAME) Integer[] someEntryIndices,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String[] someEntryTexts) {
		final JList tempList = findComponentGuarded(aComponentPath);

		List<Integer> tempEntriesToSelect = new ArrayList<Integer>();
		if (someEntryIndices != null) {
			for (Integer tempEntryPosition : someEntryIndices) {
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
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entries' indices from list '$name$'", descriptionTest = "Checks the position of the selected entries' indices in list '$name$'")
	public int[] getSelectedIndices(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		final JList tempList = findComponentGuarded(aComponentPath);

		int[] tempIndices = tempList.getSelectedIndices();

		if (tempIndices.length == 0) {
			return new int[] { 0 };
		}

		// one-based
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
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entries from list '$name$'", descriptionTest = "Checks the selected entries in list '$name$'")
	public Object[] getSelectedEntriesValue(
			@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		JList tempList = findComponentGuarded(aComponentPath);

		Object[] tempSelectedValues = tempList.getSelectedValues();

		return tempSelectedValues.length > 0 ? tempSelectedValues : null;
	}

	/**
	 * Returns or checks the currently selected entry (or multiple in case of multi-select lists) in the list. This
	 * method returns the texts of the entry.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return the selected entry or entries, or null if none is selected
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entries' texts from list '$name$'", descriptionTest = "Checks the selected entries' texts in list '$name$'")
	public String[] getSelectedEntriesText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		JList tempList = findComponentGuarded(aComponentPath);

		Object[] tempSelectedValues = tempList.getSelectedValues();

		String[] tempTargetArray = new String[tempSelectedValues.length];
		for (int i = 0; i < tempSelectedValues.length; i++) {
			tempTargetArray[i] = tempSelectedValues[i].toString();
		}

		return tempTargetArray.length > 0 ? tempTargetArray : null;
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
