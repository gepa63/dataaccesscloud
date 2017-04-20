package at.gepa.net;


public class DataAccessCloudController extends DataAccessController {
	
	

	private String accessKey;
	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getCloudBucket() {
		return cloudBucket;
	}

	public void setCloudBucket(String cloudBucket) {
		this.cloudBucket = cloudBucket;
	}

	private String secretKey;
	private String cloudBucket;

	public DataAccessCloudController(String fname, String accessKey, String secretKey, String cloudBucket) 
	{
		super(fname);
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.cloudBucket = cloudBucket;
	}

	@Override
	public boolean needReload(DataAccessController controller) 
	{
		if( controller instanceof DataAccessCloudController )
		{
			DataAccessCloudController x = (DataAccessCloudController)controller;
			if( !accessKey.equals( x.accessKey ) ) return true;
			if( !secretKey.equals( x.secretKey ) ) return true;
			if( !cloudBucket.equals( x.cloudBucket ) ) return true;
			return super.needReload(controller); 
		}
		return true; 
	}

	@Override
	public boolean validate()  throws Exception
	{
		String msg = "";
		
		if( accessKey == null || accessKey.isEmpty()  ) //amazon s3 acccess
			msg = "Access Schlüssel darf nicht leer sein!" + accessKey;
		if( secretKey == null || secretKey.isEmpty() )
		{
			if( !msg.isEmpty() )
				msg += "\r\n";
			msg += "Sicherheitsschlüssel darf nicht leer sein!";
		}
		if( cloudBucket == null || cloudBucket.isEmpty() )
		{
			if( !msg.isEmpty() )
			{
				msg += "\r\n";
				msg += "Bucket darf nicht leer sein!";
			}
		}
		else
			msg = ""; //google Version
		if( !msg.isEmpty() )
			throw new Exception(msg);
		
		return false;
	}
}
