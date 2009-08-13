/* Copyright 2009 Alex Shneyderman
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package net.groovysips.jdiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.math.BigDecimal;
import junit.framework.TestCase;
import net.groovysips.jdiff.delta.DefaultDeltaBuilder;
import net.groovysips.jdiff.delta.DeltaPrinter;
import net.groovysips.jdiff.delta.VisitingDeltaMerger;

/**
 * @author Alex Shneyderman
 * @since 0.5
 */
public class SimpleObjectDiffCollectionTests extends TestCase
{

    static
    {
        TestLog4jConfigurator.configure( );
    }

    private DefaultDeltaBuilder ddb;
    private DeltaCalculationService dcs;
    private DeltaMerger dm;

    protected void setUp()
    {
        ddb = new DefaultDeltaBuilder();

        ddb.setFinderCriteriaFactory( new SimpleFinderCriteriaFactory() );

        Map<Class, List<String>> refilledColls = new HashMap<Class, List<String>>();
        refilledColls.put( Person.class, Arrays.asList( "accounts" ));
        ddb.setRefilledCollection( refilledColls );

        dm = new VisitingDeltaMerger( new SimpleItemAppenderFactory() );

        dcs = new DefaultDeltaCalculationService( ddb, dm );
    }


    public void testAdditionOfItems()
        throws Exception
    {
        Person orig = new Person(); orig.setSsn( "123" );

        Person mod = new Person(); mod.setSsn( "123" ); mod.setAccounts( new ArrayList() );
        Account acc_0 = new Account(); acc_0.setAccountNumber( "0" ); acc_0.setCurrentBalance( new BigDecimal( "123" ) );
        mod.getAccounts().add( acc_0 );

        Account acc_1 = new Account(); acc_1.setAccountNumber( "1" );
        mod.getAccounts().add( acc_1 );

        Delta delta = dcs.diff( orig, mod );

        delta.visit( new DeltaPrinter( System.err ) );

        Person result = (Person) dcs.apply( orig, delta );

        assertNotNull( result );
        assertNotNull( result.getAccounts() );
        assertEquals( 2, result.getAccounts().size() );
    }

    public void testUpdateOfItems()
        throws Exception
    {
        Person orig = new Person(); orig.setSsn( "123" );  orig.setAccounts( new ArrayList() );
        Account orig_acc_0 = new Account(); orig_acc_0.setAccountNumber( "0" );
        orig.getAccounts().add( orig_acc_0 );

        Person mod = new Person(); mod.setSsn( "123" ); mod.setAccounts( new ArrayList() );
        Account acc_0 = new Account(); acc_0.setAccountNumber( "0" ); acc_0.setCurrentBalance( new BigDecimal( "123" ) );
        mod.getAccounts().add( acc_0 );

        Account acc_1 = new Account(); acc_1.setAccountNumber( "1" ); acc_1.setCurrentBalance( new BigDecimal( "456" ) );
        mod.getAccounts().add( acc_1 );

        Delta delta = dcs.diff( orig, mod );

        delta.visit( new DeltaPrinter( System.err ) );

        Person result = (Person) dcs.apply( orig, delta );

        assertNotNull( result );
        assertNotNull( result.getAccounts() );
        assertEquals( 2, result.getAccounts().size() );

        for( Account account : result.getAccounts() )
        {
            String accnum = account.getAccountNumber();
            if ("0".equals( accnum ))
            {
                assertEquals( new BigDecimal( "123" ), account.getCurrentBalance() );
            }
            else if ("1".equals( accnum ))
            {
                assertEquals( new BigDecimal( "456" ), account.getCurrentBalance() );
            }
            else
            {
                fail ("Account with number " + accnum + "should not exist.");
            }
        }
    }

    public void testRemoveOfItems()
        throws Exception
    {
        Person orig = new Person(); orig.setSsn( "123" );  orig.setAccounts( new ArrayList() );
        Account orig_acc_0 = new Account(); orig_acc_0.setAccountNumber( "0" );
        orig.getAccounts().add( orig_acc_0 );

        Account orig_acc_1 = new Account(); orig_acc_1.setAccountNumber( "1" );
        orig.getAccounts().add( orig_acc_1 );

        Person mod = new Person(); mod.setSsn( "123" ); mod.setAccounts( new ArrayList() );
        Account acc_0 = new Account(); acc_0.setAccountNumber( "0" ); //acc_0.setCurrentBalance( new BigDecimal( "123" ) );
        mod.getAccounts().add( acc_0 );

        Account acc_1 = new Account(); acc_1.setAccountNumber( "2" );
        mod.getAccounts().add( acc_1 );

        Delta delta = dcs.diff( orig, mod );

        delta.visit( new DeltaPrinter( System.err ) );

        Person result = (Person) dcs.apply( orig, delta );

        assertNotNull( result );
        assertNotNull( result.getAccounts() );
        assertEquals( 2, result.getAccounts().size() );

        for( Account account : result.getAccounts() )
        {
            if ("1".equals( account.getAccountNumber() ) )
            {
                fail( "Account number 1 shuld have been removed." );
            }
        }
    }

    public void testAdditionOfPrimiteves()
    {
        Account mod = new Account();

        mod.setAliases( new ArrayList<String>() );
        mod.getAliases().add("test_0");
        mod.getAliases().add("test_1");
        mod.getAliases().add("test_2");

        Delta delta = dcs.diff( null, mod );

        delta.visit( new DeltaPrinter( System.err ) );

        Account result = (Account) dcs.apply( null, delta );

        assertEquals( mod.getAliases().size(), result.getAliases().size() );
    }


}