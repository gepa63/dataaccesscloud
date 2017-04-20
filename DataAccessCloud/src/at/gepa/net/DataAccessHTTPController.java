package at.gepa.net;

import java.net.MalformedURLException;
import java.net.URL;

public class DataAccessHTTPController extends DataAccessController {
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
	

	private String httpLink;
	public String getHttpLink() {
		return httpLink;
	}

	public void setHttpLink(String urlLink) {
		this.httpLink = urlLink;
	}

	public String getSubFolder() {
		return subFolder;
	}

	public void setSubFolder(String subFolder) {
		this.subFolder = subFolder;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int ftpPort) {
		this.httpPort = ftpPort;
	}

	private String subFolder;
	private int httpPort = 80;
	
	public DataAccessHTTPController(String f ) 
	{
		this( DataAccessHTTPController.extractFileName(f), DataAccessHTTPController.extractLink(f), 80);
	}
	private static String extractLink(String f) 
	{
		if( f.toLowerCase().endsWith(".csv") )
		{
			int pos = f.lastIndexOf('/');
			if( pos >= 0 )
				f = f.substring(0, pos );
		}
		return f;
	}

	private static String extractFileName(String f) 
	{
		if( f.toLowerCase().endsWith(".csv") )
		{
			int pos = f.lastIndexOf('/');
			if( pos >= 0 )
				f = f.substring(pos, f.length() );
		}
		return f;
	}

	public DataAccessHTTPController(String f, String link ) 
	{
		this( f, link, 80);
	}
	public DataAccessHTTPController(String f, String link, int port ) 
	{
		super(f);
		this.httpLink = link;
		this.httpPort = port;
		setSubFolder("");
	}

	public URL getURL() throws MalformedURLException 
	{
		return getHttpURL(getFileName());
	}

	@Override
	public boolean needReload(DataAccessController controller) 
	{
		if( controller instanceof DataAccessHTTPController )
		{
			DataAccessHTTPController x = (DataAccessHTTPController)controller;
			if( httpPort != x.httpPort ) return true;
			if( !httpLink.equals( x.httpLink ) ) return true;
			return super.needReload(controller); 
		}
		return true; 
	}

	public URL getHttpURL() throws MalformedURLException {
		return getHttpURL( getFileName() );
	}
	public URL getHttpURL(String fname) throws MalformedURLException 
	{
		if( fname.startsWith("http:") )
			return new URL( fname );
		if( getHttpLink().isEmpty() )
		{
			return new URL( "http://" + fname );
		}
		return new URL( "http://"+getHttpLink()+ 
				(getHttpLink().endsWith("/") ? "" : "/") + fname );
	}
	
	@Override
	public boolean validate()  throws Exception
	{
		String msg = "";
		if( httpPort <= 1 )
			msg = "FTP Port passt nicht: " + httpPort;
		if( httpLink == null || httpLink.isEmpty() )
		{
			if( !msg.isEmpty() )
				msg += "\r\n";
			msg += "FTP Server darf nicht leer sein!";
		}
		if( !msg.isEmpty() )
			throw new Exception(msg);
		return true;
	}

	public String buildLink(String fname) {
		return "http://"+getHttpLink()+ (getHttpLink().endsWith("/") ? "" : "/") + fname;
		
	}
	
}
