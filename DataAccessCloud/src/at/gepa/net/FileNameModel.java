package at.gepa.net;

import java.net.URL;

public class FileNameModel {

	private String pathToFile;
	public FileNameModel(String pathToFile) 
	{
		this.pathToFile = pathToFile;
	}
	public boolean isLocalFile() 
	{
		return FileNameModel.isLocalFile(pathToFile);
	}
	public boolean isURL() 
	{
		return FileNameModel.isURL(pathToFile);
	}
	public boolean isFTP() 
	{
		return FileNameModel.isFTP(pathToFile);
	}
	public String getPath()
	{
		return pathToFile;
	}
	
	public static boolean isLocalFile(String pathToFile) 
	{
		if( pathToFile.indexOf(':') == 1 || pathToFile.startsWith("/") )
			return true;
		return false;
	}
	public static boolean isURL(String pathToFile) 
	{
		if( pathToFile.startsWith("http:") || 
				pathToFile.startsWith("wwww"))
			return true;
		return false;
	}
	public static boolean isFTP(String pathToFile) 
	{
		if( pathToFile.startsWith("ftp:") )
			return true;
		return false;
	}
	public boolean containsSlash() {
		return FileNameModel.containsSlash(pathToFile);
	}
	public static boolean containsSlash(String path) {
		return path.contains("/");
	}
	public String makePretty() 
	{
		int pos = this.pathToFile.toLowerCase().indexOf("http:");
		if( pos < 0 )
		{
			pos = this.pathToFile.toLowerCase().indexOf("ftp:");
			if( pos < 0 ) return pathToFile;
		}
		return pathToFile.substring(pos);
	}
	
	private String hostName;
	private String username;
	private String password;
	private String filename;
	public void evalURLParts( URL url)
	{
		hostName = url.getHost();
		password = username = "";
        String parse = url.getUserInfo();
        if (parse != null) {
            int split = parse.indexOf(':');
            if (split >= 0) {
                this.username = parse.substring(0, split);
                this.password = parse.substring(split + 1);
            } else {
            	this.username = parse;
            }
        }
        filename = url.getFile();
        if( filename.startsWith("/") )
        	filename = filename.substring(1);
	}
	
	public String getFilename() {
		
		return filename;
	}
	public String getUsername() 
	{
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getHost() {
		return hostName;
	}
}

