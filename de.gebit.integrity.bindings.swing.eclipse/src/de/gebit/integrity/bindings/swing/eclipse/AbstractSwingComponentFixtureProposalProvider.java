/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import de.gebit.integrity.fixtures.CustomProposalProvider;

/**
 * Abstract base class for individual Proposal Providers for specific Swing
 * components.
 * 
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public abstract class AbstractSwingComponentFixtureProposalProvider extends
		AbstractSwingFixtureAssist implements CustomProposalProvider {

	/**
	 * A high priority number.
	 */
	protected static final int HIGH_BASE_PRIORITY = 999999999;

	/**
	 * Must return the class for which to provide proposals.
	 * 
	 * @return
	 */
	protected abstract Class<? extends Component> getComponentClass();

	@Override
	public List<CustomProposalDefinition> defineParameterProposals(
			String aFixtureMethodName, String aParameterName,
			Map<String, Object> someParameterValues) {
		if (COMPONENT_PATH_PARAMETER_NAME.equals(aParameterName)) {
			return requestProposals(getComponentClass());
		}

		return null;
	}

	@Override
	public List<CustomProposalDefinition> defineResultProposals(
			String aFixtureMethodName, String aResultName, Object aResultValue,
			Map<String, Object> someParameterValues) {
		return null;
	}

	protected String packageString(String aString) {
		return "\"" + aString.replace("\"", "\\\"") + "\"";
	}

}
