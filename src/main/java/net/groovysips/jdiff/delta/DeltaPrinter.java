package net.groovysips.jdiff.delta;

import java.io.PrintStream;
import java.util.Stack;
import net.groovysips.jdiff.CompositeDelta;
import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaVisitor;

/**
 * TODO: provide javadoc.
 *
 * @author Shneyderman
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
