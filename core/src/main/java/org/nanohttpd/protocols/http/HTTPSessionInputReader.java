package org.nanohttpd.protocols.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.nanohttpd.protocols.http.request.Method;

public class HTTPSessionInputReader {
	private int splitbyte;

    private int rlen;

    private Map<String, List<String>> parms;

    private Map<String, String> headers;
    
    private final OutputStream outputStream;

    private final BufferedInputStream inputStream;
    private final int BUFSIZE;
	
	public HTTPSessionInputReader(BufferedInputStream i,OutputStream o, int buffSize){
		inputStream = i;
		outputStream = o;
		BUFSIZE = buffSize;
		
	}
	
	protected byte[] readInput() throws IOException {
    	byte[] buf = new byte[BUFSIZE];
        splitbyte = 0;
        rlen = 0;

        int read = -1;
        inputStream.mark(BUFSIZE);
        try {
            read = inputStream.read(buf, 0, BUFSIZE);
        } catch (SSLException e) {
            throw e;
        } catch (IOException e) {
        	SafeCloser.safeClose(inputStream);
        	SafeCloser.safeClose(outputStream);
            throw new SocketException("NanoHttpd Shutdown");
        }
        if (read == -1) {
            // socket was been closed
        	SafeCloser.safeClose(inputStream);
        	SafeCloser.safeClose(outputStream);
            throw new SocketException("NanoHttpd Shutdown");
        }
        while (read > 0) {
            rlen += read;
            splitbyte = findHeaderEnd(buf, rlen);
            if (splitbyte > 0) {
                break;
            }
            read = inputStream.read(buf, rlen, BUFSIZE - rlen);
        }

        if (splitbyte < rlen) {
            inputStream.reset();
            inputStream.skip(splitbyte);
        }

        parms = new HashMap<String, List<String>>();
        if (null == headers) {
            headers = new HashMap<String, String>();
        } else {
            headers.clear();
        }
        
        return buf;

    }
	
	public int getRlen() {
		return rlen;
	}
	
	public int getSplitbyte() {
		return splitbyte;
	}
	
	public Map<String, List<String>> getParms() {
		return parms;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	
    /**
     * Find byte index separating header from body. It must be the last byte of
     * the first two sequential new lines.
     */
    private int findHeaderEnd(final byte[] buf, int rlen) {
        int splitbyte = 0;
        while (splitbyte + 1 < rlen) {

            // RFC2616
            if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && splitbyte + 3 < rlen && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n') {
                return splitbyte + 4;
            }

            // tolerance
            if (buf[splitbyte] == '\n' && buf[splitbyte + 1] == '\n') {
                return splitbyte + 2;
            }
            splitbyte++;
        }
        return 0;
    }

}
