package dmst.seip.angor.junitdemo;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import dmst.seip.angor.junitdemo.io.FileIOClaudeTest;
import dmst.seip.angor.junitdemo.math.MyAdvancedMathClaudeTest;
import dmst.seip.angor.junitdemo.math.MyMathClaudeTest;
import dmst.seip.angor.junitdemo.search.NameSearchClaudeTest;

/**
 * Test suite that aggregates all Claude test classes.
 * Run this single class to execute the entire test suite.
 */
@Suite
@SelectClasses({
        MyMathClaudeTest.class,
        MyAdvancedMathClaudeTest.class,
        FileIOClaudeTest.class,
        NameSearchClaudeTest.class
})
public class AllClaudeTestSuite {
    // This class is intentionally empty.
    // It serves as a holder for the above annotations.
}
