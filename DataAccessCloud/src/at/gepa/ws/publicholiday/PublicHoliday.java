package at.gepa.ws.publicholiday;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.Calendar;

import at.gepa.lib.tools.time.TimeTool;
import at.gepa.lib.tools.time.TimeUtil;
import at.gepa.net.IElement;

@SuppressWarnings("serial")
public class PublicHoliday
implements IElement
{
	public static final float WORKING_DAY_DEFAULT= 8f;
	public static final float WORKING_DAY_FRIDAY = 6.5f;
	public static final float NONE_WORKINGDAY = 0f;
	
	public static final String DATE_FORMAT = "dd.MM.yyyy"; 
	
	private String name;
	private java.util.Date date;
	
	public PublicHoliday(String line, String delim)
	{
		setName(null);
		setDate((java.util.Date)null);
		int pos = line.indexOf(delim);
		if( pos > 0)
		{
			setName( line.substring(0, pos-1).trim() );
			String sd = line.substring(pos+5, line.length()-2);
			setDate( sd );
		}
	}
	
	public boolean validate()
	{
		return ( hasName() && hasDate() );
	}
	
	private boolean hasDate() {
		return date != null;
	}

	private boolean hasName() {
		return name != null && !name.isEmpty();
	}

	public PublicHoliday(String name, java.util.Date d)
	{
		this.name = name;
		date = d;
	}
	
	public PublicHoliday(String[] split) {
		setName(split[0].trim());
		String sd = split[1].substring(4, split[1].indexOf(')') );
		setDate( sd );
	}

	private static final String DELIM = ";";
	public PublicHoliday(String string) {
		String sa[] = string.split(DELIM);
		setName( sa[0] );
		setDate( sa[1] );
	}

	public String toString()
	{
		return TimeTool.toDateString(date) + " - " + name;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName( String n)
	{
		name = n;
	}
	
	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}
	public void setDate(Calendar cal) {
		this.date = cal.getTime();
	}
	public void setDate(String s) {
		this.date = TimeTool.toDate(s, DATE_FORMAT);
	}

	@Override
	public boolean equals( Object o )
	{
		boolean ret = false;
		if( !hasDate() ) return ret;
		if( o instanceof PublicHoliday )
		{
			PublicHoliday h = (PublicHoliday)o;
			if( h.hasDate() )
			{
				ret = TimeUtil.equals(h.getDate(), getDate()); 
			}
		}
		else if( o instanceof java.util.Date )
		{
			java.util.Date x = (java.util.Date)o;
			ret = TimeUtil.equals(x, getDate());
		}
		return ret;
	}

	@Override
	public void write(OutputStreamWriter writer, String delimField)
			throws IOException {
	}

	@Override
	public boolean isChanged() {
		return false;
	}

	@Override
	public Object get(Object key) 
	{
		return getDate();
	}

	@Override
	public void setChanged(boolean b) {
	}

	@Override
	public Object put(String key, Object o) {
		if( key.equalsIgnoreCase("Date") )
			setDate( (java.util.Date)o );
		else if( key.equalsIgnoreCase("name") )
			setName( (String)o );
		return this;
	}

	@Override
	public String getTitle(int page) {
		return toString();
	}

	@Override
	public Class<?> getType(int page) {
		if( page == 0 )
			return String.class;
		return Date.class;
	}

	@Override
	public int getLines(int page) {
		return 1;
	}

	@Override
	public String getPrefix(int page) {
		if( page == 0 )
			return "Name";
		return "Datum";
	}

	public float getHours() {
		if( TimeUtil.isWeekend(date) )
			return 0;
		if( TimeUtil.isFriday(this.date) )
			return 6.5f;
		return 8;
	}

	public String toStream() {
		String ret = getName() + DELIM;
		ret += TimeTool.toString( this.date, DATE_FORMAT) + DELIM;
		
		return ret;
	}
}
