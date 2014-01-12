package controllers.acquisition;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.mvc.Result;

public class AcquisitionTest {

/*	@Test
	public void testStartSearch() {
		
	} */

	@Test
	public void testSearching() {
		assertTrue("Searching should not be blocked", !Acquisition.isSearching());
		Acquisition.searching();
		assertTrue("Searching should not be blocked", !Acquisition.isSearching());
	}

}
