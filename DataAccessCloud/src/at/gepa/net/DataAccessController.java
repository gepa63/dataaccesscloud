package at.gepa.net;

import java.io.File;
import java.io.IOException;

public class DataAccessController {
	private String fileName;
	private ContentType contentType = ContentType.Text;
	
	public DataAccessController()
	{
		setContentType(ContentType.Text);
	}
	public DataAccessController(String f)
	{
		this.fileName = f;
		setContentType(ContentType.Text);
	}

	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return getBaseFileName();
	}
	public String getBaseFileName() {
		return fileName;
	}

	public boolean hasFileName() {
		return fileName != null && !fileName.isEmpty();
	}

	public boolean needReload(DataAccessController controller) 
	{
		return !getFileName().equals(controller.getFileName());
	}

	public boolean validate() throws Exception 
	{
		return false;
	}

	public void createLocalFileIfNotExists() throws IOException, Exception {
		File f = new File(fileName);
		if( f.exists() ) return;
		if( f.createNewFile() )
			throw new Exception("Fehler beim Erstellen der Datei: " + this.fileName);
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	public ContentType getContentType() {
		return contentType;
	}
	
	private String fieldDelimiter = null;
	public void setFieldDelimiter(String string) {
		this.fieldDelimiter = string;
	}
	public String getFieldDelimiter() {
		return this.fieldDelimiter;
	}
	public boolean hasFieldDelimiter() {
		return this.fieldDelimiter != null;
	}
	private long lastModified;
	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lm) {
		lastModified = lm;
	}
	
	private String subFolder;
	public String getSubFolder() {
		if( subFolder == null )
			subFolder = "";
		return subFolder;
	}
	public void setSubFolder(String f)
	{
		subFolder = f;
	}
	
	
}
