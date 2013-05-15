/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.authorassist.queries;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;

/**
 * This queries the server for possible entries in a combo box.
 * 
 * @author Slartibartfast
 * 
 */
public class SwingAuthorAssistComboBoxEntryQuery extends AbstractSwingAuthorAssistQuery {

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
	public SwingAuthorAssistComboBoxEntryQuery(String aComponentName) {
		componentName = aComponentName;
	}

	public String getComponentame() {
		return componentName;
	}

	@Override
	public Object[] process(AbstractSwingComponentHandler aComponentHandler, JFrame anOwnerFrame) {
		try {
			JComboBox tempComboBox = aComponentHandler.findComponentGuarded(componentName, JComboBox.class,
					anOwnerFrame);
			List<Object> tempResults = new ArrayList<Object>();

			for (int i = 0; i < tempComboBox.getItemCount(); i++) {
				Object tempItem = tempComboBox.getItemAt(i);
				if (tempItem != null) {
					tempResults.add(tempItem.toString());
				}
			}

			return tempResults.toArray();
		} catch (AmbiguousComponentPathException exc) {
			exc.printStackTrace();
		} catch (InvalidComponentPathException exc) {
			exc.printStackTrace();
		}

		return null;
	}

}
