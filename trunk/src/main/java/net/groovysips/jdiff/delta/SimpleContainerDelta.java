package net.groovysips.jdiff.delta;

/**
 * A composite delta that has no modifications of its own. The only effective changes are made by its children.
 *
 * @author Shneyderman
 */
public class SimpleContainerDelta extends AbstractCompositeDelta
{

    private String propertyName;
    public String getPropertyName()
    {
        return propertyName;
    }

    public SimpleContainerDelta(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public String toString()
    {
        return "PropertiesUpdate{" +
               "propertyName='" + propertyName + "';" +
               "numberOfProperties=" + ( children() == null ? "0" : children().size() ) +
               '}';
    }

}