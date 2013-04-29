/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.tables;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture provides access to {@link JTable} components.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingTableCellContentFixture extends AbstractSwingFixture implements CustomProposalFixture {

	/**
	 * Gets the table content (cell content as object) at a specific row and column.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aRow
	 *            the row number (one-based!)
	 * @param aColumn
	 *            the column number (one-based!)
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the content from table '$name$' at row $row$, column $column$", descriptionTest = "Check the content in table '$name$' at row $row$, column $column$")
	public Object getTableCellContent(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "row") Integer aRow, @FixtureParameter(name = "column") Integer aColumn)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		return internalGetTableContent(aComponentPath, aRow, aColumn);
	}

	/**
	 * Gets the text at a specific row and column in the given table. This forces a conversion of all table values to
	 * Strings.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aRow
	 *            the row number (one-based!)
	 * @param aColumn
	 *            the column number (one-based!)
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the text from table '$name$' at row $row$, column $column$", descriptionTest = "Check the text in table '$name$' at row $row$, column $column$")
	public Object getTableCellText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "row") Integer aRow, @FixtureParameter(name = "column") Integer aColumn)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		Object tempElement = internalGetTableContent(aComponentPath, aRow, aColumn);
		if (tempElement != null) {
			return tempElement.toString();
		} else {
			return null;
		}
	}

	/**
	 * Actually retrieves the table content.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aRow
	 *            the row number (one-based!)
	 * @param aColumn
	 *            the column number (one-based!)
	 * @return the content
	 * @throws AmbiguousComponentPathException
	 * @throws InvalidComponentPathException
	 */
	protected Object internalGetTableContent(String aComponentPath, Integer aRow, Integer aColumn)
			throws AmbiguousComponentPathException, InvalidComponentPathException {
		JTable tempTable = findComponentGuarded(aComponentPath, JTable.class, null);
		TableModel tempModel = tempTable.getModel();

		int tempRow = aRow != null ? aRow - 1 : 0;
		int tempColumn = aColumn != null ? aColumn - 1 : 0;

		if (tempRow >= 0 && tempRow < tempTable.getRowCount()) {
			if (tempColumn >= 0 && tempColumn < tempTable.getColumnCount()) {
				return tempModel.getValueAt(tempRow, tempColumn);
			}
		}

		return null;
	}

}
