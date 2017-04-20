package at.gepa.net;

import java.util.Locale;


public enum ContentType {
	Text,
	Html,
	Pdf,
	Word,
	Image,
	Binary;
	
	public static ContentType getType( String s )
	{
		s = s.toLowerCase(Locale.GERMAN); 
		if(s.endsWith(".txt") )
			return Text;
		else if(s.endsWith(".pdf") )
			return Pdf;
		else if(s.endsWith(".doc") )
			return Word;
		else if(s.endsWith(".jpg") )
			return Image;
		else if(s.endsWith(".png") )
			return Image;
		else if(s.endsWith(".gif") )
			return Image;
		else if(s.endsWith(".bak") )
			return Binary;
		else if(s.endsWith(".tiff") )
			return Binary;
		else if(s.endsWith(".arw") )
			return Binary;
		else if(s.endsWith(".raw") )
			return Binary;
		return Text;
	}

}
