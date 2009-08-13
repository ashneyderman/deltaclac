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

import java.util.Collection;
import java.util.Map;

/**
 * A criteria used to locate an object in a collection.
 *
 * @author Alex Shneyderman
 * @since 0.5
 */
public interface FinderCriteria
{
    /**
     *
     * @param collection
     * @return
     */
    Object find( Collection collection );

    /**
     *
     * @return
     */
    Map<String,Object> critValues();

}
