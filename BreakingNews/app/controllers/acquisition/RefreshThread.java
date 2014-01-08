package controllers.acquisition;

/**
 * Ruft immer wieder (nach vorgegebener Zeit) die searching()-Funktion in Acquisition auf,
 * um das Einlesen neuer XML-Dateien anzusto&szlig;en.
 * @author Christian Ochsenk&uuml;hn
 *
 */
public class RefreshThread implements Runnable {
	
	private static final int MINUTES = 1;
	private boolean running = true;

	@Override
	public void run() {
		while(running) {
			try {
				Thread.sleep(MINUTES*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.print("\n *** Auto Server-Refresh ***");
			if(!Acquisition.isReading())
				Acquisition.searching();
		}
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
}
