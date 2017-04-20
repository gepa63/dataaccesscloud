package at.gepa.ws.publicholiday;

import java.util.Calendar;

public class CalcPublicHoliday {
	
	private int year;
	private Calendar osterSonntag;
	private PublicHolidays publicHolidays;
	public CalcPublicHoliday(PublicHolidays ph, int jahr)
	{
		year = jahr;
		publicHolidays = ph;
	}
	
	public void calc()
	{
		calcOstersonntag();
		
		publicHolidays.add( new PublicHoliday("Neujahr", setDate( 1, 1).getTime() ) );
		publicHolidays.add( new PublicHoliday("Heiligen Drei Könige", setDate( 1, 6).getTime() ) );
		publicHolidays.add( new PublicHoliday("Ostersonntag", osterSonntag.getTime() ) );
		publicHolidays.add( new PublicHoliday("Ostermontag", calcDate(1).getTime() ) );
		publicHolidays.add( new PublicHoliday("Staatsfeiertag", setDate( 5, 1).getTime() ) );
		publicHolidays.add( new PublicHoliday("Christi Himmelfahrt", calcDate(39).getTime() ) );
		publicHolidays.add( new PublicHoliday("Pfingstmontag", calcDate(50).getTime() ) );
		publicHolidays.add( new PublicHoliday("Fronleichnam", calcDate(60).getTime() ) );
		publicHolidays.add( new PublicHoliday("Maria Himmelfahrt", setDate( 8, 15).getTime() ) );
		publicHolidays.add( new PublicHoliday("Nationalfeiertag", setDate( 10, 26).getTime() ) );
		publicHolidays.add( new PublicHoliday("Allerheiligen", setDate( 11, 1).getTime() ) );
		publicHolidays.add( new PublicHoliday("Maria Empfängnis", setDate( 12, 8).getTime() ) );
		publicHolidays.add( new PublicHoliday("Christtag", setDate( 12, 25).getTime() ) );
		publicHolidays.add( new PublicHoliday("Stefanitag", setDate( 12, 26).getTime() ) );
		
	}

	private void calcOstersonntag() {
		float yeardMod19 = year % 19;
		float yearDiv100 = year / 100;
		float c = (8 * yearDiv100 + 13) / 25 - 2;
		float d = yearDiv100 - (year / 400) - 2;
		float e = (19 * yeardMod19 + ((15 - c + d) % 30)) % 30;
		if( e == 28 )
		{
			if( yeardMod19 > 10 )
			{
				e = 27;
			}
		}
		else if ( e == 29 )
			e = 28;
		
		float baseCalc = (d + 6 * e + 2 * (year % 4) + 4 * (year % 7) + 6) % 7 ;
		
		java.util.Calendar date = setDate( 3, 22); 
		date.add(Calendar.DAY_OF_MONTH, (int)(e+baseCalc));
		
		this.osterSonntag = date;
	}

	private Calendar setDate(int month, int day) {
		Calendar c = java.util.Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set( Calendar.HOUR_OF_DAY, 0);
		c.set( Calendar.MINUTE, 0);
		c.set( Calendar.SECOND, 0);
		return c;
	}
	private Calendar calcDate(int days) {
		Calendar d = (Calendar)osterSonntag.clone();
		d.add(Calendar.DAY_OF_MONTH, days);
		return d;
	}

}
