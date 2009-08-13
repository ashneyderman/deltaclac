package net.groovysips.jdiff;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * TODO: provide javadoc.
 *
 * @author Shneyderman
 */
public class AllTests extends TestSuite
{

    public static Test suite()
    {
        TestSuite test = new TestSuite();

        test.addTestSuite( SimpleObjectDiffCollectionTests.class );
        test.addTestSuite( SimpleObjectDiffTests.class );
        test.addTestSuite( SimpleObjectGraphTestCase.class );

        return test;
    }

}
