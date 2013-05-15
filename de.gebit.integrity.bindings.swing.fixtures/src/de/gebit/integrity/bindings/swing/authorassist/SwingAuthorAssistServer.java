/*******************************************************************************
 * Copyright (c) 2013 Rene Schneider, GEBIT Solutions GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.gebit.integrity.bindings.swing.authorassist;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import de.gebit.integrity.bindings.swing.AbstractSwingComponentHandler;
import de.gebit.integrity.bindings.swing.authorassist.queries.AbstractSwingAuthorAssistQuery;

/**
 * The author assist server provides a kind of entrance into the running application, primarily used by the Swing
 * Fixture content assist feature in the Integrity editor. When the server is running, the content assist feature can
 * connect to it and query for information about components in the currently-running application.
 * 
 * @author Rene Schneider - initial API and implementation
 * 
 */
public class SwingAuthorAssistServer {

	/**
	 * The server thread which performs the actual work.
	 */
	protected SwingAuthorAssistServerThread serverThread;

	/**
	 * The component handler.
	 */
	protected AbstractSwingComponentHandler swingComponentHandler;

	/**
	 * The frame which owns this server.
	 */
	protected JFrame ownerFrame;

	/**
	 * The classloader to use for deserialization of author assist queries.
	 */
	protected ClassLoader classLoader;

	/**
	 * The default port to use.
	 */
	public static final int DEFAULT_PORT = 61432;

	/**
	 * The default hostname to bind to.
	 */
	public static final String DEFAULT_HOST = "127.0.0.1";

	/**
	 * Creates a new instance.
	 */
	public SwingAuthorAssistServer(AbstractSwingComponentHandler aSwingComponentHandler, JFrame anOwnerFrame,
			ClassLoader aClassLoader) {
		swingComponentHandler = aSwingComponentHandler;
		ownerFrame = anOwnerFrame;
	}

	/**
	 * Creates and returns a new {@link ServerSocket}, bound to the desired hostname and port.
	 * 
	 * @return the bound socket
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	protected ServerSocket createServerSocket() throws UnknownHostException, IOException {
		return new ServerSocket(DEFAULT_PORT, 1, InetAddress.getByName(DEFAULT_HOST));
	}

	/**
	 * Starts the server.
	 */
	public void startUp() {
		if (serverThread == null) {
			serverThread = new SwingAuthorAssistServerThread();
			serverThread.start();
		}
	}

	/**
	 * Shuts down the server.
	 */
	public void shutDown() {
		if (serverThread != null) {
			serverThread.kill();
			serverThread = null;
		}
	}

	/**
	 * The author assist server is responsible for providing an entrance into the running Swing application to the code
	 * completion functionality of the Swing bindings (base class:
	 * {@link de.gebit.integrity.bindings.swing.eclipse.AbstractSwingFixtureAssist} ).
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	protected class SwingAuthorAssistServerThread extends Thread {

		/**
		 * The server socket.
		 */
		protected ServerSocket serverSocket;

		/**
		 * Creates an instance.
		 */
		public SwingAuthorAssistServerThread() {
			super("Author Assist Server");
		}

		/**
		 * Kills the server by closing the socket and ending the thread.
		 */
		public void kill() {
			try {
				serverSocket.close();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				serverSocket = createServerSocket();

				while (!serverSocket.isClosed()) {
					Socket tempClientSocket;
					try {
						tempClientSocket = serverSocket.accept();
					} catch (SocketException exc) {
						if (!serverSocket.isClosed()) {
							exc.printStackTrace();
						}
						break;
					}

					ObjectInputStream tempObjectInputStream = null;
					try {
						tempObjectInputStream = new ClassloaderAwareObjectInputStream(
								tempClientSocket.getInputStream(), classLoader);

						Object tempQuery = tempObjectInputStream.readObject();

						if (tempQuery instanceof AbstractSwingAuthorAssistQuery) {
							Object[] tempResult = ((AbstractSwingAuthorAssistQuery) tempQuery).process(
									swingComponentHandler, ownerFrame);
							if (tempResult == null) {
								tempResult = new Object[0];
							}

							ObjectOutputStream tempObjectOutputStream = new ObjectOutputStream(
									tempClientSocket.getOutputStream());
							tempObjectOutputStream.writeObject(tempResult);
							tempObjectOutputStream.flush();
						}

						// } else if ("treeitems".equals(tempQueryType)) {
						// processTreeItemQuery(tempQueryLine, tempWriter);
						// }

					} catch (Throwable exc) {
						exc.printStackTrace();
					} finally {
						try {
							if (tempObjectInputStream != null) {
								tempObjectInputStream.close();
							}
							tempClientSocket.close();
						} catch (IOException exc) {
							// ignored on closing
						}
					}
				}
			} catch (UnknownHostException exc) {
				exc.printStackTrace();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}

	/**
	 * This object input stream allows to specify the classloader which is used to resolve the classes.
	 * 
	 * 
	 * @author Rene Schneider - initial API and implementation
	 * 
	 */
	public static class ClassloaderAwareObjectInputStream extends ObjectInputStream {

		/**
		 * The classloader to use.
		 */
		private ClassLoader classLoader;

		/**
		 * Creates an instance.
		 * 
		 * @param anInputStream
		 *            the input stream to read from
		 * @param aClassLoader
		 *            the classloader to use for resolving
		 * @throws IOException
		 */
		public ClassloaderAwareObjectInputStream(InputStream anInputStream, ClassLoader aClassLoader)
				throws IOException {
			super(anInputStream);
			classLoader = aClassLoader;
		}

		@Override
		protected Class<?> resolveClass(ObjectStreamClass aDescription) throws IOException, ClassNotFoundException {
			if (classLoader != null) {
				try {
					return classLoader.loadClass(aDescription.getName());
				} catch (ClassNotFoundException exc) {
					return super.resolveClass(aDescription);
				}
			} else {
				return super.resolveClass(aDescription);
			}
		}

	}
}
