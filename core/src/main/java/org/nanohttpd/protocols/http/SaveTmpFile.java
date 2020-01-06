package org.nanohttpd.protocols.http;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.nanohttpd.protocols.http.tempfiles.ITempFile;
import org.nanohttpd.protocols.http.tempfiles.ITempFileManager;

public class SaveTmpFile {

	private final ITempFileManager tempFileManager;
	
	public SaveTmpFile(ITempFileManager t){
		tempFileManager =t;
		
	}
	/**
     * Retrieves the content of a sent file and saves it to a temporary file.
     * The full path to the saved file is returned.
     */
    public String saveTmpFile(ByteBuffer b, int offset, int len, String filename_hint ) {
        String path = "";
        if (len > 0) {
            FileOutputStream fileOutputStream = null;
            try {
                ITempFile tempFile = tempFileManager.createTempFile(filename_hint);
                ByteBuffer src = b.duplicate();
                fileOutputStream = new FileOutputStream(tempFile.getName());
                FileChannel dest = fileOutputStream.getChannel();
                src.position(offset).limit(offset + len);
                dest.write(src.slice());
                path = tempFile.getName();
            } catch (Exception e) { // Catch exception if any
                throw new Error(e); // we won't recover, so throw an error
            } finally {
            	SafeCloser.safeClose(fileOutputStream);
            }
        }
        return path;
    }
}
