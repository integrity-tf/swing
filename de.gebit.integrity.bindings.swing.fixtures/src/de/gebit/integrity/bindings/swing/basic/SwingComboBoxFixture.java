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
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture provides access to {@link JComboBox} components.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingComboBoxFixture extends AbstractSwingFixture<JComboBox> implements CustomProposalFixture {

	/**
	 * The parameter name for entry text.
	 */
	public static final String ENTRY_TEXT_PARAMETER_NAME = "text";

	/**
	 * The parameter name for entry position.
	 */
	public static final String ENTRY_POSITION_PARAMETER_NAME = "position";

	/**
	 * The parameter name for entry model.
	 */
	public static final String ENTRY_MODEL_PARAMETER_NAME = "model";

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
	 */
	@FixtureMethod(description = "Select the entry {" + ENTRY_POSITION_PARAMETER_NAME + "?at position $"
			+ ENTRY_POSITION_PARAMETER_NAME + "$}{" + ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} in combo box '$name$'")
	public void selectEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_POSITION_PARAMETER_NAME) Integer anEntryPosition,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String anEntryText) {
		final JComboBox tempComboBox = findComponentGuarded(aComponentPath);

		checkComponentEnabled(tempComboBox);

		final int tempIndexToSelect = findItemIndexGuarded(tempComboBox, anEntryPosition, anEntryText);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempComboBox.setSelectedIndex(tempIndexToSelect);
			}
		});
	}

	/**
	 * Selects an entry provided as a model object.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @param aModelObject
	 *            The model object to be selected
	 */
	@FixtureMethod(description = "Select the entry {" + ENTRY_POSITION_PARAMETER_NAME + "?at position $"
			+ ENTRY_POSITION_PARAMETER_NAME + "$}{" + ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} in combo box '$name$'")
	public void selectEntryModel(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_MODEL_PARAMETER_NAME) final Object aModelObject) {
		final JComboBox tempComboBox = findComponentGuarded(aComponentPath);

		checkComponentEnabled(tempComboBox);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempComboBox.setSelectedItem(aModelObject);
			}
		});
	}

	/**
	 * Returns or checks the position of the currently selected entry in the combo box.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return the position of the selected entry (one-based) or zero if none is selected
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entrys' position from combo box '$name$'", descriptionTest = "Checks the position of the selected entry in combo box '$name$'")
	public int getSelectedEntryPosition(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		final JComboBox tempComboBox = findComponentGuarded(aComponentPath);

		return tempComboBox.getSelectedIndex() + 1;
	}

	/**
	 * Returns or checks the currently selected entry in the combo box.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return the selected entry as text, or null if none is selected
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entry from combo box '$name$'", descriptionTest = "Checks the selected entry in combo box '$name$'")
	public String getSelectedEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		final JComboBox tempComboBox = findComponentGuarded(aComponentPath);

		return nullSafeToString(tempComboBox.getSelectedItem());
	}

	/**
	 * Returns or checks the currently selected entry in the combo box.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return the selected entry, or null if none is selected
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected entry from combo box '$name$'", descriptionTest = "Checks the selected entry in combo box '$name$'")
	public Object getSelectedEntryModel(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		final JComboBox tempComboBox = findComponentGuarded(aComponentPath);

		return tempComboBox.getSelectedItem();
	}

	/**
	 * Returns the number of entries found in the combo box.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @return true if the entry exists, false if it does not exist
	 */
	@FixtureMethod(descriptionCall = "Returns the number of entries in combo box '$name$'", descriptionTest = "Checks the number of entries in combo box '$name$'")
	public int getEntryCount(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		JComboBox tempComboBox = findComponentGuarded(aComponentPath);

		return tempComboBox.getModel().getSize();
	}

	/**
	 * Finds an entry which matches the provided position or text.
	 * 
	 * @param aComponentPath
	 * @param anEntryPosition
	 * @param anEntryText
	 * @return
	 */
	protected Object getEntryInternal(String aComponentPath, Integer anEntryPosition, String anEntryText) {
		JComboBox tempComboBox = findComponentGuarded(aComponentPath);

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
	 * Fetches a given entry at a specified position or with a given text from the combo box. You only need to provide
	 * one: either a position or a text.
	 * 
	 * @param aComponentPath
	 *            The path to the component
	 * @param anEntryPosition
	 *            The position of the entry to be fetched (one-based!)
	 * @return the entry
	 */
	@FixtureMethod(descriptionCall = "Fetches the entry {" + ENTRY_POSITION_PARAMETER_NAME + "?at position $"
			+ ENTRY_POSITION_PARAMETER_NAME + "$}{" + ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} from combo box '$name$'", descriptionTest = "Checks the entry {"
			+ ENTRY_POSITION_PARAMETER_NAME
			+ "?at position $"
			+ ENTRY_POSITION_PARAMETER_NAME
			+ "$}{"
			+ ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} in combo box '$name$'")
	public String getEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_POSITION_PARAMETER_NAME) Integer anEntryPosition,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String anEntryText) {
		return nullSafeToString(getEntryInternal(aComponentPath, anEntryPosition, anEntryText));
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
	 */
	@FixtureMethod(descriptionCall = "Fetches the entry {" + ENTRY_POSITION_PARAMETER_NAME + "?at position $"
			+ ENTRY_POSITION_PARAMETER_NAME + "$}{" + ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} from combo box '$name$'", descriptionTest = "Checks the entry {"
			+ ENTRY_POSITION_PARAMETER_NAME
			+ "?at position $"
			+ ENTRY_POSITION_PARAMETER_NAME
			+ "$}{"
			+ ENTRY_TEXT_PARAMETER_NAME + "?'$text$'} in combo box '$name$'")
	public Object getEntryModel(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = ENTRY_POSITION_PARAMETER_NAME) Integer anEntryPosition,
			@FixtureParameter(name = ENTRY_TEXT_PARAMETER_NAME) String anEntryText) {
		return getEntryInternal(aComponentPath, anEntryPosition, anEntryText);
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
