package at.gepa.net;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class LatitudeLongitude {
	
	private String address;
	private double latitude;
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	private double longitude;

	public LatitudeLongitude(String addr)
	{
		address = addr;
	}

	public void getLatLongPositions() throws Exception
	{
	    int responseCode = 0;
	    String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=true";
	    System.out.println("URL : "+api);
	    URL url = new URL(api);
	    HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
	    httpConnection.connect();
	    responseCode = httpConnection.getResponseCode();
	    if(responseCode == 200)
	    {
	    	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();;
	    	Document document = builder.parse(httpConnection.getInputStream());
	    	XPathFactory xPathfactory = XPathFactory.newInstance();
	    	XPath xpath = xPathfactory.newXPath();
	    	XPathExpression expr = xpath.compile("/GeocodeResponse/status");
	    	String status = (String)expr.evaluate(document, XPathConstants.STRING);
	    	if(status.equals("OK"))
	    	{
	    		expr = xpath.compile("//geometry/location/lat");
	    		String slatitude = (String)expr.evaluate(document, XPathConstants.STRING);
	    		expr = xpath.compile("//geometry/location/lng");
	    		String slongitude = (String)expr.evaluate(document, XPathConstants.STRING);

	    		latitude = Double.parseDouble(slatitude);
	    		longitude = Double.parseDouble(slongitude);
	      }
	      else
	      {
	    	  throw new Exception("Error from the API - response status: "+status);
	      }
	    }
	}
}
