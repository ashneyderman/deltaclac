package net.groovysips.jdiff.delta;

import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.PropertyDescriptorUtils;

/**
 * TODO: provide javadoc.
 *
 * @author Shneyderman
 */
public class JavaBeanDelta extends AbstractCompositeDelta
{

    private Class rootObjectClass = null;
    private String propertyName = null;

    public String getPropertyName()
    {
        return propertyName;
    }

    public Class getRootObjectClass()
    {
        return rootObjectClass;
    }

    public JavaBeanDelta( Class rootObjectClass )
    {
        this.rootObjectClass = rootObjectClass;
    }

    public JavaBeanDelta( Class rootObjectClass, String propertyName )
    {
        this.rootObjectClass = rootObjectClass;
        this.propertyName = propertyName;
    }

    public JavaBeanDelta( Delta graph, String propertyName )
    {
        JavaBeanDelta prototype = (JavaBeanDelta) graph;
        this.rootObjectClass = prototype.rootObjectClass;
        this.children().addAll( prototype.children() );
        this.propertyName = propertyName;
    }

    public Object createInstance()
    {
        try
        {
            return rootObjectClass.newInstance();
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Unable to instantiate class " + rootObjectClass.getName(), e );
        }
    }

    @Override public String toString()
    {
        return "BeanCreate{" +
               "assignedToProperty='" + propertyName + '\'' +
               ", beanClass=" + ( rootObjectClass == null ? "unknown" : rootObjectClass.getName() ) +
               '}';
    }
}
