package net.groovysips.jdiff;

/**
 * TODO: provide javadoc.
 *
 * @author Shneyderman
 */
public interface DeltaVisitor
{

    void visit( Delta root );

    void visitChild( Delta child );

    void endVisit( CompositeDelta parent );

}
