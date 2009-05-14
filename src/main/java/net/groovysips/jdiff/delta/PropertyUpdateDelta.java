package net.groovysips.jdiff.delta;

import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaVisitor;

/**
 * @author Shneyderman
 */
public class PropertyUpdateDelta implements Delta
{

    private String propertyName;
    private Object newValue;
    private Object oldValue;
    private String writeMethodName;

    public Object getNewValue()
    {
        return newValue;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public Object getOldValue()
    {
        return oldValue;
    }

    public String getWriteMethodName()
    {
        return writeMethodName;
    }

    public PropertyUpdateDelta( String propertyName, Object newValue )
    {
        this( propertyName, newValue, null );
    }

    public PropertyUpdateDelta( String propertyName, Object newValue, Object oldValue )
    {
        this( propertyName, newValue, oldValue, null );
    }

    public PropertyUpdateDelta( String propertyName, Object newValue, Object oldValue, String writeMethodName )
    {
        this.propertyName = propertyName;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.writeMethodName = writeMethodName;
    }

    public void visit( DeltaVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override public String toString()
    {
        return "PropertyUpdate{" +
               "name='" + propertyName + '\'' +
               ", newVal='" + newValue + "'" +
               ", oldVal='" + oldValue + "'" +
               ", writeWith='" + writeMethodName + "'" +
               '}';
    }

}
