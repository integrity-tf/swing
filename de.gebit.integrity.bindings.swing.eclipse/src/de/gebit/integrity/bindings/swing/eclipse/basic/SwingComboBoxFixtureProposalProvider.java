/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse.basic;

import java.awt.Component;

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

}
