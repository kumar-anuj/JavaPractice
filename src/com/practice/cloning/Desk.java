package com.anuj.cloning;

public class Desk {
	
	private int seatNo;
	private long deskPhone;
	
	public Desk(int seatNo, long deskPhone) {
		this.seatNo = seatNo;
		this.deskPhone = deskPhone;
	}
	
	public Desk(Desk desk) {
		this.seatNo = desk.getSeatNo();
		this.deskPhone = desk.getDeskPhone();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Desk) { 
			Desk d1 =(Desk)obj;
			return d1.getSeatNo() == this.getSeatNo() ? d1.getDeskPhone() == this.getDeskPhone(): false;
		}
		return false;
	}
	
	public int getSeatNo() {
		return seatNo;
	}
	public void setSeatNo(int seatNo) {
		this.seatNo = seatNo;
	}
	public long getDeskPhone() {
		return deskPhone;
	}
	public void setDeskPhone(long deskPhone) {
		this.deskPhone = deskPhone;
	}
	
}
