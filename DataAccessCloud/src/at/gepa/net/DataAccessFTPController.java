package at.gepa.net;

import java.net.MalformedURLException;
import java.net.URL;

import at.gepa.lib.tools.security.BasicUser;

public class DataAccessFTPController extends DataAccessController {
//	private static String FTP_LINK = ""; 
//	private static String FTP_SUBFOLDER = "";
//	private static String FTP_FILENAME = "bloodvalues.csv";
//	private static String FTP_USERNAME = ""; 
//	private static String FTP_PWD = ""; 
//	private static int FTP_PORT = 21;
	
//	public static String CSV_URL_FTP()
//	{
//		return "ftp://"+FTP_USERNAME+":"+FTP_PWD+"@"+FTP_LINK+FTP_SUBFOLDER+":" + FTP_PORT + "/" + FTP_FILENAME;
//	}
//	public static String CSV_URL_FTP(String fname)
//	{
//		return "ftp://"+FTP_USERNAME+":"+FTP_PWD+"@"+FTP_LINK+FTP_SUBFOLDER+":" + FTP_PORT + "/" + fname;
//	}
//	public static String CSV_URL_HTTP()
//	{
//		return CSV_URL_HTTP(FTP_FILENAME);
//	}
//	public static String CSV_URL_HTTP(String fname)
//	{
//		return "http://"+FTP_LINK+ "/" + FTP_USERNAME +"/" + fname;
//	}
//	public static String GET_FTP_LINK() {
//		return FTP_LINK;
//	}
//	public static String GET_FTP_FILENAME() {
//		return FTP_FILENAME;
//	}
//	public static String GET_FTP_PWD() {
//		return FTP_PWD;
//	}
//	public static String GET_FTP_USERNAME() {
//		return FTP_USERNAME;
//	}
//	public static int GET_FTP_PORT() {
//		return FTP_PORT;
//	}
//	public static void SET_FTP_PORT(int port) {
//		FTP_PORT = port;
//	}
//	public static void SET_FTP_FILENAME(String fname) {
//		FTP_FILENAME = fname; 
//	}
//	public static void SET_FTP_LINK(String link) {
//		FTP_LINK = link;
//	}
//	public static void SET_FTP_PWD(String pwd) {
//		FTP_PWD = pwd;
//	}
//	public static void SET_FTP_USERNAME(String user) {
//		FTP_USERNAME = user;
//	}
//	public static boolean isFTPConfigured() {
//		return !FTP_LINK.isEmpty() && !FTP_USERNAME.isEmpty();
//	}
	

	private String ftpLink;
	public String getFtpLink() {
		return ftpLink;
	}

	public void setFtpLink(String ftpLink) {
		this.ftpLink = ftpLink;
	}

	public String getSubFolder() {
		return subFolder;
	}

	public void setSubFolder(String subFolder) {
		this.subFolder = subFolder;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	private String subFolder;
	private int ftpPort;
	private BasicUser userAccess;
	
	public DataAccessFTPController(String f, String link, int port, BasicUser ua ) 
	{
		super(f);
		this.userAccess = ua;
		this.ftpLink = link;
		this.ftpPort = port;
		setSubFolder("");
	}

	public URL getURL() throws MalformedURLException 
	{
		return getURL(getFileName());
	}

	public BasicUser getUserAccess() {
		return userAccess;
	}

	@Override
	public boolean needReload(DataAccessController controller) 
	{
		if( controller instanceof DataAccessFTPController )
		{
			DataAccessFTPController x = (DataAccessFTPController)controller;
			if( ftpPort != x.ftpPort ) return true;
			if( !ftpLink.equals( x.ftpLink ) ) return true;
			if( !userAccess.isSame(x.getUserAccess()) ) return true; 
			return super.needReload(controller); 
		}
		return true; 
	}

	public URL getURL(String file) throws MalformedURLException  {
		return new URL(buildLink(file));
	}

	public URL getHttpURL() throws MalformedURLException {
		return getHttpURL( getFileName() );
	}
	public URL getHttpURL(String fname) throws MalformedURLException 
	{
		return new URL( "http://"+getFtpLink()+ "/" + getUserAccess().getUserName() +"/" + fname );
	}
	
	@Override
	public boolean validate()  throws Exception
	{
		super.validate();
		userAccess.validate();
		String msg = "";
		if( ftpPort <= 1 )
			msg = "FTP Port passt nicht: " + ftpPort;
		if( ftpLink == null || ftpLink.isEmpty() )
		{
			if( !msg.isEmpty() )
				msg += "\r\n";
			msg += "FTP Server darf nicht leer sein!";
		}
		if( !msg.isEmpty() )
			throw new Exception(msg);
		return true;
	}

	public String buildLink(String file) {
		String ret = "ftp://"+userAccess.getUserName() +":"+userAccess.getPassword()+"@"+getFtpLink()+getSubFolder()+":" + getFtpPort() + "/" + file;
		return ret;
	}
	@Override
	public String getFileName() {
		String fName = super.getFileName();
		if( !fName.isEmpty() && fName.charAt(0) == '/' )
			fName = fName.substring( fName.lastIndexOf('/')+1 );
		return fName;
	}

}
