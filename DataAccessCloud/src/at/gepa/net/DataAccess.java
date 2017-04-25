package at.gepa.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.List;

import at.gepa.net.DataAccessCloudControllerDropbox.IDropboxSessionHandler;


public class DataAccess {

	public static enum eAccessType
	{
		LocalFile,
		FTPAccess,
		HttpAccess,
		Dropbox,
		S3CloudeAccessAmazonGoogle,
		Unknown
	}

	private eAccessType accessType;
	private DataAccessController controller;
	
	public static DataAccess createInstance(String fname, String user, String pwd, String ftplink, String subpath, int ftpPort, String accessKey, String secretKey, String cloudBucket )
	{
		DataAccess.eAccessType type = eAccessType.LocalFile;
		DataAccessController dac = null;
		
		if( ftplink != null && !ftplink.isEmpty() && user != null && !user.isEmpty() && pwd != null )
		{
			type = eAccessType.FTPAccess;
			try{
				at.gepa.lib.tools.security.BasicUser buser = at.gepa.lib.tools.security.BasicUser.createInstance(user, pwd);
				dac = new DataAccessFTPController(fname, ftplink, ftpPort, buser );
				dac.setSubFolder(subpath);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else if( accessKey != null && !accessKey.isEmpty() && secretKey != null && !secretKey.isEmpty() && (cloudBucket == null || cloudBucket.isEmpty()) )
		{
			type = eAccessType.Dropbox;
			dac = new DataAccessCloudControllerDropbox(fname, accessKey, secretKey );
			dac.setSubFolder(subpath);
		}
		else
		{
			type = eAccessType.LocalFile;
			dac = new DataAccessController(fname);
		}
		DataAccess da = new DataAccess(dac, type);
		return da;
	}
	public DataAccess(DataAccessController controller, DataAccess.eAccessType type)
	{
		this.accessType = type;
		this.controller = controller;
	}
	
	public String saveFile( IModel list, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory)
	{
		switch( accessType )
		{
		case LocalFile:
			return DataAccessLocal.saveLocalFile(controller, list, uploadTask, headerFactory);
		case FTPAccess:
			return DataAccessFTP.uploadFileFTP( (DataAccessFTPController)controller, list, uploadTask, headerFactory);
		case S3CloudeAccessAmazonGoogle:
			return "Not long supported";// DataAccessCloudAmazonGoogle.uploadFile( (DataAccessCloudController)controller, list, uploadTask, headerFactory);
		case Dropbox:
			return DataAccessDropbox.uploadFile( getDropboxController(), list, uploadTask, headerFactory);
		case HttpAccess:
			return DataAccessHTTP.uploadHttpFile( getHttpController(), list, uploadTask, headerFactory);
		default:
			break;
		}
		return null;
	}
	private DataAccessHTTPController getHttpController() {
		return (DataAccessHTTPController)this.controller;
	}
	private DataAccessCloudControllerDropbox getDropboxController() {
		return (DataAccessCloudControllerDropbox)controller;
	}
	public String loadFile(IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener)
	{
		switch( accessType )
		{
		case LocalFile:
			return DataAccessLocal.loadFromLocal(controller, downloadTask, list, readHeaderListener);
		case FTPAccess:
			return DataAccessFTP.downloadFile( getFtpController(), downloadTask, list, readHeaderListener);
		case S3CloudeAccessAmazonGoogle:
			return "Not long supported";// DataAccessCloudAmazonGoogle.downloadFile( getCloudController(), downloadTask, list, readHeaderListener);
		case HttpAccess: 
			return DataAccessHTTP.downloadFile(getHttpController(), downloadTask, list);
		case Dropbox:
			return DataAccessDropbox.downloadFile( getDropboxController(), downloadTask, list, readHeaderListener);
		default:
			break;
		}
		return null;
	}
	private DataAccessCloudController getCloudController() {
		return (DataAccessCloudController)controller;
	}
	private DataAccessFTPController getFtpController() {
		return (DataAccessFTPController)controller;
	}
	public String getFtpLink() 
	{
		switch( accessType )
		{
		case FTPAccess:
			return getFtpController().getFtpLink();
		case LocalFile:
		case S3CloudeAccessAmazonGoogle:
		case Dropbox:
		default:
			break;
		}
		return "";
	}
	public String getSubFolder() 
	{
		return controller.getSubFolder();
	}
	
	public String getFileName() {
		return controller.getFileName();
	}
	public String getBaseFileName() {
		return controller.getBaseFileName();
	}
	
	public String getPassword() {
		switch( accessType )
		{
		case FTPAccess:
			return getFtpController().getUserAccess().getPassword();
		case S3CloudeAccessAmazonGoogle:
		case LocalFile:
		case Dropbox:
		default:
			break;
		}
		return "";
	}
	public String getUserName() {
		switch( accessType )
		{
		case FTPAccess:
			return getFtpController().getUserAccess().getUserName();
		case S3CloudeAccessAmazonGoogle:
		case LocalFile:
		default:
			break;
		}
		return "";
	}
	public int getFtpPort() 
	{
		switch( accessType )
		{
		case FTPAccess:
			return getFtpController().getFtpPort();
		case S3CloudeAccessAmazonGoogle:
		case LocalFile:
		case Dropbox:
		default:
			break;
		}
		return 0;
	}
	public void setFtpPort(int port) {
		switch( accessType )
		{
		case FTPAccess:
			getFtpController().setFtpPort(port);
			break;
		case S3CloudeAccessAmazonGoogle:
		case LocalFile:
		case Dropbox:
		default:
			break;
		}
	}
	public void setFileName(String string) {
		controller.setFileName(string);
	}
	public void setFtpLink(String link) {
		switch( accessType )
		{
		case FTPAccess:
			getFtpController().setFtpLink(link);
			break;
		case S3CloudeAccessAmazonGoogle:
		case LocalFile:
		case Dropbox:
		default:
			break;
		}
	}
	public boolean isConfigured() {
		return this.accessType != eAccessType.Unknown && controller.hasFileName();
	}
	public String getCloudKey() {
		switch( accessType )
		{
		case S3CloudeAccessAmazonGoogle:
		case Dropbox:
			return getCloudController().getAccessKey();
		case FTPAccess:
		case LocalFile:
		default:
			break;
		}
		return "";
	}
	public String getCloudSecKey() {
		switch( accessType )
		{
		case S3CloudeAccessAmazonGoogle:
		case Dropbox:
			return getCloudController().getSecretKey();
		case FTPAccess:
		case LocalFile:
		default:
			break;
		}
		return "";
	}
	public String getCloudBucket() {
		switch( accessType )
		{
		case S3CloudeAccessAmazonGoogle:
			return getCloudController().getCloudBucket();
		case FTPAccess:
		case LocalFile:
		case Dropbox:
		default:
			break;
		}
		return "";
	}
	public boolean needReload(DataAccess da) 
	{
		if( accessType != da.accessType )
			return true;
		return controller.needReload(da.controller);
	}
	public void writeObjectToXML(String xmlFile, Calendar minDate) throws IOException {
		switch( accessType )
		{
		case FTPAccess:
			DataAccessFTP.writeObjectToXML( getFtpController(), xmlFile, minDate);
			break;
		case S3CloudeAccessAmazonGoogle:
		case LocalFile:
		case Dropbox:
		default:
			break;
		}
	}
	public Object readObjectFromXML(DataAccess da, String xmlFile) throws IOException {
		switch( accessType )
		{
		case FTPAccess:
			return DataAccessFTP.readObjectFromXML( getFtpController(), xmlFile);
		case S3CloudeAccessAmazonGoogle:
		case LocalFile:
		case Dropbox:
		default:
			break;
		}
		return null;
	}
	public void validate() throws Exception 
	{
		switch( accessType )
		{
		case FTPAccess:
			getFtpController().validate();
			break;
		case S3CloudeAccessAmazonGoogle:
		case Dropbox:
			getCloudController().validate();
		case LocalFile:
		default:
			controller.validate();
			break;
		}
	}
	public void setHandler( IDropboxSessionHandler h )
	{
		switch( accessType )
		{
		case Dropbox:
			getDropboxController().setSessionHandler(h);
			break;
		case FTPAccess:
		case S3CloudeAccessAmazonGoogle:
		case LocalFile:
		default:
			break;
		}
	}
	public static DataAccess createInstance(String filename) {
		return createInstance(filename, "", "", "", "", 0, "", "", "");
	}
	public static DataAccess createInstance(String filename, String username, String pwd, String ftpLink, String subpath, int port) {
		return createInstance(filename, username, pwd, ftpLink, subpath, port, "", "", "" );
	}
	public boolean isFTPActive() 
	{
		return ( accessType == eAccessType.FTPAccess );
	}
	public boolean isLocalFileActive() {
		return ( accessType == eAccessType.LocalFile );
	}
	
	public static int calcFileLength(URLConnection con, InputStream [] input) 
	{
		int fileLength = 0;
		if( input[0] != null )
		{
	        try {
	        	fileLength = con.getContentLength();
	        } catch (Throwable e) {}
	        try {
				if( fileLength == 0 )
				{
					List<String> values = con.getHeaderFields().get("content-Length");
					if (values != null && !values.isEmpty()) 
					{
						String sLength = (String) values.get(0);
						if (sLength != null && !sLength.isEmpty()) {
							fileLength = Integer.parseInt(sLength);
						}
					}
				}
	        } catch (Throwable e) {}
	        if( fileLength == 0 )
	        {
	        	int c = 0;
	        	try {
					while( (c=input[0].read()) >= 0 )
					{
						fileLength++;
					}
					input[0].close();
					input[0] = con.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
		return fileLength;
	}
	public static DataAccess createInstance(FileNameModel fnm) 
	{
		if( fnm.isFTP() )
		{
			URL url;
			try {
				url = new URL(fnm.getPath());
				fnm.evalURLParts(url);
				String fn = fnm.getFilename();
				return DataAccess.createInstance(fn, fnm.getUsername(), fnm.getPassword(), fnm.getHost(), fnm.getSubFolder(), url.getPort());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		if( fnm.isURL() )
		{
			DataAccessHTTPController dac = new DataAccessHTTPController(fnm.getPath());
			DataAccess da = new DataAccess(dac, eAccessType.HttpAccess);
			return da;
		}
		else if( fnm.isLocalFile() )
		{
			DataAccessController dac = new DataAccessController(fnm.getPath());
			DataAccess dal = new DataAccess(dac, eAccessType.LocalFile); 
			return dal;
		}
		return null;
	}
	public String getFtpFileName() {
		switch( accessType )
		{
		case FTPAccess:
			return getFtpController().getFileName();
		case LocalFile:
		case S3CloudeAccessAmazonGoogle:
		case Dropbox:
		default:
			break;
		}
		return controller.getFileName();
	}
	public void createIfNotExistsFile() throws IOException, Exception {
		switch( accessType )
		{
		case LocalFile:
			controller.createLocalFileIfNotExists();
			break;
		case FTPAccess:
		case S3CloudeAccessAmazonGoogle:
		case Dropbox:
		default:
			break;
		}
		
	}
	public String buildLink(String file) 
	{
		if( file.contains("/") )
		{
			file = file.substring(file.lastIndexOf('/')+1);
		}
		switch( accessType )
		{
		case FTPAccess:
			file = ((DataAccessFTPController)controller).buildLink(file);
			break;
		case HttpAccess:
			file = getHttpController().buildLink(file);
			break;
		case LocalFile:
		case S3CloudeAccessAmazonGoogle:
		case Dropbox:
		default:
			break;
		}
		return file;
	}
	public void setContentType(ContentType contentType) {
		if( this.controller != null )
		{
			controller.setContentType( contentType );
		}
	}
	public void copyFrom(File fromFile) throws Exception 
	{
		Exception e2Throw = null;
		int lines = 0; 
		controller.setLastModified(0L);
		switch( accessType )
		{
		case FTPAccess:
			DataAccessFTP.backupFileIfExists( (DataAccessFTPController)controller, ".backup" );
			lines = DataAccessFTP.copy( fromFile, (DataAccessFTPController)controller );
			e2Throw = new Exception( lines + " Cachezeilen erfolgreich nach '"+controller.getFileName()+"' hochgeladen" );
			break;
		case LocalFile:
			String currentFile = getFileName();
			
			java.io.File toFile = new java.io.File( currentFile );
			if( toFile.exists() )
			{
				java.io.File newPath = new java.io.File(DataAccessLocal.buildUniqueFilename( currentFile, ".backup"));
				toFile.renameTo(newPath);
				toFile = new java.io.File( currentFile );
			}
			try {
				lines = FileStreamAccess.copy( fromFile, toFile, true );
				e2Throw = new Exception( lines + " Cachezeilen erfolgreich nach '"+currentFile+"' kopiert" );
				
				controller.setLastModified(Calendar.getInstance().getTimeInMillis());
			} catch (Exception e) {
				e2Throw = e;
			}
			break;
		case HttpAccess:
		case S3CloudeAccessAmazonGoogle:
		case Dropbox:
		default:
			break;
		}

		if( e2Throw != null )
			throw e2Throw;
	}
	public long getLastModified() {
		return controller.getLastModified();
	}
}
