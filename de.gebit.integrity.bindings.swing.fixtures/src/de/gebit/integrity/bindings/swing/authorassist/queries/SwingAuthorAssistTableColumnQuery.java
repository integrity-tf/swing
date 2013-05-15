/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.authorassist.queries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;
import de.gebit.integrity.bindings.swing.tables.SwingTableContentFixture;

/**
 * This queries the server for columns available for a certain table.
 * 
 * @author Slartibartfast
 * 
 */
public class SwingAuthorAssistTableColumnQuery extends AbstractSwingAuthorAssistQuery {

	/**
	 * The serial version.
	 */
	private static final long serialVersionUID = -4735860911905218675L;

	/**
	 * The name of the component to inspect.
	 */
	private String componentName;

	/**
	 * Creates an instance.
	 * 
	 * @param aComponentClassName
	 */
	public SwingAuthorAssistTableColumnQuery(String aComponentName) {
		componentName = aComponentName;
	}

	public String getComponentame() {
		return componentName;
	}

	@Override
	public Object[] process(AbstractSwingComponentHandler aComponentHandler, JFrame anOwnerFrame) {
		try {
			JTable tempTable = aComponentHandler.findComponentGuarded(componentName, JTable.class, anOwnerFrame);
			List<Object> tempResults = new ArrayList<Object>();
			for (int i = 0; i < tempTable.getColumnCount(); i++) {
				String tempColumnName = tempTable.getColumnName(i);
				if (tempColumnName != null) {
					tempResults.add(new SwingAuthorAssistTableColumnQueryResult(SwingTableContentFixture
							.simplifyColumnName(tempColumnName), tempColumnName));
				}

				tempResults.add(new SwingAuthorAssistTableColumnQueryResult(SwingTableContentFixture
						.generateColumnName(i), tempColumnName));
			}

			return tempResults.toArray();
		} catch (AmbiguousComponentPathException exc) {
			exc.printStackTrace();
		} catch (InvalidComponentPathException exc) {
			exc.printStackTrace();
		}

		return null;
	}

	/**
	 * The result for {@link SwingAuthorAssistTableColumnQuery}s.
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	public static class SwingAuthorAssistTableColumnQueryResult implements Serializable {

		/**
		 * The serial version.
		 */
		private static final long serialVersionUID = -5072992175537055892L;

		/**
		 * The column name as it can be used in integrity.
		 */
		private String technicalColumnName;

		/**
		 * The human-readable original column name.
		 */
		private String columnName;

		/**
		 * Creates an instance.
		 * 
		 * @param aTechnicalColumnName
		 *            the column name as it can be used in integrity
		 * @param aColumnName
		 *            the human-readable original column name
		 */
		public SwingAuthorAssistTableColumnQueryResult(String aTechnicalColumnName, String aColumnName) {
			technicalColumnName = aTechnicalColumnName;
			columnName = aColumnName;
		}

		public String getTechnicalColumnName() {
			return technicalColumnName;
		}

		public String getColumnName() {
			return columnName;
		}

	}

}
