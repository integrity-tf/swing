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

import javax.swing.JFrame;
import javax.swing.JList;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;

/**
 * This queries the server for possible entries in a list.
 * 
 * @author Slartibartfast
 * 
 */
public class SwingAuthorAssistListEntryQuery extends AbstractSwingAuthorAssistQuery {

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
	public SwingAuthorAssistListEntryQuery(String aComponentName) {
		componentName = aComponentName;
	}

	public String getComponentame() {
		return componentName;
	}

	@Override
	public Object[] process(AbstractSwingComponentHandler aComponentHandler, JFrame anOwnerFrame) {
		try {
			JList tempList = aComponentHandler.findComponentGuarded(componentName, JList.class, anOwnerFrame);
			List<Object> tempResults = new ArrayList<Object>();

			for (int i = 0; i < tempList.getModel().getSize(); i++) {
				Object tempItem = tempList.getModel().getElementAt(i);
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
