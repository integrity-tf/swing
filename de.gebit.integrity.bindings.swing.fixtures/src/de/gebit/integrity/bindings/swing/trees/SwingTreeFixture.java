/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.trees;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.gebit.integrity.bindings.swing.exceptions.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.exceptions.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.exceptions.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture provides access to {@link JTree} components.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingTreeFixture extends AbstractSwingTreeFixture implements
		CustomProposalFixture {

	/**
	 * Gets the number of columns from the given table.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the number of {"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "?childs of item '$"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "$'}{^"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "?first-level items} in tree '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'", descriptionTest = "Check the number of {"
			+ TREE_ITEM_PATH_PARAMETER_NAME
			+ "?childs of item '$"
			+ TREE_ITEM_PATH_PARAMETER_NAME
			+ "$'}{^"
			+ TREE_ITEM_PATH_PARAMETER_NAME
			+ "?first-level items} in tree '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'")
	public Integer getTreeChildCount(
			@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String anItemPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException,
			InvalidComponentPathException {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);

		TreePath tempPath = parseTreePathString(tempTree, anItemPath);

		return ((TreeNode) tempPath.getLastPathComponent()).getChildCount();
	}
}
