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

import java.util.ArrayList;
import java.util.List;
import net.groovysips.jdiff.CompositeDelta;
import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaVisitor;

/**
 * @author Alex Shneyderman
 * @since 0.3
 */
public abstract class AbstractCompositeDelta implements CompositeDelta
{

    private List<Delta> children = new ArrayList<Delta>();

    public List<Delta> children()
    {
        return children;
    }

    public void addChild( Delta child )
    {
        children.add( child );
    }

    public void visit( DeltaVisitor visitor )
    {
        visitor.visit( this );

        for( Delta child : children )
        {
            visitor.visitChild( child );
        }

        visitor.endVisit( this );
    }

    @Override public String toString()
    {
        return this.getClass().getSimpleName() + "{" +
               "children=" + ( children == null ? "null" : children.size() ) +
               '}';
    }
}
