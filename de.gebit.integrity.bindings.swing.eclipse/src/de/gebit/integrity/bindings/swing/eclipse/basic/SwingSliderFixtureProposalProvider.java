/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse.basic;

import java.awt.Component;

import javax.swing.JSlider;

import de.gebit.integrity.bindings.swing.basic.SwingSliderFixture;
import de.gebit.integrity.bindings.swing.eclipse.SwingComponentFixtureProposalProvider;
import de.gebit.integrity.fixtures.CustomProposalProvider.CustomProposalFixtureLink;

/**
 * Proposal provider class for the {@link SwingSliderFixture}.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
@CustomProposalFixtureLink(SwingSliderFixture.class)
public class SwingSliderFixtureProposalProvider extends SwingComponentFixtureProposalProvider {

	@Override
	protected Class<? extends Component> getComponentClass() {
		return JSlider.class;
	}

}
