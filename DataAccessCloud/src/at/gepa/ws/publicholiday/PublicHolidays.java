package at.gepa.ws.publicholiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import at.gepa.lib.tools.time.TimeTool;
import at.gepa.lib.tools.time.TimeUtil;
import at.gepa.net.IElement;
import at.gepa.net.IModel;
import at.gepa.net.IReadWriteHeaderListener;

@SuppressWarnings("serial")
public class PublicHolidays extends ArrayList<PublicHoliday>
implements IModel
{
	public static final String LOCK = new String("PUBLICHOLIDAY");
	
	public PublicHolidays()
	{
		synchronized (LOCK) {
			clear();
		}
	}
	
	public boolean add( String line )
	{
		boolean added = false;
		PublicHoliday h = new PublicHoliday(line, "(");
		if( h.validate() )
		{
			if( !contains(h) )
			{
				added = true;
				synchronized (LOCK) {
					super.add(h);
				}
			}
		}
		return added;
	}

	@Override
	public boolean add( PublicHoliday h )
	{
		boolean ok = false;
		if( h.validate() )
		{
			if( !contains(h) )
			{
				synchronized (LOCK) {
					ok = super.add(h);
				}
			}
		}
		return ok;
	}
	
	@Override
	public void clearModel() {
	}

	@Override
	public void checkPrevious(IElement prev, IElement bp) {
	}

	@Override
	public void add(IElement bp) {
		synchronized (LOCK) {
			super.add((PublicHoliday)bp);
		}
	}

	@Override
	public IElement createInstance(String[] split) {
		return new PublicHoliday(split);
	}

	@Override
	public void writeHeader(OutputStreamWriter writer, String header)
			throws IOException {
	}

	@Override
	public IReadWriteHeaderListener getHeaderListener() {
		return new IReadWriteHeaderListener(){

			@Override
			public void readHeader(BufferedReader reader) throws IOException {
				String line = null;
				boolean isInBody = false;
				while( (line = reader.readLine()) != null ) //Gesetzliche Feiertage XXXX in Österreich:
				{
					if( line.toUpperCase().contains("<BODY") )
						isInBody = true;
					if( isInBody )
					{
						if( line.contains("Gesetzliche Feiertage") )
							break;
					}
				}
			}

			@Override
			public void writeHeader(OutputStreamWriter writer, IModel list)
					throws IOException {
			}};
	}

	@Override
	public void done() {
		// dump();
	}

	public void dump() {
		synchronized (LOCK) {
			for( PublicHoliday ph : this )
			{
				System.out.println(ph.toString()); 
			}
		}
	}

	@Override
	public boolean contains(IElement bp) {
		synchronized (LOCK) {
			PublicHoliday p = (PublicHoliday)bp;
			for( PublicHoliday ph : this )
			{
				if( ph.equals(p) )
					return true;
			}
		}
		return false;
	}

	@Override
	public void add(int i, IElement bp) {
		synchronized (LOCK) {
			super.add( i, (PublicHoliday)bp);
		}
	}

	@Override
	public boolean checkLastModified(long lastModified) {
		return true;
	}

	@Override
	public void setLastModified(long lastModified) {
	}

	@Override
	public String setStream(InputStream input) {
		return null;
	}

	public PublicHoliday isPublicHoliday(Date time) 
	{
		//System.out.println("testing " + TimeUtil.toString(time) + " for public holiday!");
		synchronized (LOCK) {
			for( PublicHoliday ph : this )
			{
				if( ph.equals(time) )
					return ph;
			}
		}
		return null;
	}

	@Override
	public boolean isLineToProceed(String line, String fieldDelim) {
		return !line.isEmpty() && line.contains(fieldDelim);
	}

	public TableModel buildTableModel(int year) 
	{
		DefaultTableModel model = new DefaultTableModel(){
			
			public ArrayList<String[]> data = new ArrayList<String[]>();
			
			@Override
			public void addRow( Object [] o )
			{
				init();
				data.add( (String[])o );
			}
			private void init() {
				if( data == null )
					data = new ArrayList<String[]>();				
			}
			@Override
			public boolean isCellEditable( int row, int col )
			{
				return false;
			}
			
			@Override 
			public Class<?> getColumnClass( int col )
			{
				return String.class;
			}
			@Override
			public int getRowCount()
			{
				init();
				return data.size();
			}
			@Override
			public int getColumnCount()
			{
				return 2;
			}
			@Override
			public Object getValueAt( int row, int col )
			{
				init();
				return data.get(row)[col];
			}
			
		};
		model.addColumn("Feiertag");
		model.addColumn("Datum");
		synchronized (LOCK) {
			for( PublicHoliday ph : this )
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(ph.getDate());
				if( cal.get(Calendar.YEAR) == year )
				{
					String[] sa = new String[2];
					sa[0] = ph.getName();
					sa[1] = TimeTool.toDateString(ph.getDate(), "E, dd.MM.yyyy");
					
					model.addRow( (Object [])sa );
				}
			}
		}
		return model;
	}

	public int getMaxYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime( get( super.size()-1 ).getDate() );
		return cal.get(Calendar.YEAR);
	}
	public int getMinYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime( get( 0 ).getDate() );
		return cal.get(Calendar.YEAR);
	}
}
