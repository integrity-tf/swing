/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.authorassist.queries;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToggleButton;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;
import de.gebit.integrity.bindings.swing.util.Base64;

/**
 * This queries the server for available components matching a certain class.
 * 
 * @author Slartibartfast
 * 
 */
public class SwingAuthorAssistComponentQuery extends AbstractSwingAuthorAssistQuery {

	/**
	 * The serial version.
	 */
	private static final long serialVersionUID = -4735860911905218675L;

	/**
	 * The component class name to filter. If null, don't filter.
	 */
	private String componentClassName;

	/**
	 * This cache is used internally for caching expensive stuff, such as generated component images.
	 */
	private Map<Object, Object> cacheMap = new HashMap<Object, Object>();

	/**
	 * This is used as newline indicator in both HTML and plain results.
	 */
	public static final String COMPONENT_LINE_NEWLINE = "<br>";

	/**
	 * Creates an instance.
	 * 
	 * @param aComponentClassName
	 */
	public SwingAuthorAssistComponentQuery(String aComponentClassName) {
		componentClassName = aComponentClassName;
	}

	public String getComponentClassName() {
		return componentClassName;
	}

	@Override
	public Object[] process(AbstractSwingComponentHandler aComponentHandler, JFrame anOwnerFrame) {
		try {
			Class<?> tempFilterClass = getClass().getClassLoader().loadClass(componentClassName);

			@SuppressWarnings("unchecked")
			List<Component> tempComponents = (List<Component>) aComponentHandler.findComponents(null,
					(Class<? extends Component>) tempFilterClass, anOwnerFrame);

			List<Object> tempResults = new ArrayList<Object>();
			for (Component tempComponent : tempComponents) {
				String tempLongPath = aComponentHandler.createUniquifiedComponentPath(tempComponent);
				String tempShortPath = aComponentHandler.createShortestComponentPath(tempComponent);

				if (tempLongPath != null) {
					if (tempShortPath == null) {
						tempShortPath = "";
					}
					tempResults.add(generateResult(tempShortPath, tempLongPath, tempComponent));
				}
			}

			return tempResults.toArray();
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		}

		return null;
	}

	/**
	 * Creates a result object for a specific component.
	 * 
	 * @param aShortPath
	 *            the shortest possible path
	 * @param aLongPath
	 *            the long path
	 * @param aComponent
	 *            the component
	 * @return the result object
	 */
	protected SwingAuthorAssistComponentQueryResult generateResult(String aShortPath, String aLongPath,
			Component aComponent) {
		StringBuilder tempHTMLDescription = new StringBuilder();
		StringBuilder tempPlainDescription = new StringBuilder();

		addHTMLComponentCSSLinePart(tempHTMLDescription);
		addHTMLComponentTagLinePart(tempHTMLDescription, "Component");
		tempHTMLDescription.append(aComponent.getClass().getName() + COMPONENT_LINE_NEWLINE);

		tempPlainDescription.append("Component: " + aComponent.getClass().getName() + COMPONENT_LINE_NEWLINE);

		if (aComponent instanceof JButton) {
			String tempButtonText = "'" + ((JButton) aComponent).getText() + "'";
			addHTMLComponentTagLinePart(tempHTMLDescription, "Text");
			tempHTMLDescription.append(tempButtonText + COMPONENT_LINE_NEWLINE);

			tempPlainDescription.append("Text: " + tempButtonText + COMPONENT_LINE_NEWLINE);
		} else if (aComponent instanceof JToggleButton) {
			String tempButtonText = "'" + ((JToggleButton) aComponent).getText() + "'";
			addHTMLComponentTagLinePart(tempHTMLDescription, "Text");
			tempHTMLDescription.append(tempButtonText + COMPONENT_LINE_NEWLINE);

			tempPlainDescription.append("Text: " + tempButtonText + COMPONENT_LINE_NEWLINE);
		}

		addHTMLComponentTagLinePart(tempHTMLDescription, "Enabled");
		tempHTMLDescription.append(aComponent.isEnabled());

		tempPlainDescription.append("Enabled: " + aComponent.isEnabled() + COMPONENT_LINE_NEWLINE);

		addHTMLComponentImageLinePart(tempHTMLDescription, aComponent);

		return new SwingAuthorAssistComponentQueryResult(aLongPath, aShortPath, tempHTMLDescription.toString(),
				tempPlainDescription.toString());
	}

