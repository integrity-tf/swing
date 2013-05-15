/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.trees;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.fixtures.CustomProposalFixture;

/**
 * This fixture is an abstract base for fixtures providing access to JTree components.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public abstract class AbstractSwingTreeFixture extends AbstractSwingFixture implements CustomProposalFixture {

	/**
	 * The name of the parameter denoting the path to individual tree items.
	 */
	public static final String TREE_ITEM_PATH_PARAMETER_NAME = "path";

	/**
	 * The names of methods for which only leaf nodes shall be suggested for {@link #TREE_ITEM_PATH_PARAMETER_NAME}.
	 */
	public static final String[] TREE_ITEM_LEAFS_ONLY_METHODS = new String[] {};

	/**
	 * The names of methods for which only parent nodes shall be suggested for {@link #TREE_ITEM_PATH_PARAMETER_NAME}.
	 */
	public static final String[] TREE_ITEM_PARENTS_ONLY_METHODS = new String[] {};

	/**
	 * Parses a serialized path into a {@link TreePath} object. When parsing, '^' chars are used as delimiters of
	 * individual path parts, except if a '\' is encountered right in front of a '^', which escapes the '^'. '\' only
	 * work this way in combination with '^' - if no '^' follows, the '\' is just interpreted as normal character, so
	 * you don't need to "escape the escape char" (except if you actually want to express the string "\^", in which
	 * you'll need to escape the '\' with another '\', and the '^' as well, so you'll get "\\\^"). Bottom line: escaping
	 * '\' is allowed, but in most cases optional.
	 * 
	 * @param aPathString
	 * @return the parsed TreePath object
	 */
	protected TreePath parseTreePathString(JTree aTree, String aPathString) {
		List<String> tempPathParts = new ArrayList<String>();

		if (aPathString != null) {
			boolean tempPossibleEscape = false;
			StringBuilder tempCurrentPart = new StringBuilder();
			for (char tempChar : aPathString.toCharArray()) {
				if (!tempPossibleEscape) {
					if (tempChar == '\\') {
						tempPossibleEscape = true;
					} else if (tempChar == '^') {
						tempPathParts.add(tempCurrentPart.toString());
						tempCurrentPart = new StringBuilder();
					} else {
						tempCurrentPart.append(tempChar);
					}
				} else {
					if (tempChar == '^') {
						tempCurrentPart.append('^');
					} else if (tempChar == '\\') {
						tempCurrentPart.append('\\');
					} else {
						tempCurrentPart.append(tempChar);
					}
					tempPossibleEscape = false;
				}
			}

			if (tempCurrentPart.length() > 0) {
				tempPathParts.add(tempCurrentPart.toString());
			}
		}

		TreeNode tempCurrentNode = (TreeNode) aTree.getModel().getRoot();
		if (tempCurrentNode == null) {
			return null;
		}

		List<TreeNode> tempPathNodes = new ArrayList<TreeNode>();
		tempPathNodes.add(tempCurrentNode);
		for (String tempNextPart : tempPathParts) {
			if (tempCurrentNode.getAllowsChildren()) {
				boolean tempFound = false;
				for (int i = 0; i < tempCurrentNode.getChildCount(); i++) {
					TreeNode tempPossibleChild = tempCurrentNode.getChildAt(i);
					if (checkPathPartEquality(tempNextPart, tempPossibleChild)) {
						tempPathNodes.add(tempPossibleChild);
						tempCurrentNode = tempPossibleChild;
						tempFound = true;
						continue;
					}
				}

				if (!tempFound) {
					throw new IllegalArgumentException("Tree Item path element '" + tempNextPart + "' not found.");
				}
			} else {
				throw new IllegalArgumentException("Tree Item path element '" + tempNextPart
						+ "' can not be matched: previous element doesn't have any childs.");
			}
		}

		return new TreePath(tempPathNodes.toArray());
	}

	/**
	 * Checks whether a given (partial) path string matches a given {@link TreeNode}.
	 * 
	 * @param aPathPart
	 *            the path part
	 * @param aNode
	 *            the node
	 * @return true if the node is considered to match the path part, false otherwise
	 */
	protected boolean checkPathPartEquality(String aPathPart, TreeNode aNode) {
		return aPathPart.equals(aNode.toString());
	}

}
