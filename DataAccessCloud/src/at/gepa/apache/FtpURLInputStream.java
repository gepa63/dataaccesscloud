package at.gepa.apache;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * This class associates a given inputStream with a control socket. This ensures
 * the control socket Object stays live while the stream is in use
 */
class FtpURLInputStream extends InputStream {
    private InputStream is; // Actual input stream
    private Socket controlSocket;
    public FtpURLInputStream(InputStream is, Socket controlSocket) {
        this.is = is;
        this.controlSocket = controlSocket;
    }
    @Override
    public int read() throws IOException {
        return is.read();
    }
    @Override
    public int read(byte[] buf, int off, int nbytes) throws IOException {
        return is.read(buf, off, nbytes);
    }
    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }
    @Override
    public synchronized void mark(int limit) {
        is.mark(limit);
    }
    @Override
    public boolean markSupported() {
        return is.markSupported();
    }
    @Override
    public void close() {
    	if( controlSocket != null )
    	{
    		try {controlSocket.shutdownInput(); } catch(Throwable t){}
    		try {controlSocket.shutdownOutput(); } catch(Throwable t){}
    		try {controlSocket.close(); } catch(Throwable t){}
    	}
    	if( is != null ) try {is.close(); } catch(Throwable t){}
    	
    }
    @Override
    public int available() throws IOException {
        return is.available();
    }
    @Override
    public long skip(long byteCount) throws IOException {
        return is.skip(byteCount);
    }
}