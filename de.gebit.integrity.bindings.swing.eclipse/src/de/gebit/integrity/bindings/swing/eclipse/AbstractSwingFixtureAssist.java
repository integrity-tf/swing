/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.eclipse;

import java.awt.Component;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;
import de.gebit.integrity.bindings.swing.authorassist.SwingAuthorAssistServer;
import de.gebit.integrity.bindings.swing.authorassist.SwingAuthorAssistServer.ClassloaderAwareObjectInputStream;
import de.gebit.integrity.bindings.swing.authorassist.queries.AbstractSwingAuthorAssistQuery;
import de.gebit.integrity.bindings.swing.authorassist.queries.SwingAuthorAssistComponentQuery;
import de.gebit.integrity.bindings.swing.authorassist.queries.SwingAuthorAssistComponentQuery.SwingAuthorAssistComponentQueryResult;
import de.gebit.integrity.fixtures.CustomProposalProvider.CustomProposalDefinition;

/**
 * Abstract base class for providing content assist features to Swing fixtures. This basic functionality mostly covers
 * the identification of components (= assistance while filling the "name" parameter).
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class AbstractSwingFixtureAssist extends AbstractSwingComponentHandler {

	/**
	 * The default timeout for an author-assist query in msecs.
	 */
	protected static final int DEFAULT_AUTHOR_ASSIST_REQUEST_TIMEOUT = 5000;

	/**
	 * Creates a socket for communication with the {@link SwingAuthorAssistServer}.
	 * 
	 * @return the initialized and connected socket
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	protected Socket createSocket() throws UnknownHostException, IOException {
		return new Socket(SwingAuthorAssistServer.DEFAULT_HOST, SwingAuthorAssistServer.DEFAULT_PORT);
	}

	/**
	 * Returns the timeout to use for author-assist queries.
	 * 
	 * @return
	 */
	protected int getAuthorAssistQueryTimeout() {
		return DEFAULT_AUTHOR_ASSIST_REQUEST_TIMEOUT;
	}

	/**
	 * Returns the classloader to use for author assist response deserialization.
	 * 
	 * @return
	 */
	protected ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}

	/**
	 * Runs a {@link SwingAuthorAssistRequestRunnable} against an author assist server. The connection is established
	 * automatically, then the runnables' code is run, and the connection closed.
	 * 
	 * @param aQuery
	 *            the request to submit to the author assist server
	 * @param aRunnable
	 *            the runnable to execute for response parsing
	 * @return a list of proposal definitions returned by the runnable
	 */
	protected <T extends Object> List<T> runAuthorAssistRequest(AbstractSwingAuthorAssistQuery aQuery,
			SwingAuthorAssistRequestRunnable<T> aRunnable) {
		Timer tempTimeoutTimer = new Timer();
		Socket tempSocket = null;
		ObjectOutputStream tempObjectOutputStream = null;
		ObjectInputStream tempObjectInputStream = null;
		try {
			tempSocket = createSocket();

			final Socket tempFinalSocket = tempSocket;
			final Thread tempAuthorAssistThread = Thread.currentThread();
			tempTimeoutTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						tempFinalSocket.close();
					} catch (IOException exc) {
						// ignored
					}
					tempAuthorAssistThread.interrupt();
				}
			}, getAuthorAssistQueryTimeout());

			tempObjectOutputStream = new ObjectOutputStream(tempSocket.getOutputStream());
			tempObjectOutputStream.writeObject(aQuery);
			tempObjectOutputStream.flush();

			tempObjectInputStream = new ClassloaderAwareObjectInputStream(tempSocket.getInputStream(),
					getClassLoader());
			Object[] tempResult = (Object[]) tempObjectInputStream.readObject();

			return aRunnable.run(tempResult);
		} catch (UnknownHostException exc) {
			exc.printStackTrace();
		} catch (IOException exc) {
			exc.printStackTrace();
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		} finally {
			if (tempTimeoutTimer != null) {
				tempTimeoutTimer.cancel();
			}
			if (tempObjectOutputStream != null) {
				try {
					tempObjectOutputStream.close();
				} catch (IOException exc) {
					// ignoring close errors
				}
			}
			if (tempObjectInputStream != null) {
				try {
					tempObjectInputStream.close();
				} catch (IOException exc) {
					// ignoring close errors
				}
			}
			if (tempSocket != null) {
				try {
					tempSocket.close();
				} catch (IOException exc) {
					// ignoring close errors
				}
			}
		}

		return null;
	}

	/**
	 * Requests all proposals from the {@link SwingAuthorAssistServer} for the given component class. The proposals
	 * which are returned are parsed and converted into {@link CustomProposalDefinition} instances as well.
	 * 
	 * @param aComponentClass
	 *            the component class to filter for
	 * @return the list of proposals (may be empty or null in case of no proposals/errors)
	 */
	protected List<CustomProposalDefinition> requestProposals(Class<? extends Component> aComponentClass) {
		return runAuthorAssistRequest(new SwingAuthorAssistComponentQuery(aComponentClass.getName()),
				new SwingAuthorAssistRequestRunnable<CustomProposalDefinition>() {

					@Override
					public List<CustomProposalDefinition> run(Object[] someResults) throws IOException {
						List<CustomProposalDefinition> tempList = new ArrayList<CustomProposalDefinition>();

						for (Object tempGenericResult : someResults) {
							SwingAuthorAssistComponentQueryResult tempResult = (SwingAuthorAssistComponentQueryResult) tempGenericResult;

							String tempLongPath = tempResult.getLongPath();
							String tempShortPath = tempResult.getShortPath();
							String tempHTMLDetails = tempResult.getHtmlDescription();
							String tempPlainDetails = tempResult.getPlainDescription();

							if (tempHTMLDetails.length() == 0) {
								tempHTMLDetails = null;
							}
							if (tempPlainDetails.length() == 0) {
								tempPlainDetails = null;
							} else {
								tempPlainDetails = tempPlainDetails
										.replace(SwingAuthorAssistComponentQuery.COMPONENT_LINE_NEWLINE, "\n");
							}

							boolean tempHasShortPath = tempShortPath.length() > 0
									&& !tempShortPath.equals(tempLongPath);

							tempList.add(new CustomProposalDefinition('"' + tempLongPath + '"', tempLongPath,
									tempHTMLDetails, tempPlainDetails, tempHasShortPath ? 0 : 1, true));
							if (tempShortPath.length() > 0 && !tempShortPath.equals(tempLongPath)) {
								tempList.add(new CustomProposalDefinition('"' + tempShortPath + '"', tempShortPath,
										tempHTMLDetails, tempPlainDetails, 2, true));
							}
						}

						return tempList;
					}
				});
	}

	/**
	 * Implementations of this interface encapsulate parsers for responses from the {@link SwingAuthorAssistServer}.
	 * 
	 * 
	 * @param <T>
	 * @author Slartibartfast
	 * 
	 */
	protected interface SwingAuthorAssistRequestRunnable<T> {

		/**
		 * This shall contain the code that processes the result objects into suggestions.
		 * 
		 * @param someResults
		 *            the results
		 * @return a list of proposals
		 * @throws IOException
		 */
		List<T> run(Object[] someResults) throws IOException;

	}
}
