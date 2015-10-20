import org.protelis.lang.datatype.DeviceUID;

/** Simple integer UIDs */
public class IntegerUID implements DeviceUID {
	private static final long serialVersionUID = 7168671027263227202L;
	private final int uid;
	
	IntegerUID(final int uid) { this.uid = uid; }
	
	public int getUID() { return uid; }
	
	public boolean equals(final IntegerUID alt) { return this.uid==alt.uid; }
	
	public String toString() { return Integer.toString(uid); }
}
