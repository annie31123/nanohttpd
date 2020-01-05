package org.nanohttpd.protocols.http;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SafeCloser {
	
	public static final Logger LOG = Logger.getLogger(SafeCloser.class.getName());
	public static final void safeClose(Object closeable) {
        try {
            if (closeable != null) {
                if (closeable instanceof Closeable) {
                    ((Closeable) closeable).close();
                } else if (closeable instanceof Socket) {
                    ((Socket) closeable).close();
                } else if (closeable instanceof ServerSocket) {
                    ((ServerSocket) closeable).close();
                } else {
                    throw new IllegalArgumentException("Unknown object to close");
                }
            }
        } catch (IOException e) {
        	SafeCloser.LOG.log(Level.SEVERE, "Could not close", e);
        }
    }

}
