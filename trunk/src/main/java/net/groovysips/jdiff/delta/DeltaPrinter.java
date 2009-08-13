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
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;
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

    private Writer writer;

    public DeltaPrinter( PrintStream out )
    {
        this.writer = new PrintWriter( out );
    }

    public DeltaPrinter( Writer writer )
    {
        this.writer = writer;
    }

    private Stack<Delta> deltaStack = new Stack<Delta>();

    public void visit( Delta root )
    {
        try
        {
        indent();

        deltaStack.push( root );

            writer.write( root.toString() );
            writer.write( "\n" );
            writer.flush();
    }
        catch( IOException e )
        {
            System.err.println( "Unable to write to the writer." );
        }
    }

    public void visitChild( Delta child )
    {
        if( child instanceof CompositeDelta )
        {
            child.visit( this );
            return;
        }

        try
        {
        indent();

            writer.write( child.toString() );
            writer.write( "\n" );
            writer.flush();
    }
        catch( IOException e )
        {
            System.err.println( "Unable to write to the writer." );
        }
    }

    public void endVisit( CompositeDelta parent )
    {
        deltaStack.pop();
    }

    private void indent()
        throws IOException
    {
        for( int i = 0; i < deltaStack.size(); i++ )
        {
            writer.write( "\t" );
        }
        writer.flush();
    }


}
