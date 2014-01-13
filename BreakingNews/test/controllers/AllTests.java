package controllers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import views.IndexTest;
import controllers.acquisition.AcquisitionTest;
import controllers.analysis.AnalysisTest;
import controllers.preparation.PreparationTest;

@RunWith(Suite.class)
@SuiteClasses({ AcquisitionTest.class, AnalysisTest.class, PreparationTest.class, IndexTest.class })
public class AllTests {

}
