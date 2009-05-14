package net.groovysips.jdiff;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import static net.groovysips.jdiff.StringUtils.buildLogableString;

/**
 * Various property descriptor utilities. This class relies on spring to fetch property descriptions.
 *
 * @author Shneyderman
 */
public abstract class PropertyDescriptorUtils
{

    private static String[] sysprops = { "class" };

    /**
     * Reads a field value of the source instance given the field descriptor of that field.
     *
     * @param source   - object to read the field value from
     * @param propDesc - descriptor of the field to read
     * @return value of the field
     * @throws RuntimeException - if can not read or read method does not exist.
     */
    public static final Object read( Object source, PropertyDescriptor propDesc )
    {
        if( source == null )
        {
            return null;
        }

        Method readMethod = propDesc.getReadMethod();

        if( readMethod == null )
        {
            throw new RuntimeException( "Unable to find read method on the source object." );
        }

        try
        {
            return readMethod.invoke( source );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Unable to read property value.", e );
        }
    }

    /**
     * Reads a field value of the source instance given the field descriptor of that field.
     *
     * @param source   - object to read the field value from
     * @param propertyName - property name
     * @return value of the field
     * @throws RuntimeException - if can not read or read method does not exist.
     */
    public static final Object read( Object source, String propertyName )
    {
        if( source == null )
        {
            return null;
        }

        PropertyDescriptor propDesc = fetchDescriptor( source, propertyName );

        Method readMethod = propDesc.getReadMethod();

        if( readMethod == null )
        {
            throw new RuntimeException( "Unable to find read method on the source object." );
        }

        try
        {
            return readMethod.invoke( source );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Unable to read property value.", e );
        }
    }

    /**
     * Writes value into the field of the target instance given property descriptor.
     *
     * @param target   - target object
     * @param value    - value for the field
     * @param propDesc - descriptor of the field
     * @throws RuntimeException - if can not write or write method does not exist.
     */
    public static final void write( Object target, Object value, PropertyDescriptor propDesc )
    {
        if( target == null )
        {
            return;
        }

        Method writeMethod = propDesc.getWriteMethod();

        if( writeMethod == null )
        {
            throw new RuntimeException( "Unable to find a write method on the target object." );
        }

        try
        {
            writeMethod.invoke( target, value );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Unable to write value to the target object.", e );
        }
    }

    /**
     * Writes value into the field of the target instance given property name.
     *
     * @param target   - target object
     * @param value    - value for the field
     * @param propName - name of the field
     * @throws RuntimeException - if can not write or write method does not exist.
     */
    public static final void write( Object target, Object value, String propName )
    {
        PropertyDescriptor pd = fetchDescriptor( target, propName );

        if( pd != null )
        {
            write( target, value, pd );
        }
    }

    /**
     * Writes value into the field of the target instance given the name of the write method.
     *
     * @param target     - target object
     * @param value      - value for the field
     * @param methodName - name of the method to use
     * @throws RuntimeException - if can not write or write method does not exist.
     */
    public static final void writeWith( Object target, Object value, String methodName )
    {
        Method writeMethod = null;
        try
        {
            writeMethod = target.getClass().getMethod( methodName, value.getClass() );
            writeMethod.invoke( target, value );
        }
        catch( Exception e )
        {
            String msg = buildLogableString( "Unable to write new value property on target with custom write method.",
                                             new Object[][]{
                                                 { "target.class", target.getClass() },
                                                 { "value", value },
                                                 { "methodName", methodName }
                                             } );
            throw new RuntimeException( msg, e );
        }
    }

    /**
     * Determines if the class is primitive enough for the purpose of gdiff.
     *
     * @param propertyType
     * @return true if primitive or one of the primitive looking classes.
     */
    public static boolean isPrimitive( Class propertyType )
    {
        if( propertyType == null )
        {
            throw new IllegalArgumentException( "Class propertyType - can not be null." );
        }

        return propertyType.isPrimitive() ||
               propertyType.isAssignableFrom( Boolean.class ) ||
               propertyType.isAssignableFrom( String.class ) ||
               propertyType.isAssignableFrom( Number.class ) ||
               propertyType.isAssignableFrom( Date.class ) ||
               propertyType.isEnum();
    }

    /**
     * @param propertyName - property name to check
     * @return true if property is a system provided property of the class.
     */
    public static boolean isSystemProperty( String propertyName )
    {
        if( !StringUtils.hasLength( propertyName ) )
        {
            return false;
        }

        for( int i = 0; i < sysprops.length; i++ )
        {
            if( propertyName.equals( sysprops[ i ] ) )
            {
                return true;
            }
        }

        return false;
    }

    public static final PropertyDescriptor[] getPropertyDescriptors( Object target )
    {
        // TODO (Shneyderman - May 8, 2009) : potential optimization to cache the results of the call
        return placeSystemPropertiesOnTop( BeanUtils.getPropertyDescriptors( target.getClass() ) );
    }

    private static final PropertyDescriptor[] placeSystemPropertiesOnTop( PropertyDescriptor[] arg )
    {
        List<PropertyDescriptor> list = Arrays.asList( arg );
        Collections.sort( list, new Comparator<PropertyDescriptor>()
        {
            public int compare( PropertyDescriptor pd1, PropertyDescriptor pd2 )
            {
                if( isSystemProperty( pd1.getName() ) &&
                    !isSystemProperty( pd2.getName() ) )
                {
                    return -1;
                }
                else if( !isSystemProperty( pd1.getName() ) &&
                         isSystemProperty( pd2.getName() ) )
                {
                    return 1;
                }
                return pd1.getName().compareTo( pd2.getName() );
            }
        } );

        return (PropertyDescriptor[]) list.toArray( new PropertyDescriptor[list.size()] );
    }

    public static final PropertyDescriptor fetchDescriptor( Object obj, String propertyName )
    {
        if( obj == null )
        {
            return null;
        }

        if( !StringUtils.hasText( propertyName ) )
        {
            return null;
        }

        return BeanUtils.getPropertyDescriptor( obj.getClass(), propertyName );
    }

}
