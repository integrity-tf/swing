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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.gebit.integrity.bindings.swing.AbstractSwingFixture;
import de.gebit.integrity.bindings.swing.exceptions.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.exceptions.EventQueueTimeoutException;
import de.gebit.integrity.bindings.swing.exceptions.InvalidComponentPathException;
import de.gebit.integrity.fixtures.CustomProposalFixture;
import de.gebit.integrity.fixtures.FixtureMethod;
import de.gebit.integrity.fixtures.FixtureParameter;

/**
 * This fixture is an abstract base for fixtures providing access to JTree components.
 * 
 * @param <C>
 *            the component
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public abstract class AbstractSwingTreeFixture<C extends JTree> extends AbstractSwingFixture<C> implements
		CustomProposalFixture {

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
	public static final String[] TREE_ITEM_PARENTS_ONLY_METHODS = new String[] { "expandEntry", "collapseEntry",
			"isEntryExpanded" };

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
	 * Generates a textual tree path representation for the provided path.
	 * 
	 * @param aPath
	 *            the path to use
	 * @return the string representation of the path
	 */
	protected String generateTreePathString(TreePath aPath) {
		StringBuilder tempBuilder = new StringBuilder();
		for (int i = 0; i < aPath.getPathCount(); i++) {
			Object tempPathComponent = aPath.getPathComponent(i);

			String tempChildPathPart = tempPathComponent.toString();

			// Escape this childs' name, if necessary
			tempChildPathPart = tempChildPathPart.replace("^", "\\^");

			if (i > 0) {
				tempBuilder.append("^");
			}
			tempBuilder.append(tempChildPathPart);
		}

		return tempBuilder.toString();
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

	/**
	 * This determines the textual representation of a given tree node.
	 * 
	 * @param aPath
	 *            the path to look at
	 * @return the textual value
	 */
	protected String getTextOfNodeAtPath(TreePath aPath) {
		TreeNode tempNode = (TreeNode) aPath.getLastPathComponent();
		return tempNode.toString();
	}

	/**
	 * This determines the value of a given tree node. The value is a user-specified object in case a
	 * {@link DefaultMutableTreeNode} is used, or a string in case an other node is used.
	 * 
	 * @param aPath
	 *            the path to look at
	 * @return the value
	 */
	protected Object getValueOfNodeAtPath(TreePath aPath) {
		TreeNode tempNode = (TreeNode) aPath.getLastPathComponent();

		if (tempNode instanceof DefaultMutableTreeNode) {
			return ((DefaultMutableTreeNode) tempNode).getUserObject();
		} else {
			return tempNode.toString();
		}
	}

	/**
	 * Gets the number of columns from the given table.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @throws AmbiguousComponentPathException
	 * @throws EventQueueTimeoutException
	 * @throws InvalidComponentPathException
	 */
	@FixtureMethod(descriptionCall = "Get the number of {" + TREE_ITEM_PATH_PARAMETER_NAME + "?childs of entry '$"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "$'}{^" + TREE_ITEM_PATH_PARAMETER_NAME
			+ "?first-level items} in tree '$" + COMPONENT_PATH_PARAMETER_NAME + "$'", descriptionTest = "Check the number of {"
			+ TREE_ITEM_PATH_PARAMETER_NAME
			+ "?childs of entry '$"
			+ TREE_ITEM_PATH_PARAMETER_NAME
			+ "$'}{^"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "?first-level items} in tree '$" + COMPONENT_PATH_PARAMETER_NAME + "$'")
	public Integer getChildCount(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String anItemPath)
			throws AmbiguousComponentPathException, EventQueueTimeoutException, InvalidComponentPathException {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);
		TreePath tempPath = parseTreePathString(tempTree, anItemPath);

		return ((TreeNode) tempPath.getLastPathComponent()).getChildCount();
	}

	/**
	 * Get the value of a specific tree node entry. This returns the user object in case of a
	 * {@link DefaultMutableTreeNode}, or the textual string in case of any other kind of tree node.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param anItemPath
	 *            the tree entries' path
	 * @return the value of the entry
	 */
	@FixtureMethod(descriptionCall = "Get the value of entry '$" + TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'", descriptionTest = "Check the value of entry '$"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$" + COMPONENT_PATH_PARAMETER_NAME + "$'")
	public Object getEntryValue(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String anItemPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);
		TreePath tempPath = parseTreePathString(tempTree, anItemPath);

		return getValueOfNodeAtPath(tempPath);
	}

	/**
	 * Get the text of a specific tree node entry.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param anItemPath
	 *            the tree entries' path
	 * @return the textual value of the entry
	 */
	@FixtureMethod(descriptionCall = "Get the text of entry '$" + TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'", descriptionTest = "Check the text of entry '$"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$" + COMPONENT_PATH_PARAMETER_NAME + "$'")
	public Object getEntryText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String anItemPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);
		TreePath tempPath = parseTreePathString(tempTree, anItemPath);

		return getTextOfNodeAtPath(tempPath);
	}

	/**
	 * Expands an entry in a tree. If parents of this entry aren't expanded yet, they will be expanded automatically as
	 * well.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param anItemPath
	 *            the tree entries' path
	 */
	@FixtureMethod(description = "Expand entry '$" + TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'")
	public void expandEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String anItemPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);
		TreePath tempPath = parseTreePathString(tempTree, anItemPath);

		tempTree.expandPath(tempPath);
	}

	/**
	 * Collapses an entry in a tree.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param anItemPath
	 *            the tree entries' path
	 */
	@FixtureMethod(description = "Collapse entry '$" + TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'")
	public void collapseEntry(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String anItemPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);
		TreePath tempPath = parseTreePathString(tempTree, anItemPath);

		tempTree.collapsePath(tempPath);
	}

	/**
	 * Checks whether a given entry in a tree is currently expanded.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param anItemPath
	 *            the tree entries' path
	 */
	@FixtureMethod(descriptionCall = "Get the expanded state of entry '$" + TREE_ITEM_PATH_PARAMETER_NAME
			+ "$' in tree '$" + COMPONENT_PATH_PARAMETER_NAME + "$'", descriptionTest = "Check whether entry '$"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$" + COMPONENT_PATH_PARAMETER_NAME + "$' is expanded")
	public Boolean isEntryExpanded(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String anItemPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);
		TreePath tempPath = parseTreePathString(tempTree, anItemPath);

		return tempTree.isExpanded(tempPath);
	}

	/**
	 * Checks whether a given entry in a tree is currently visible. An entry is considered visible if its parents are
	 * expanded.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param anItemPath
	 *            the tree entries' path
	 */
	@FixtureMethod(descriptionCall = "Get the visibility state of entry '$" + TREE_ITEM_PATH_PARAMETER_NAME
			+ "$' in tree '$" + COMPONENT_PATH_PARAMETER_NAME + "$'", descriptionTest = "Check whether entry '$"
			+ TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$" + COMPONENT_PATH_PARAMETER_NAME + "$' is visible")
	public Boolean isEntryVisible(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String anItemPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);
		TreePath tempPath = parseTreePathString(tempTree, anItemPath);

		return tempTree.isVisible(tempPath);
	}

	/**
	 * Selects one or multiple entries in the given tree.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @param someEntryPaths
	 *            one or multiple paths to tree entries
	 */
	@FixtureMethod(description = "Select entry '$" + TREE_ITEM_PATH_PARAMETER_NAME + "$' in tree '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'")
	public void selectEntries(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath,
			@FixtureParameter(name = TREE_ITEM_PATH_PARAMETER_NAME) String[] someEntryPaths) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);

		if (someEntryPaths == null || someEntryPaths.length == 0) {
			throw new IllegalArgumentException("At least one tree entry path must be provided.");
		}

		TreePath[] tempPaths = new TreePath[someEntryPaths.length];
		for (int i = 0; i < someEntryPaths.length; i++) {
			tempPaths[i] = parseTreePathString(tempTree, someEntryPaths[i]);
		}

		tempTree.setSelectionPaths(tempPaths);
	}

	/**
	 * Returns the paths of the selected entries in a given tree.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @return the paths of the selected entries
	 */
	@FixtureMethod(descriptionCall = "Get the paths of the selected entries in tree '$" + COMPONENT_PATH_PARAMETER_NAME
			+ "$'")
	public String[] getSelectedEntriesPath(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);

		ArrayList<String> tempPathStrings = new ArrayList<String>();
		for (TreePath tempPath : tempTree.getSelectionPaths()) {
			tempPathStrings.add(generateTreePathString(tempPath));
		}

		return tempPathStrings.toArray(new String[tempPathStrings.size()]);
	}

	/**
	 * Returns the textual representations of the selected entries in a given tree.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @return the paths of the selected entries
	 */
	@FixtureMethod(descriptionCall = "Get the texts of the selected entries in tree '$" + COMPONENT_PATH_PARAMETER_NAME
			+ "$'")
	public String[] getSelectedEntriesText(@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);

		ArrayList<String> tempStrings = new ArrayList<String>();
		for (TreePath tempPath : tempTree.getSelectionPaths()) {
			tempStrings.add(getTextOfNodeAtPath(tempPath));
		}

		return tempStrings.toArray(new String[tempStrings.size()]);
	}

	/**
	 * Returns the textual representations of the selected entries in a given tree.
	 * 
	 * @param aComponentPath
	 *            the path to the component
	 * @return the paths of the selected entries
	 */
	@FixtureMethod(descriptionCall = "Get the values of the selected entries in tree '$"
			+ COMPONENT_PATH_PARAMETER_NAME + "$'")
	public Object[] getSelectedEntriesValue(
			@FixtureParameter(name = COMPONENT_PATH_PARAMETER_NAME) String aComponentPath) {
		JTree tempTree = findComponentGuarded(aComponentPath, JTree.class, null);

		ArrayList<Object> tempObjects = new ArrayList<Object>();
		for (TreePath tempPath : tempTree.getSelectionPaths()) {
			tempObjects.add(getValueOfNodeAtPath(tempPath));
		}

		return tempObjects.toArray(new Object[tempObjects.size()]);
	}
}
