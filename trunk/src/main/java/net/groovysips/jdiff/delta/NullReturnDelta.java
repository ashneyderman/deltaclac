package net.groovysips.jdiff.delta;

import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaVisitor;

/**
 * TODO: provide javadoc.
 *
 * @author Shneyderman
 */
public class NullReturnDelta implements Delta
{

    private String propertyName;
    private String writeMethodName;

    public String getPropertyName()
    {
        return propertyName;
    }

    public String getWriteMethodName()
    {
        return writeMethodName;
    }

    public NullReturnDelta()
    {
        this( null, null );
    }

    public NullReturnDelta( String propertyName, String writeMethodName )
    {
        this.propertyName = propertyName;
        this.writeMethodName = writeMethodName;
    }

    public void visit( DeltaVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override public String toString()
    {
        return "NullReturnDelta{" +
               "propertyName='" + propertyName + '\'' +
               ", writeMethodName='" + writeMethodName + '\'' +
               '}';
    }
}
