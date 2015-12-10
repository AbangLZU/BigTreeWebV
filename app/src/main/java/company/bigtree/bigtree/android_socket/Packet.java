package company.bigtree.bigtree.android_socket;


import company.bigtree.bigtree.util.AtomicIntegerUtil;

/**
 *
 *线程安全的包封装类
 */
public class Packet {
	
	private int id= AtomicIntegerUtil.getIncrementID();
	private byte[] data;
	
	public int getId() {
		return id;
	}

	public void pack(String txt)
	{
		data=txt.getBytes();
	}
	
	public byte[] getPacket()
	{
		return data;
	}
}
