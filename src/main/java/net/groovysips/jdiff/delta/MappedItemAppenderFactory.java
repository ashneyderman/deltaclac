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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.Collection;

/**
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.5
 */
public class MappedItemAppenderFactory implements ItemAppenderFactory
{

    private CollectionAddItemAppenderFactory regularCollectionAdd = new CollectionAddItemAppenderFactory();

    private Map<Class, ItemAppenderFactory> appendersMap = new HashMap<Class, ItemAppenderFactory>();

    public Map<Class, ItemAppenderFactory> getAppendersMap()
    {
        return appendersMap;
    }

    public void setAppendersMap( Map<Class, ItemAppenderFactory> appendersMap )
    {
        this.appendersMap = appendersMap;
    }


    public ItemAppender create( Object item )
    {
        if( item == null )
        {
            return new NullItemAppender();
        }

        Class itemClass = item.getClass();

        ItemAppenderFactory factory = appendersMap.get( itemClass );

        if( factory != null )
        {
            return factory.create( item );
        }

        // let's see if there is any superclass in that map.
        Set<Class> keys = appendersMap.keySet();
        for( Class keyClass : keys )
        {
            if( keyClass.isAssignableFrom( itemClass ) )
            {
                factory = appendersMap.get( keyClass );
                return factory.create( item );
            }
        }

        return regularCollectionAdd.create( item );
    }

    protected static final class NullItemAppender implements ItemAppender
    {
        public void append( Stack stack, Collection collection, Object item )
        {
        }
    }

}
