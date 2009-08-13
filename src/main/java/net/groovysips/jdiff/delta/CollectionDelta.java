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

import net.groovysips.jdiff.CompositeDelta;

/**
 * @author Alex Shneyderman
 * @since 0.3
 */
public class CollectionDelta extends AbstractCompositeDelta implements CompositeDelta
{

    private String propertyName;
    private Class collectionClass;

    public String getPropertyName()
    {
        return propertyName;
    }

    public Class getCollectionClass()
    {
        return collectionClass;
    }

    public CollectionDelta (String propertyName, Class collectionClass)
    {
        this.propertyName = propertyName;
        this.collectionClass = collectionClass;
    }

    /**
     * A collections delta might have to do the following:
     *
     *   1. Create a collection and assign it to the target field.
     *   2. 
     */

    @Override public String toString()
    {
        return "CollectionDelta{" +
               "collectionClass=" + collectionClass +
               ", propertyName='" + propertyName + '\'' +
               '}';
    }
}
