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

import java.beans.PropertyDescriptor;

/**
 * Delta builder that buildes delta by calculating the diffs of the values of the java bean's properties.
 *
 * @author Alex Shneyderman
 * @since 0.3
 */
public interface DeltaBuilder
{

    public static final PropertyDescriptor[] EMPTY_PROPERTY_DESCRIPTORS_ARRAY = new PropertyDescriptor[0];

    /**
     * Calculates delta between two java beans.
     *
     * @param original java bean
     * @param modified java bean
     * @return delta
     */
    Delta build( Object original, Object modified );

    /**
     * Calculates delta between two java beans that can trigger an assignment of the result to a property described by
     * propertyDescriptor argument.
     *
     * @param original
     * @param modified
     * @param propertyDescriptor
     * @return delta
     */
    Delta build( Object original, Object modified, PropertyDescriptor propertyDescriptor );

}
