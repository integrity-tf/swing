/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.authorassist.queries;

import java.io.Serializable;

import javax.swing.JFrame;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;

/**
 * This is a base class for author assist queries.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public abstract class AbstractSwingAuthorAssistQuery implements Serializable {

	/**
	 * The serial version.
	 */
	private static final long serialVersionUID = 1034876941254714859L;

	/**
	 * Processes the query and produces a result returned to the client.
	 * 
	 * @param aComponentHandler
	 *            the component handler to use for query processing
	 * @param anOwnerFrame
	 *            the frame owning the author assist server
	 * @return the result
	 */
	public abstract Object[] process(AbstractSwingComponentHandler aComponentHandler, JFrame anOwnerFrame);

}
