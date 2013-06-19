/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing;

/**
 * This interface is preliminary and will be used some day as root interface for other interfaces defining a "core"
 * fixture method set for GUI testing fixtures. That interface set is still in development however and not yet part of
 * the Integrity Swing Bindings open-source codebase, although it will probably be at some point of time in the future.
 * 
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public interface IComponentHandler {

	/**
	 * The parameter name for the component path parameter.
	 */
	String COMPONENT_PATH_PARAMETER_NAME = "name";

}
