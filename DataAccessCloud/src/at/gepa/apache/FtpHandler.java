package at.gepa.apache;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
public class FtpHandler extends URLStreamHandler {
    /**
     * Open a URLConnection on the given URL.
     */
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new FtpURLConnection(u);
    }
    /**
     * Returns a connection, which is established via the <code>proxy</code>,
     * to the FTP server specified by this <code>URL</code>. If
     * <code>proxy</code> is DIRECT type, the connection is made in normal
     * way.
     *
     * @param url
     *            the URL which the connection is pointing to
     * @param proxy
     *            the proxy which is used to make the connection
     * @return a connection to the resource pointed by this url.
     *
     * @throws IOException
     *             if this handler fails to establish a connection.
     * @throws IllegalArgumentException
     *             if any argument is null or the type of proxy is wrong.
     * @throws UnsupportedOperationException
     *             if the protocol handler doesn't support this method.
     */
    @Override
    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (url == null || proxy == null) {
            throw new IllegalArgumentException("url == null || proxy == null");
        }
        return new FtpURLConnection(url, proxy);
    }
    /**
     * Return the default port.
     */
    @Override
    protected int getDefaultPort() {
        return 21;
    }
}