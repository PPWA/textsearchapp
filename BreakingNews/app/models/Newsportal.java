package models;

/**
 * Diese Klasse modelliert ein Nachrichtenportal.
 * 
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Newsportal {

	/**
	 * Bezeichnung des Newsportals
	 */
	private String name;
	/**
	 * Anzahl von publizierten Artikeln mit neuen Themen
	 */
	private int anzahl;

	/**
	 * Erzeugt ein Objekt vom Typ Newsportal mit dem entsprechenden Namen und
	 * setzt Anzahl standardm&auml;ßig auf 0.
	 * 
	 * @param name
	 *            Bezeichnung des Newsportals
	 */
	public Newsportal(String name) {
		this.name = name;
		this.anzahl = 0;
	}

	/**
	 * Ermittelt die Bezeichnung des Newsportals.
	 * 
	 * @return Die Bezeichnung des Newsportals
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt die Bezeichnung des Newsportals
	 * 
	 * @param name
	 *            Die Bezeichnung des Newsportala
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Ermittelt die Anzahl publizierter Artikel mit neuen Themen.
	 * 
	 * @return Die Anzahl publizierter Artikel mit neuen Themen
	 */
	public int getAnzahl() {
		return anzahl;
	}

	/**
	 * Setzt die Anzahl von publizierten Artikeln mit neuen Themen.
	 * 
	 * @param anzahl
	 *            Die Anzahl von publizierten Artikeln mit neuen Themen.
	 */
	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	/**
	 * Erh&ouml;ht die Anzahl von publizierten Artikeln mit neuen Themen um Eins.
	 */
	public void raise() {
		this.anzahl++;
	}

}
