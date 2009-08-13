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
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.5
 */
public class UpdateItemDelta extends AbstractCompositeDelta implements CompositeDelta
{

    private FinderCriteria finderCriteria;

    public FinderCriteria getFinderCriteria()
    {
        return finderCriteria;
    }

    public UpdateItemDelta( SimpleContainerDelta delegate, FinderCriteria crit )
    {
        this.finderCriteria = crit;
        addChild( delegate );
    }

    @Override public String toString()
    {
        return "UpdateItemDelta{" +
               "delegate=" + (children() != null && !children().isEmpty() ? children().get( 0 ) : null) +
               ", finderCritera=" + finderCriteria +
               '}';
    }
}
