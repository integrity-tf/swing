/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.authorassist.queries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;
import de.gebit.integrity.bindings.swing.AmbiguousComponentPathException;
import de.gebit.integrity.bindings.swing.InvalidComponentPathException;

/**
 * This queries the server for possible items in a tree.
 * 
 * @author Slartibartfast
 * 
 */
public class SwingAuthorAssistTreeItemQuery extends AbstractSwingAuthorAssistQuery {

	/**
	 * The serial version.
	 */
	private static final long serialVersionUID = -4735860911905218675L;

	/**
	 * The name of the component to inspect.
	 */
	private String componentName;

	/**
	 * Whether parent nodes shall be included.
	 */
	private boolean includeParentNodes;

	/**
	 * Whether leafs shall be included.
	 */
	private boolean includeLeafNodes;

	/**
	 * A prefix to filter nodes for before returning them.
	 */
	private String filterPrefix;

	/**
	 * Creates an instance.
	 * 
	 * @param aComponentClassName
	 */
	public SwingAuthorAssistTreeItemQuery(String aComponentName, boolean anIncludeParentNodesFlag,
			boolean anIncludeLeafNodesFlag, String aFilterPrefix) {
		componentName = aComponentName;
		includeParentNodes = anIncludeParentNodesFlag;
		includeLeafNodes = anIncludeLeafNodesFlag;
		filterPrefix = aFilterPrefix;
	}

	public String getComponentame() {
		return componentName;
	}

	@Override
	public Object[] process(AbstractSwingComponentHandler aComponentHandler, JFrame anOwnerFrame) {
		try {
			JTree tempTree = aComponentHandler.findComponentGuarded(componentName, JTree.class, anOwnerFrame);
			List<Object> tempResults = new ArrayList<Object>();

			TreeNode tempRootNode = (TreeNode) tempTree.getModel().getRoot();
			recursiveProcessTreeItemQuery("", new TreePath(tempRootNode), tempRootNode, tempTree, tempResults);

			return tempResults.toArray();
		} catch (AmbiguousComponentPathException exc) {
			exc.printStackTrace();
		} catch (InvalidComponentPathException exc) {
			exc.printStackTrace();
		}

		return null;
	}

	private void recursiveProcessTreeItemQuery(String aPathString, TreePath aPath, TreeNode aNode, JTree aTree,
			List<Object> aResultList) {
		if (aNode.getAllowsChildren()) {
			for (int i = 0; i < aNode.getChildCount(); i++) {
				TreeNode tempChild = aNode.getChildAt(i);
				String tempChildPathPart = tempChild.toString();

				// Escape the childs' name, if necessary
				tempChildPathPart = tempChildPathPart.replace("^", "\\^");

				String tempChildPathString = aPathString;
				if (tempChildPathString.length() > 0) {
					tempChildPathString += "^";
				}
				tempChildPathString += tempChildPathPart;

				if (filterPrefix == null || filterPrefix.length() == 0 || filterPrefix.startsWith(tempChildPathPart)) {
					TreePath tempChildPath = aPath.pathByAddingChild(tempChild);
					if ((includeParentNodes && tempChild.getAllowsChildren())
							|| (includeLeafNodes && !tempChild.getAllowsChildren())) {
						boolean tempIsSelected = aTree.getSelectionModel().isPathSelected(tempChildPath);
						boolean tempIsVisible = aTree.isExpanded(aPath); // parent expanded = visible

						aResultList.add(new SwingAuthorAssistTreeItemQueryResult(tempChildPathString, tempIsVisible,
								tempIsSelected));
					}
					recursiveProcessTreeItemQuery(tempChildPathString, tempChildPath, tempChild, aTree, aResultList);
				}
			}
		}
	}

	/**
	 * The result for {@link SwingAuthorAssistTreeItemQuery}s.
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	public static class SwingAuthorAssistTreeItemQueryResult implements Serializable {

		/**
		 * The serial version.
		 */
		private static final long serialVersionUID = -1434246374822642340L;

		/**
		 * The tree items' path.
		 */
		private String itemPath;

		/**
		 * Whether the item is currently selected by the user in the tree.
		 */
		private boolean selected;

		/**
		 * Whether the item is currently visible.
		 */
		private boolean visible;

		/**
		 * Creates an instance.
		 */
		public SwingAuthorAssistTreeItemQueryResult(String anItemPath, boolean anIsVisibleFlag, boolean anIsSelectedFlag) {
			itemPath = anItemPath;
			selected = anIsSelectedFlag;
			visible = anIsVisibleFlag;
		}

		public String getItemPath() {
			return itemPath;
		}

		public boolean isSelected() {
			return selected;
		}

		public boolean isVisible() {
			return visible;
		}

	}

}
