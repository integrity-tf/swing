/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse.tables;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.gebit.integrity.bindings.swing.eclipse.AbstractSwingFixtureAssist;
import de.gebit.integrity.bindings.swing.tables.SwingTableContentFixture;
import de.gebit.integrity.fixtures.ArbitraryParameterEnumerator;
import de.gebit.integrity.fixtures.ArbitraryParameterFixtureLink;

/**
 * This parameter enumerator parses data from the swing author assist server and produces proposals for arbitrary
 * parameters in JTable tests, basically resembling the columns of the table.
 * 
 * @author Slartibartfast
 * 
 */
@ArbitraryParameterFixtureLink(SwingTableContentFixture.class)
public class SwingTableContentFixtureParameterEnumerator extends AbstractSwingFixtureAssist implements
		ArbitraryParameterEnumerator {

	@Override
	public List<ArbitraryParameterDefinition> defineArbitraryParameters(String aFixtureMethodName,
			Map<String, Object> someFixedParameters, List<String> aNestedObjectPath) {
		return null;
	}

	@Override
	public List<ArbitraryParameterDefinition> defineArbitraryResults(String aFixtureMethodName,
			Map<String, Object> someFixedParameters, List<String> aNestedObjectPath) {
		String tempTableName = (String) someFixedParameters.get("name");
		if (tempTableName == null) {
			return null;
		}

		return runAuthorAssistRequest("tablecols", tempTableName,
				new SwingAuthorAssistRequestRunnable<ArbitraryParameterDefinition>() {

					@Override
					public List<ArbitraryParameterDefinition> run(BufferedReader aReader) throws IOException {
						List<ArbitraryParameterDefinition> tempResults = new ArrayList<ArbitraryParameterDefinition>();

						String tempLine = aReader.readLine();
						while (tempLine != null) {
							String[] tempParts = tempLine.split(":");

							tempResults.add(new ArbitraryParameterDefinition(tempParts[0], tempParts[1]));

							tempLine = aReader.readLine();
						}

						return tempResults;
					}
				});
	}
}
