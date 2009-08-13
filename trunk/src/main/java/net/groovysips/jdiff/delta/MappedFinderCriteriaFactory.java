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
package net.groovysips.jdiff.delta;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.5
 */
public class MappedFinderCriteriaFactory implements FinderCriteriaFactory
{

    private Map<Class, FinderCriteriaFactory> findersMap = new HashMap<Class, FinderCriteriaFactory>();

    public Map<Class, FinderCriteriaFactory> getFindersMap()
    {
        return findersMap;
    }

    public void setFindersMap( Map<Class, FinderCriteriaFactory> findersMap )
    {
        this.findersMap = findersMap;
    }

    public FinderCriteria create( Object item )
    {
        if( item == null )
        {
            return new NullReturningCriteria();
        }

        Class itemClass = item.getClass();

        FinderCriteriaFactory factory = findersMap.get( itemClass );

        if( factory != null )
        {
            return factory.create( item );
        }

        // let's see if there is any superclass in that map.
        Set<Class> keys = findersMap.keySet();
        for( Class keyClass : keys )
        {
            if( keyClass.isAssignableFrom( itemClass ) )
            {
                factory = findersMap.get( keyClass );
                return factory.create( item );
            }
        }

        return new NullReturningCriteria();
    }

}