	/**
	 * Adds the CSS style info to the given line.
	 * 
	 * @param aBuilder
	 *            the line builder
	 */
	protected void addHTMLComponentCSSLinePart(StringBuilder aBuilder) {
		aBuilder.append("<style type=\"text/css\">"
				+ "body { font-family: Arial, Sans-Serif; font-size: x-small; margin: 4px; } "
				+ ".tag { font-weight: bold; }" + "</style>");
	}

	/**
	 * Adds a tag to the HTML line being built. A tag is basically a header for some data afterwards and printed bold.
	 * 
	 * @param aBuilder
	 *            the string builder building the line
	 * @param aTag
	 *            the name of the tag
	 */
	protected void addHTMLComponentTagLinePart(StringBuilder aBuilder, String aTag) {
		aBuilder.append("<span class=\"tag\">" + aTag + ":</span> ");
	}

	/**
	 * Adds a little overview image pointing out the position of the provided component in its host frame with a red
	 * line.
	 * 
	 * @param aBuilder
	 *            the string builder building the line
	 * @param aComponent
	 *            the component
	 */
	protected void addHTMLComponentImageLinePart(StringBuilder aBuilder, Component aComponent) {
		Window tempOuterContainer = null;
		Container tempParent = aComponent.getParent();
		while (tempParent != null) {
			if (tempParent instanceof Window) {
				tempOuterContainer = (Window) tempParent;
				break;
			}
			tempParent = tempParent.getParent();
		}

		if (tempOuterContainer != null && tempOuterContainer.getWidth() > 10 && tempOuterContainer.getHeight() > 10) {
			// Scaled images of outer containers are stored in the provided
			// cache for quick reuse
			BufferedImage tempScaledOriginalImage = (BufferedImage) cacheMap.get(tempOuterContainer);
			Float tempScalingFactor = null;

			if (tempScaledOriginalImage != null) {
				tempScalingFactor = (Float) cacheMap.get(tempScaledOriginalImage);
			} else {
				BufferedImage tempImage = new BufferedImage(tempOuterContainer.getWidth(),
						tempOuterContainer.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D tempGraphics = (Graphics2D) tempImage.getGraphics();
				tempOuterContainer.paintAll(tempGraphics);

				float tempScalingFactor1 = (float) getComponentLineOverviewImgMaxWidth() / (float) tempImage.getWidth();
				float tempScalingFactor2 = (float) getComponentLineOverviewImgMaxHeight()
						/ (float) tempImage.getHeight();
				tempScalingFactor = tempScalingFactor1 < tempScalingFactor2 ? tempScalingFactor1 : tempScalingFactor2;

				if (tempScalingFactor >= 1.0) {
					tempScalingFactor = 1.0f;
					tempScaledOriginalImage = tempImage;
				} else {
					if (tempScalingFactor < getComponentLineOverviewMinScaling()) {
						tempScalingFactor = getComponentLineOverviewMinScaling();
					}

					tempScaledOriginalImage = new BufferedImage(Math.round((float) tempImage.getWidth()
							* tempScalingFactor), Math.round((float) tempImage.getHeight() * tempScalingFactor),
							BufferedImage.TYPE_INT_RGB);
					tempGraphics = (Graphics2D) tempScaledOriginalImage.getGraphics();
					tempGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					tempGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					tempGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					tempGraphics.drawImage(tempImage, 0, 0, tempScaledOriginalImage.getWidth(),
							tempScaledOriginalImage.getHeight(), 0, 0, tempImage.getWidth(), tempImage.getHeight(),
							null);
					tempGraphics.dispose();
				}

				cacheMap.put(tempOuterContainer, tempScaledOriginalImage);
				cacheMap.put(tempScaledOriginalImage, tempScalingFactor);
			}

			// Create a copy of the scaled image for addition of component
			// location info
			BufferedImage tempScaledImage = new BufferedImage(tempScaledOriginalImage.getWidth(),
					tempScaledOriginalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D tempGraphics = (Graphics2D) tempScaledImage.getGraphics();
			tempGraphics.drawImage(tempScaledOriginalImage, 0, 0, null);

			// Calculate the coordinates for drawing a border around the
			// component in question
			int tempComponentX = aComponent.getX();
			int tempComponentY = aComponent.getY();
			int tempBorderWidth = aComponent.getWidth();
			int tempBorderHeight = aComponent.getHeight();
			tempParent = aComponent.getParent();
			while (tempParent != null && !(tempParent == tempOuterContainer)) {
				tempComponentX += tempParent.getX();
				tempComponentY += tempParent.getY();
				tempParent = tempParent.getParent();
			}
			tempComponentX = Math.round((float) tempComponentX * tempScalingFactor) - 2;
			tempComponentY = Math.round((float) tempComponentY * tempScalingFactor) - 2;
			tempBorderWidth = Math.round((float) tempBorderWidth * tempScalingFactor) + 2;
			tempBorderHeight = Math.round((float) tempBorderHeight * tempScalingFactor) + 2;

			// Ensure component border coordinates are within valid bounds
			if (tempComponentX < 0) {
				tempComponentX = 0;
			} else if (tempComponentX >= tempScaledImage.getWidth()) {
				tempComponentX = tempScaledImage.getWidth() - 1;
			}
			if (tempComponentY < 0) {
				tempComponentY = 0;
			} else if (tempComponentY >= tempScaledImage.getHeight()) {
				tempComponentY = tempScaledImage.getHeight() - 1;
			}
			if (tempBorderWidth + tempComponentX > tempScaledImage.getWidth()) {
				tempBorderWidth = tempScaledImage.getWidth() - tempComponentX;
			}
			if (tempBorderHeight + tempComponentY > tempScaledImage.getHeight()) {
				tempBorderHeight = tempScaledImage.getHeight() - tempComponentY;
			}

			// Draw a border around the whole image and a red border around the
			// coordinate
			tempGraphics.setColor(Color.BLACK);
			tempGraphics.drawRect(0, 0, tempScaledImage.getWidth() - 1, tempScaledImage.getHeight() - 1);
			tempGraphics.setColor(Color.RED);
			tempGraphics.drawRect(tempComponentX, tempComponentY, tempBorderWidth, tempBorderHeight);
			tempGraphics.dispose();

			// Encode the final image in base64 and place it in the HTML as an
			// "inline css" image
			ByteArrayOutputStream tempOutBuffer = new ByteArrayOutputStream();
			try {
				ImageIO.write(tempScaledImage, "PNG", tempOutBuffer);
				final byte[] tempByteArray = tempOutBuffer.toByteArray();
				String tempEncodedData = Base64.encodeBytes(tempByteArray);
				aBuilder.append("<div style=\"width: " + tempScaledImage.getWidth() + "px; height: "
						+ tempScaledImage.getHeight() + "px; background: #000 url(data:image/png;base64,"
						+ tempEncodedData + ");\"/>");
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}

	/**
	 * Maximum width for the quickhelp overview pictures pointing out a specific component. If the real width is larger,
	 * scaling will occur.
	 */
	public final int getComponentLineOverviewImgMaxWidth() {
		return 260;
	}

	/**
	 * Maximum height for the quickhelp overview pictures pointing out a specific component. If the real height is
	 * larger, scaling will occur.
	 */
	public final int getComponentLineOverviewImgMaxHeight() {
		return 160;
	}

	/**
	 * Minimum scaling factor. If scaling below this factor would be necessary, the max height/width values are ignored.
	 */
	public final float getComponentLineOverviewMinScaling() {
		return 0.33f;
	}

	/**
	 * The result for {@link SwingAuthorAssistComponentQuery}s.
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	public static class SwingAuthorAssistComponentQueryResult implements Serializable {

		/**
		 * The serial version.
		 */
		private static final long serialVersionUID = -5072992175537055892L;

		/**
		 * The long component path.
		 */
		private String longPath;

		/**
		 * The shortest unique component path.
		 */
		private String shortPath;

		/**
		 * The HTML-enhanced description.
		 */
		private String htmlDescription;

		/**
		 * The plaintext description.
		 */
		private String plainDescription;

		/**
		 * Creates an instance.
		 * 
		 * @param aLongPath
		 *            the long component path
		 * @param aShortPath
		 *            the shortest unique component path
		 * @param aHtmlDescription
		 *            the HTML-enhanced description
		 * @param aPlainDescription
		 *            the plaintext description
		 */
		public SwingAuthorAssistComponentQueryResult(String aLongPath, String aShortPath, String aHtmlDescription,
				String aPlainDescription) {
			longPath = aLongPath;
			shortPath = aShortPath;
			htmlDescription = aHtmlDescription;
			plainDescription = aPlainDescription;
		}

		public String getLongPath() {
			return longPath;
		}

		public String getShortPath() {
			return shortPath;
		}

		public String getHtmlDescription() {
			return htmlDescription;
		}

		public String getPlainDescription() {
			return plainDescription;
		}

	}

}
