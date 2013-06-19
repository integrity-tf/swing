/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.exceptions;

/**
 * This exception is thrown in case an action is requested by the test which cannot be performed, such as a click on a
 * button which is disabled, or editing a text field which is not editable.
 * 
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class InvalidActionException extends IntegritySwingBindingsException {

	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 8665120028414379401L;

	/**
	 * Creates a new instance.
	 * 
	 * @param aMessage
	 *            the message
	 */
	public InvalidActionException(String aMessage) {
		super(aMessage);
	}

}
