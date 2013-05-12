/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;

/**
 * Abstract base class for proposal providers which propose possible results based on data currently found in the
 * application. This one is geared towards a specific "list"-style pattern.
 * 
 * @author Slartibartfast
 * 
 */
public abstract class AbstractResultSuggestingSwingComponentFixtureProposalProvider extends
		AbstractSwingComponentFixtureProposalProvider {

	/**
	 * Returns the parameter name to use for the value.
	 * 
	 * @return
	 */
	protected abstract String getRelevantTextParameterName();

	/**
	 * Returns the parameter name to use for positions.
	 * 
	 * @return
	 */
	protected abstract String getRelevantPositionParameterName();

	/**
	 * Returns the method names for which results shall be suggested.
	 * 
	 * @return
	 */
	protected abstract String[] getRelevantMethods();

	/**
	 * Returns the author assist request type to send to the author assist server.
	 * 
	 * @return
	 */
	protected abstract String getAuthorAssistRequestType();

	@Override
	public List<CustomProposalDefinition> defineParameterProposals(String aFixtureMethodName, String aParameterName,
			Map<String, Object> someParameterValues) {
		if (getRelevantTextParameterName().equals(aParameterName)) {
			return requestEntryProposals(someParameterValues);
		} else {
			return super.defineParameterProposals(aFixtureMethodName, aParameterName, someParameterValues);
		}
	}

	@Override
	public List<CustomProposalDefinition> defineResultProposals(String aFixtureMethodName, String aResultName,
			Object aResultValue, Map<String, Object> someParameterValues) {
		if (Arrays.asList(getRelevantMethods()).contains(aFixtureMethodName)) {
			return requestEntryProposals(someParameterValues);
		} else {
			return super.defineResultProposals(aFixtureMethodName, aResultName, aResultValue, someParameterValues);
		}
	}

	private List<CustomProposalDefinition> requestEntryProposals(Map<String, Object> someParameterValues) {
		String tempComponentName = (String) someParameterValues.get(AbstractSwingFixture.COMPONENT_PATH_PARAMETER_NAME);
		if (tempComponentName == null) {
			return null;
		}

		final Integer tempPosition = getRelevantPositionParameterName() != null ? (Integer) someParameterValues
				.get(getRelevantPositionParameterName()) : null;

		return runAuthorAssistRequest(getAuthorAssistRequestType(), tempComponentName,
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
