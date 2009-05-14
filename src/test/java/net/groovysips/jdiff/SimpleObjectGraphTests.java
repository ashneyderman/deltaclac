package net.groovysips.jdiff;

import java.util.Date;
import junit.framework.TestCase;
import net.groovysips.jdiff.delta.DefaultDeltaBuilder;
import net.groovysips.jdiff.delta.DeltaPrinter;
import net.groovysips.jdiff.delta.VisitingDeltaMerger;
import net.groovysips.jdiff.delta.NullReturnDelta;

public class SimpleObjectGraphTests extends TestCase
{

    private DeltaCalculationService dcs;

    protected void setUp()
    {
        DefaultDeltaBuilder dBuilder = new DefaultDeltaBuilder();
        DeltaMerger dMerger = new VisitingDeltaMerger();
        dcs = new DefaultDeltaCalculationService( dBuilder, dMerger );
    }

    public void testGraphDelta()
    {
        Person original = createSingleObject();
        Person modified = createSingleObject();

        modified.setName( "Viktor Orban" );
        modified.setDob( new Date( 1973, 3, 26 ) );
        modified.setSsn( "000-00-0001" );
        modified.setYearsInSchool( 21 );

        // first find the difference.
        Delta delta = dcs.diff( original, modified );

        delta.visit( new DeltaPrinter( System.err ) );

        // then apply the difference.
        Object result = dcs.apply( original, delta );

        // it should be lokking the same way.
        assertTrue( result.equals( modified ) );
    }

    public void testDeltaVisitor()
    {
        Person original = createSingleObject();
        Person modified = createSingleObject();

        modified.setName( "Viktor Orban" );
        modified.setDob( new Date( 1973, 3, 26 ) );
        modified.setSsn( "000-00-0001" );
        modified.setYearsInSchool( 21 );
        modified.setSpouse( createSpouse() );

        // first find the difference.
        Delta delta = dcs.diff( original, modified );

        delta.visit( new DeltaPrinter( System.err ) );

        // then apply the difference.
        Object result = dcs.apply( original, delta );

        // it should be lokking the same way.
        assertTrue( result.equals( modified ) );
    }

    public void testNullOriginalGraphDelta()
    {
        Person original = null;
        Person modified = createMarriedWithFirstChildObject();

        // first find the difference.
        Delta delta = dcs.diff( original, modified );

        delta.visit( new DeltaPrinter( System.err ) );

        // then apply the delta delta.
        Object result = dcs.apply( original, delta );

        System.err.println( result );

        // it should be looking the same way.
        assertTrue( result.equals( modified ) );
    }


    public void testNullOriginalGraphDelta_1()
    {
        Person original = null;
        Person modified = createMarriedObject();

        // first find the difference.
        Delta delta = dcs.diff( original, modified );

        delta.visit( new DeltaPrinter( System.err ) );

        // then apply the delta delta.
        Object result = dcs.apply( original, delta );

        System.err.println( result );

        // it should be looking the same way.
        assertTrue( result.equals( modified ) );
    }

    public void testNullReturnDelta()
    {
        Person original = createSingleObject();

        Delta delta = dcs.diff( original, null );

        assertTrue( delta instanceof NullReturnDelta );

        NullReturnDelta nrDelta = (NullReturnDelta) delta;

        assertNull( nrDelta.getPropertyName() );
        
        Object result = dcs.apply( original, nrDelta );

        assertNull( result );
    }

    public void testNullReturnValueEmbedded()
    {
        Person original = createMarriedObject();
        Person modified = createSingleObject();

        Delta delta = dcs.diff( original, modified );
        
        delta.visit( new DeltaPrinter( System.err ) );

        dcs.apply( modified, delta );

        assertNull( ((Person) modified).getSpouse() );
    }

//    public void testNullOriginalCircularGraphDelta()
//    {
//        Object original = null;
//        Object modified = createMutuallyMarriedObject();
//
//        DeltaCalculationService dcs = new DefaultDeltaCalculationService();
//
//        // first find the difference.
//        Delta deltaCmd = dcs.diff( original, modified );
//
//        // then apply the delta delta.
//        Object result = deltaCmd.apply( original );
//
//        System.err.println( result );
//
//        // it should be looking the same way.
//        assertTrue( result.equals( modified ) );
//    }

    private Person createSingleObject()
    {
        Person result = new Person();
        result.setDob( new Date() );
        result.setName( "Alex Shneyderman" );
        result.setSsn( null );
        result.setYearsInSchool( 23 );
        return result;
    }

    private Person createSpouse()
    {
        Person result = createSingleObject();
        result.setName( "Spouse" );
        result.setYearsInSchool( 52 );
        return result;
    }

    private Person createMarriedObject()
    {
        Person result = createSingleObject();
        Person spouse = createSpouse();
        result.setSpouse( spouse );
        return result;
    }

    private Person createMutuallyMarriedObject()
    {
        Person result = createSingleObject();
        Person spouse = createSpouse();
        result.setSpouse( spouse );
        spouse.setSpouse( result );
        return result;
    }

    private Person createMarriedWithFirstChildObject()
    {
        Person result = createSingleObject();
        Person spouse = createSpouse();
        Person firstChild = createSingleObject();
        result.setSpouse( spouse );
        firstChild.setName( "First-born" );

        result.setFirstChild( firstChild );
        spouse.setFirstChild( firstChild );

        return result;
    }

    private Person createWithKids()
    {
        Person result = new Person();
        result.setDob( new Date() );
        result.setName( "Alex Shneyderman" );
        result.setSsn( null );
        result.setYearsInSchool( 23 );
        return result;
    }

}
