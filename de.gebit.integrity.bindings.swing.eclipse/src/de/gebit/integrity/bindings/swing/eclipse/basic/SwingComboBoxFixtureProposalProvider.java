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

import javax.swing.JComboBox;

import de.gebit.integrity.bindings.swing.basic.SwingComboBoxFixture;
import de.gebit.integrity.bindings.swing.eclipse.AbstractSwingComponentFixtureProposalProvider;
import de.gebit.integrity.fixtures.CustomProposalProvider.CustomProposalFixtureLink;

/**
 * Proposal provider class for the {@link SwingComboBoxFixture}.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
@CustomProposalFixtureLink(SwingComboBoxFixture.class)
public class SwingComboBoxFixtureProposalProvider extends AbstractSwingComponentFixtureProposalProvider {

	@Override
	protected Class<? extends Component> getComponentClass() {
		return JComboBox.class;
	}

	@Override
	public List<CustomProposalDefinition> defineParameterProposals(String aFixtureMethodName, String aParameterName,
			Map<String, Object> someParameterValues) {
		if (SwingComboBoxFixture.ENTRY_TEXT_PARAMETER_NAME.equals(aParameterName)) {
			return requestEntryProposals(someParameterValues);
		} else {
			return super.defineParameterProposals(aFixtureMethodName, aParameterName, someParameterValues);
		}
	}

	@Override
	public List<CustomProposalDefinition> defineResultProposals(String aFixtureMethodName, String aResultName,
			Object aResultValue, Map<String, Object> someParameterValues) {
		if (Arrays.asList(SwingComboBoxFixture.METHODS_WITH_ENTRY_TEXT_RESULTS).contains(aFixtureMethodName)) {
			return requestEntryProposals(someParameterValues);
		} else {
			return super.defineResultProposals(aFixtureMethodName, aResultName, aResultValue, someParameterValues);
		}
	}

	private List<CustomProposalDefinition> requestEntryProposals(Map<String, Object> someParameterValues) {
		String tempComponentName = (String) someParameterValues.get(SwingComboBoxFixture.COMPONENT_PATH_PARAMETER_NAME);
		if (tempComponentName == null) {
			return null;
		}

		final Integer tempPosition = (Integer) someParameterValues
				.get(SwingComboBoxFixture.ENTRY_POSITION_PARAMETER_NAME);

		return runAuthorAssistRequest("comboboxentries", tempComponentName,
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
