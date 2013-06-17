/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse.basic;

import java.awt.Component;

import javax.swing.JList;

import de.gebit.integrity.bindings.swing.authorassist.queries.AbstractSwingAuthorAssistQuery;
import de.gebit.integrity.bindings.swing.authorassist.queries.SwingAuthorAssistListEntryQuery;
import de.gebit.integrity.bindings.swing.basic.SwingListFixture;
import de.gebit.integrity.bindings.swing.eclipse.AbstractResultSuggestingSwingComponentFixtureProposalProvider;
import de.gebit.integrity.fixtures.CustomProposalProvider.CustomProposalFixtureLink;

/**
 * Proposal provider class for the {@link SwingListFixture}.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
@CustomProposalFixtureLink(SwingListFixture.class)
public class SwingListFixtureProposalProvider extends AbstractResultSuggestingSwingComponentFixtureProposalProvider {

	@Override
	protected Class<? extends Component> getComponentClass() {
		return JList.class;
	}

	@Override
	protected AbstractSwingAuthorAssistQuery generateAuthorAssistQuery(String aComponentName) {
		return new SwingAuthorAssistListEntryQuery(aComponentName);
	}

	@Override
	protected String[] getRelevantMethods() {
		return SwingListFixture.METHODS_WITH_ENTRY_TEXT_RESULTS;
	}

	@Override
	protected String getRelevantPositionParameterName() {
		return SwingListFixture.ENTRY_INDEX_PARAMETER_NAME;
	}

	@Override
	protected String getRelevantTextParameterName() {
		return SwingListFixture.ENTRY_TEXT_PARAMETER_NAME;
	}

}
