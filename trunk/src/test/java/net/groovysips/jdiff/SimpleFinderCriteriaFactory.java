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

import net.groovysips.jdiff.delta.FinderCriteriaFactory;
import net.groovysips.jdiff.delta.FinderCriteria;
import net.groovysips.jdiff.delta.NullReturningCriteria;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.5
 */
public class SimpleFinderCriteriaFactory implements FinderCriteriaFactory
{

    private Map<Class,FinderCriteriaFactory> findersMap;

    public SimpleFinderCriteriaFactory ()
    {
        findersMap = new HashMap<Class,FinderCriteriaFactory>();
        findersMap.put( Person.class, new PersonFinderCriteriaFactory() );
        findersMap.put( Account.class, new AccountFinderCriteriaFactory() );
    }

    public FinderCriteria create( Object item )
    {
        FinderCriteria result = null;
        FinderCriteriaFactory factory = findersMap.get( item.getClass() );

        if (factory == null)
        {
            result = new NullReturningCriteria();
        }
        else
        {
            result = factory.create( item );
        }

        return result;
    }

    private static final class PersonFinderCriteriaFactory implements FinderCriteriaFactory {
        public FinderCriteria create( final Object item )
        {
            return new FinderCriteria() {
                private String ssn = ((Person) item).getSsn();
                public Object find( Collection collection )
                {
                    for( Object p : collection )
                    {
                        if (p instanceof Person && ((Person) p).getSsn().equals( ssn ))
                        {
                            return p;
                        }
                    }
                    return null;
                }

                public Map<String, Object> critValues()
                {
                    Map<String,Object> result = new HashMap<String,Object>();
                    result.put( "ssn", ssn );
                    return result;
                }


                @Override public String toString()
                {
                    return "PersonFinderCriteria{" +
                           "ssn='" + ssn + '\'' +
                           '}';
                }
            };
        }
    }

    private static final class AccountFinderCriteriaFactory implements FinderCriteriaFactory {
        public FinderCriteria create( final Object item )
        {
            return new FinderCriteria() {
                private String accountNumber = ((Account) item).getAccountNumber();
                public Object find( Collection collection )
                {
                    for( Object a : collection )
                    {
                        if (a instanceof Account && ((Account) a).getAccountNumber().equals( accountNumber ))
                        {
                            return a;
                        }
                    }
                    return null;
                }

                public Map<String, Object> critValues()
                {
                    Map<String,Object> result = new HashMap<String,Object>();
                    result.put( "accountNumber", accountNumber );
                    return result;
                }

                public String toString()
                {
                    return "AccountFinderCriteria{" +
                           "accountNumber='" + accountNumber + '\'' +
                           '}';
                }
            };
        }
    }


}
