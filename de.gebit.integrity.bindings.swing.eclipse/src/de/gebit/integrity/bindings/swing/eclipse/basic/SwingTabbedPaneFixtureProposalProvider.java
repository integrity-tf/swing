/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse.basic;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;

import de.gebit.integrity.bindings.swing.basic.SwingTabbedPaneFixture;
import de.gebit.integrity.bindings.swing.eclipse.AbstractSwingComponentFixtureProposalProvider;
import de.gebit.integrity.fixtures.CustomProposalProvider.CustomProposalFixtureLink;

/**
 * Proposal provider class for the {@link SwingTabbedPaneFixture}.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
@CustomProposalFixtureLink(SwingTabbedPaneFixture.class)
public class SwingTabbedPaneFixtureProposalProvider extends AbstractSwingComponentFixtureProposalProvider {

	@Override
	protected Class<? extends Component> getComponentClass() {
		return JTabbedPane.class;
	}

	@Override
	public List<CustomProposalDefinition> defineParameterProposals(String aFixtureMethodName, String aParameterName,
			Map<String, Object> someParameterValues) {
		if (SwingTabbedPaneFixture.TAB_TEXT_PARAMETER_NAME.equals(aParameterName)) {
			return requestTabTitleProposals(someParameterValues);
		} else {
			return super.defineParameterProposals(aFixtureMethodName, aParameterName, someParameterValues);
		}
	}

	@Override
	public List<CustomProposalDefinition> defineResultProposals(String aFixtureMethodName, String aResultName,
			Object aResultValue, Map<String, Object> someParameterValues) {
		if (Arrays.asList(SwingTabbedPaneFixture.METHODS_WITH_TAB_TEXT_RESULTS).contains(aFixtureMethodName)) {
			return requestTabTitleProposals(someParameterValues);
		} else {
			return super.defineResultProposals(aFixtureMethodName, aResultName, aResultValue, someParameterValues);
		}
	}

	private List<CustomProposalDefinition> requestTabTitleProposals(Map<String, Object> someParameterValues) {
		String tempComponentName = (String) someParameterValues
				.get(SwingTabbedPaneFixture.COMPONENT_PATH_PARAMETER_NAME);
		if (tempComponentName == null) {
			return null;
		}

		final Integer tempPosition = (Integer) someParameterValues
				.get(SwingTabbedPaneFixture.TAB_POSITION_PARAMETER_NAME);

		return runAuthorAssistRequest("tabtitles", tempComponentName,
				new SwingAuthorAssistRequestRunnable<CustomProposalDefinition>() {

					@Override
					public List<CustomProposalDefinition> run(BufferedReader aReader) throws IOException {
						List<CustomProposalDefinition> tempResults = new ArrayList<CustomProposalDefinition>();

						String tempLine = aReader.readLine();
						int tempCount = 1;
						while (tempLine != null) {
							if (tempPosition != null && tempCount == tempPosition) {
								tempResults.add(new CustomProposalDefinition("\"" + tempLine + "\"", tempLine
										+ " (currently at pos. " + tempPosition + ")", HIGH_BASE_PRIORITY, null));
							} else {
								tempResults.add(new CustomProposalDefinition("\"" + tempLine + "\"", tempLine,
										HIGH_BASE_PRIORITY - tempCount, null));
							}
							tempCount++;

							tempLine = aReader.readLine();
						}

						return tempResults;
					}
				});
	}

}
