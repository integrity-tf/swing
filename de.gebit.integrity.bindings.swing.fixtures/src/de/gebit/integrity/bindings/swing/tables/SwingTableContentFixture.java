/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.tables;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;
import de.gebit.integrity.fixtures.ArbitraryParameterFixture;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture provides access to {@link JTable} components.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingTableContentFixture extends AbstractSwingFixture implements CustomProposalFixture,
		ArbitraryParameterFixture {

	/**
	 * The last row; this is used in case of repeated invocations because of tabletests to track which row must be
	 * checked next.
	 */
	protected int nextRow;

	/**
	 * Gets the table content (cell content as object) at a specific row.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aRow
	 *            the row number (one-based!), omit for automatic row calculation in tabletests
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(description = "Check the content in table '$name$'")
	public SwingTableRowResult getTableContent(
			@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "row") Integer aRow) throws AmbiguousComponentPathException,
			EventQueueTimeoutException, InvalidComponentPathException {
		return new SwingTableRowResult(internalGetTableRowContent(aComponentPath, aRow));
	}

	/**
	 * Gets the text at a specific row in the given table. This forces a conversion of all table values to Strings.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aRow
	 *            the row number (one-based!), omit for automatic row calculation in tabletests
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(description = "Check the text in table '$name$'")
	public Object getTableText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "row") Integer aRow, @FixtureParameter(name = "column") Integer aColumn)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		return new SwingTableRowResult(internalGetTableRowText(aComponentPath, aRow));
	}

	/**
	 * A result object which encapsulates the named arbitrary results from a call to
	 * {@link SwingTableContentFixture#getTableContent(String, Integer)} or
	 * {@link SwingTableContentFixture#getTableText(String, Integer, Integer)}.
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	protected static class SwingTableRowResult {

		/**
		 * The map containing the arbitrary result values.
		 */
		private Map<String, Object> arbitraryResultsMap;

		/**
		 * Creates a new instance.
		 */
		public SwingTableRowResult(Map<String, Object> aResultMap) {
			arbitraryResultsMap = aResultMap;
		}

		public Map<String, Object> getArbitraryResultsMap() {
			return arbitraryResultsMap;
		}

	}

	/**
	 * Actually retrieves the table content.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aRow
	 *            the row number (one-based!) or null for automatic row calculation in tabletests
	 * @return the content
	 * @throws AmbiguousComponentPathException
	 * @throws InvalidComponentPathException
	 */
	protected Map<String, Object> internalGetTableRowContent(String aComponentPath, Integer aRow)
			throws AmbiguousComponentPathException, InvalidComponentPathException {
		JTable tempTable = findComponentGuarded(aComponentPath, JTable.class, null);
		TableModel tempModel = tempTable.getModel();

		int tempRow = aRow != null ? aRow - 1 : (nextRow++);

		Map<String, Object> tempResults = new HashMap<String, Object>();
		if (tempRow >= 0 && tempRow <= tempTable.getRowCount()) {
			for (int i = 0; i < tempTable.getColumnCount(); i++) {
				Object tempValue = tempModel.getValueAt(tempRow, i);

				String tempName = simplifyColumnName(tempTable.getColumnName(i));
				if (tempName != null) {
					tempResults.put(tempName, tempValue);
				}
				tempResults.put(generateColumnName(i), tempValue);
			}
		}

		return tempResults;
	}

	/**
	 * Actually retrieves the table texts.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aRow
	 *            the row number (one-based!)
	 * @return the content
	 * @throws AmbiguousComponentPathException
	 * @throws InvalidComponentPathException
	 */
	protected Map<String, Object> internalGetTableRowText(String aComponentPath, int aRow)
			throws AmbiguousComponentPathException, InvalidComponentPathException {
		Map<String, Object> tempResults = internalGetTableRowContent(aComponentPath, aRow);
		for (Entry<String, Object> tempEntry : tempResults.entrySet()) {
			Object tempValue = tempEntry.getValue();
			if (tempValue != null) {
				tempEntry.setValue(tempValue.toString());
			}
		}

		return tempResults;
	}

	/**
	 * These characters are allowed for named result identifiers.
	 */
	protected static final String IDENTIFIER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

	/**
	 * Simplifies any given column name to match the requirements for named result identifiers. This converts a nice
	 * name like "First Name" to "firstName", trying to keep as much information as possible while conforming to rules
	 * for named result identifiers.
	 * 
	 * @param aName
	 *            the name to simplify
	 * @return the simplified name
	 */
	public static String simplifyColumnName(String aName) {
		if (aName == null) {
			return null;
		}

		// First: clean up all characters except alphanumerics. We'll replace them by spaces.
		StringBuffer tempNameBuffer = new StringBuffer();
		for (int i = 0; i < aName.length(); i++) {
			char tempChar = aName.charAt(i);
			if (IDENTIFIER_CHARS.indexOf(tempChar) >= 0) {
				tempNameBuffer.append(tempChar);
			} else {
				tempNameBuffer.append(" ");
			}
		}
		String tempName = tempNameBuffer.toString();

		// Second: tokenize string, using spaces as splitter. Then combine tokens, enforcing camel-case.
		tempNameBuffer = new StringBuffer();
		for (String tempPart : aName.split(" ")) {
			if (tempPart.length() > 0) {
				tempNameBuffer.append(tempPart.substring(0, 1).toUpperCase() + tempPart.substring(1));
			}
		}
		tempName = tempNameBuffer.toString();

		// Filter out cases like column names with spaces/illegal chars only which have been filtered out now
		if (tempName.length() == 0) {
			return null;
		}

		// Third: lowercase the first character
		tempName = tempName.substring(0, 1).toLowerCase() + tempName.substring(1);

		// Fourth: if the name starts with a number, put an underscore in front
		if (Character.isDigit(tempName.charAt(0))) {
			tempName = "_" + tempName;
		}

		return tempName;
	}

	/**
	 * Autogenerates a column name based on the number of the column.
	 * 
	 * @param aColumn
	 *            the column number
	 * @return the generated name
	 */
	public static String generateColumnName(int aColumn) {
		return "col" + (aColumn + 1);
	}

}
