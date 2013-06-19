/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.basic;

import javax.swing.JTabbedPane;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture provides access to {@link JTabbedPane} components.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingTabbedPaneFixture extends AbstractSwingFixture<JTabbedPane> implements CustomProposalFixture {

	/**
	 * The parameter name for tab text.
	 */
	public static final String TAB_TEXT_PARAMETER_NAME = "text";

	/**
	 * The parameter name for tab position.
	 */
	public static final String TAB_POSITION_PARAMETER_NAME = "position";

	/**
	 * These methods accept tab title texts as their expected result.
	 */
	public static final String[] METHODS_WITH_TAB_TEXT_RESULTS = { "getSelectedTab", "getTabTitle" };

	/**
	 * Selects a given tab in a tabbed pane. The tab to select can be identified either by position or by its title.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aTabPosition
	 *            the position of the tab (one-based)
	 * @param aTabText
	 *            the title text of the tab
	 */
	@FixtureMethod(description = "Select the tab {" + TAB_POSITION_PARAMETER_NAME + "?at position $"
			+ TAB_POSITION_PARAMETER_NAME + "$}{" + TAB_TEXT_PARAMETER_NAME + "?'$text$'} in tabbed pane '$name$'")
	public void selectTab(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TAB_POSITION_PARAMETER_NAME) Integer aTabPosition,
			@FixtureParameter(name = TAB_TEXT_PARAMETER_NAME) String aTabText) {
		final JTabbedPane tempTabbedPane = findComponentGuarded(aComponentPath);

		final Integer tempTabToSelect;
		if (aTabPosition != null) {
			if (aTabPosition < 1) {
				throw new IndexOutOfBoundsException("Tab positions below 1 are illegal.");
			} else if (aTabPosition > tempTabbedPane.getTabCount()) {
				throw new IndexOutOfBoundsException("The tab position " + aTabPosition
						+ " is higher than the maximum tab index " + tempTabbedPane.getTabCount() + ".");
			} else {
				tempTabToSelect = aTabPosition - 1;
			}
		} else if (aTabText != null) {
			Integer tempFoundAtIndex = null;
			for (int i = 0; i < tempTabbedPane.getTabCount(); i++) {
				if (aTabText.equals(tempTabbedPane.getTitleAt(i))) {
					tempFoundAtIndex = i;
					break;
				}
			}

			if (tempFoundAtIndex == null) {
				StringBuffer tempBuffer = new StringBuffer("The tab list does not contain a tab '" + aTabText
						+ "'. Valid tabs are: ");
				for (int i = 0; i < tempTabbedPane.getTabCount(); i++) {
					if (i > 0) {
						tempBuffer.append(", ");
					}
					tempBuffer.append("'" + tempTabbedPane.getTitleAt(i) + "'");
				}
				throw new IllegalArgumentException(tempBuffer.toString());
			} else {
				tempTabToSelect = tempFoundAtIndex;
			}
		} else {
			throw new IllegalArgumentException("You need to provide either a position or a text.");
		}

		focusComponent(tempTabbedPane);

		runOnEventQueueAndWait(new Runnable() {

			@Override
			public void run() {
				tempTabbedPane.setSelectedIndex(tempTabToSelect);
			}
		});
	}

	/**
	 * Determines the currently selected tabs' title and returns it.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @return
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected tabs' title from tabbed pane '$name$'", descriptionTest = "Checks the title of the selected tab in tabbed pane '$name$'")
	public String getSelectedTab(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		JTabbedPane tempTabbedPane = findComponentGuarded(aComponentPath);

		return tempTabbedPane.getTitleAt(tempTabbedPane.getSelectedIndex());
	}

	/**
	 * Determines the currently selected tabs' position (one-based) and returns it.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @return
	 */
	@FixtureMethod(descriptionCall = "Fetches the selected tabs' position from tabbed pane '$name$'", descriptionTest = "Checks the position of the selected tab in tabbed pane '$name$'")
	public Integer getSelectedTabPosition(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		JTabbedPane tempTabbedPane = findComponentGuarded(aComponentPath);

		return tempTabbedPane.getSelectedIndex() + 1;
	}

	/**
	 * Determines the title of a specific tab and returns it.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param aTabPosition
	 *            the position of the tab (one-based)
	 * @return
	 */
	@FixtureMethod(descriptionCall = "Fetches the title of the tab at position $" + TAB_POSITION_PARAMETER_NAME
			+ "$ from tabbed pane '$name$'", descriptionTest = "Checks the title of the tab at position $"
			+ TAB_POSITION_PARAMETER_NAME + "$ in tabbed pane '$name$'")
	public String getTabTitle(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TAB_POSITION_PARAMETER_NAME) Integer aTabPosition) {
		JTabbedPane tempTabbedPane = findComponentGuarded(aComponentPath);

		if (aTabPosition == null || aTabPosition < 1) {
			throw new IndexOutOfBoundsException("Tab positions below 1 are illegal.");
		} else if (aTabPosition > tempTabbedPane.getTabCount()) {
			throw new IndexOutOfBoundsException("The tab position " + aTabPosition
					+ " is higher than the maximum tab index " + tempTabbedPane.getTabCount() + ".");
		}

		return tempTabbedPane.getTitleAt(aTabPosition - 1);
	}
}
