package net.groovysips.jdiff.delta;

import java.util.ArrayList;
import java.util.List;
import net.groovysips.jdiff.CompositeDelta;
import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaVisitor;

/**
 * @author Shneyderman
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
