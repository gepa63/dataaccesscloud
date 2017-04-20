package at.gepa.net;



public interface IElement
extends IWriteable
{
	public boolean isChanged();
	public Object get(Object key);
	public void setChanged(boolean b);
	public Object put(String key, Object o);
	public String getTitle(int page);
	public Class<?> getType(int page);
	public int getLines(int page);
	public String getPrefix(int page);
}
