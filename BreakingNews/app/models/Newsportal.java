package models;

public class Newsportal {
	
	private String name;
	private int anzahl;
	
	public Newsportal(String name)
	{
		this.name = name;
		this.anzahl = 0;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAnzahl() {
		return anzahl;
	}
	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}
	
	public void raise() {
		this.anzahl++;
	}

}
