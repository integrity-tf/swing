/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.tables;

import javax.swing.JTable;

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
public class SwingTableContentFixture extends AbstractSwingFixture implements CustomProposalFixture {

	/**
	 * Gets the table content (cell content as object) at a specific row and column.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the content from the table '$name$' at row $row$, column $column$", descriptionTest = "Check the content from the table '$name$' at row $row$, column $column$")
	public Object getTableContent(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "row") Integer aRow, @FixtureParameter(name = "column") Integer aColumn)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		return internalGetTableContent(aComponentPath, aRow, aColumn);
	}

	/**
	 * Gets the text at a specific row and column in the given table.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the text from the table '$name$' at row $row$, column $column$", descriptionTest = "Check the text from the table '$name$' at row $row$, column $column$")
	public Object getTableText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = "row") Integer aRow, @FixtureParameter(name = "column") Integer aColumn)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		Object tempElement = internalGetTableContent(aComponentPath, aRow, aColumn);
		if (tempElement != null) {
			return tempElement.toString();
		} else {
			return null;
		}
	}

	protected Object internalGetTableContent(String aComponentPath, Integer aRow, Integer aColumn)
			throws AmbiguousComponentPathException, InvalidComponentPathException {
		return findComponentGuarded(aComponentPath, JTable.class, null).getModel().getValueAt(aRow != null ? aRow : 0,
				aColumn != null ? aColumn : 0);
	}

}
