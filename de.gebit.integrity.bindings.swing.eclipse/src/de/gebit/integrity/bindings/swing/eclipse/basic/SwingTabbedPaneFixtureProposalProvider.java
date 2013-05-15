/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse.basic;

import java.awt.Component;

import javax.swing.JTabbedPane;

import de.gebit.integrity.bindings.swing.authorassist.queries.AbstractSwingAuthorAssistQuery;
import de.gebit.integrity.bindings.swing.authorassist.queries.SwingAuthorAssistTabTitleQuery;
import de.gebit.integrity.bindings.swing.basic.SwingTabbedPaneFixture;
import de.gebit.integrity.bindings.swing.eclipse.AbstractResultSuggestingSwingComponentFixtureProposalProvider;
import de.gebit.integrity.fixtures.CustomProposalProvider.CustomProposalFixtureLink;

/**
 * Proposal provider class for the {@link SwingTabbedPaneFixture}.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
@CustomProposalFixtureLink(SwingTabbedPaneFixture.class)
public class SwingTabbedPaneFixtureProposalProvider extends
		AbstractResultSuggestingSwingComponentFixtureProposalProvider {

	@Override
	protected Class<? extends Component> getComponentClass() {
		return JTabbedPane.class;
	}

	@Override
	protected AbstractSwingAuthorAssistQuery generateAuthorAssistQuery(String aComponentName) {
		return new SwingAuthorAssistTabTitleQuery(aComponentName);
	}

	@Override
	protected String[] getRelevantMethods() {
		return SwingTabbedPaneFixture.METHODS_WITH_TAB_TEXT_RESULTS;
	}

	@Override
	protected String getRelevantPositionParameterName() {
		return SwingTabbedPaneFixture.TAB_POSITION_PARAMETER_NAME;
	}

	@Override
	protected String getRelevantTextParameterName() {
		return SwingTabbedPaneFixture.TAB_TEXT_PARAMETER_NAME;
	}

}
