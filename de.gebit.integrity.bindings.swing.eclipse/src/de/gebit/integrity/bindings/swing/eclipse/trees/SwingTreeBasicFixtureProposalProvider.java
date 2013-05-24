/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse.trees;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;

import de.gebit.integrity.bindings.swing.authorassist.queries.SwingAuthorAssistTreeItemQuery;
import de.gebit.integrity.bindings.swing.authorassist.queries.SwingAuthorAssistTreeItemQuery.SwingAuthorAssistTreeItemQueryResult;
import de.gebit.integrity.bindings.swing.eclipse.AbstractSwingComponentFixtureProposalProvider;
import de.gebit.integrity.bindings.swing.trees.AbstractSwingTreeFixture;
import de.gebit.integrity.bindings.swing.trees.SwingTreeBasicFixture;
import de.gebit.integrity.fixtures.CustomProposalProvider.CustomProposalFixtureLink;

/**
 * Proposal provider class for the {@link SwingTreeBasicFixture}.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
@CustomProposalFixtureLink(SwingTreeBasicFixture.class)
public class SwingTreeBasicFixtureProposalProvider extends AbstractSwingComponentFixtureProposalProvider {

	@Override
	protected Class<? extends Component> getComponentClass() {
		return JTree.class;
	}

	@Override
	public List<CustomProposalDefinition> defineParameterProposals(String aFixtureMethodName, String aParameterName,
			Map<String, Object> someParameterValues) {

		if (AbstractSwingTreeFixture.TREE_ITEM_PATH_PARAMETER_NAME.equals(aParameterName)) {
			return requestPathProposals(someParameterValues, shallSuggestParents(aFixtureMethodName),
					shallSuggestLeafs(aFixtureMethodName),
					(String) someParameterValues.get(AbstractSwingTreeFixture.TREE_ITEM_PATH_PARAMETER_NAME));
		} else {
			return super.defineParameterProposals(aFixtureMethodName, aParameterName, someParameterValues);
		}
	}

	private boolean shallSuggestParents(String aFixtureMethodName) {
		return !Arrays.asList(AbstractSwingTreeFixture.TREE_ITEM_LEAFS_ONLY_METHODS).contains(aFixtureMethodName);
	}

	private boolean shallSuggestLeafs(String aFixtureMethodName) {
		return !Arrays.asList(AbstractSwingTreeFixture.TREE_ITEM_PARENTS_ONLY_METHODS).contains(aFixtureMethodName);
	}

	private List<CustomProposalDefinition> requestPathProposals(Map<String, Object> someParameterValues,
			boolean anIncludeParentsFlag, boolean anIncludeLeafsFlag, String aPathPrefix) {
		String tempComponentPath = (String) someParameterValues.get(COMPONENT_PATH_PARAMETER_NAME);
		if (tempComponentPath == null) {
			return null;
		}

		return runAuthorAssistRequest(new SwingAuthorAssistTreeItemQuery(tempComponentPath, anIncludeParentsFlag,
				anIncludeLeafsFlag, aPathPrefix), new SwingAuthorAssistRequestRunnable<CustomProposalDefinition>() {

			@Override
			public List<CustomProposalDefinition> run(Object[] someResults) throws IOException {
				List<CustomProposalDefinition> tempResults = new ArrayList<CustomProposalDefinition>();

				int tempCount = 1;
				for (Object tempGenericResult : someResults) {
					SwingAuthorAssistTreeItemQueryResult tempResult = (SwingAuthorAssistTreeItemQueryResult) tempGenericResult;

					int tempPriorityDivider;
					String tempSuffixMessage;
					if (tempResult.isSelected()) {
						tempPriorityDivider = 1;
						tempSuffixMessage = " (selected)";
					} else if (tempResult.isVisible()) {
						tempPriorityDivider = 2;
						tempSuffixMessage = " (visible)";
					} else {
						tempPriorityDivider = 3;
						tempSuffixMessage = "";
					}

					tempResults.add(new CustomProposalDefinition(packageString(tempResult.getItemPath()), tempResult
							.getItemPath() + tempSuffixMessage, (HIGH_BASE_PRIORITY / tempPriorityDivider) - tempCount,
							null));
					tempCount++;
				}

				return tempResults;
			}

		});
	}
}
