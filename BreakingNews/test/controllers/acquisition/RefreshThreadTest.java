package controllers.acquisition;

import static org.junit.Assert.*;

import org.junit.Test;

public class RefreshThreadTest {

	@Test
	public void testRun() {
		Thread refreshThread = new Thread( new RefreshThread() );
		refreshThread.start();
		
		try {
			Thread.sleep(59999);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("Acquisition should be searching", Acquisition.isSearching());	
	}

}
