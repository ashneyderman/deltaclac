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

import java.io.PrintStream;
import java.util.Stack;
import net.groovysips.jdiff.CompositeDelta;
import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaVisitor;

/**
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.3
 */
public class DeltaPrinter implements DeltaVisitor
{

    private PrintStream out;

    public DeltaPrinter( PrintStream out )
    {
        this.out = out;
    }

    Stack<Delta> deltaStack = new Stack<Delta>();

    public void visit( Delta root )
    {
        indent();

        deltaStack.push( root );

        out.println( root );
    }

    public void visitChild( Delta child )
    {
        if( child instanceof CompositeDelta )
        {
            child.visit( this );
            return;
        }

        indent();

        out.println( child );
    }

    public void endVisit( CompositeDelta parent )
    {
        deltaStack.pop();
    }

    private void indent()
    {
        for( int i = 0; i < deltaStack.size(); i++ )
        {
            out.print( "\t" );
        }
    }


}
