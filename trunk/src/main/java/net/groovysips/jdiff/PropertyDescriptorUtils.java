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
package net.groovysips.jdiff;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import static net.groovysips.jdiff.StringUtils.buildLogableString;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Various property descriptor utilities. This class relies on spring to fetch property descriptions.
 *
 * @author Alex Shneyderman
 * @since 0.3
 */
public abstract class PropertyDescriptorUtils
{

    private static final Log LOG = LogFactory.getLog(PropertyDescriptorUtils.class);

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
            readMethod = seeIfWeNeedToSetCustomBooleanGetter( source.getClass(), propDesc );
        }

        if( readMethod == null )
        {
            throw new RuntimeException( "Unable to find read method on the source object." + propDesc.getName() );
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
     * @param source       - object to read the field value from
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

        if(propDesc == null)
        {
            LOG.error( buildLogableString( "property descriptor is null.",
                                           new Object[][] {
                                               {"source",source},
                                               {"propertyName",propertyName}
                                           } ) );
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
            String msg = buildLogableString( "Unable to find a write method for property.",
                                             new Object[][] {
                                                 {"propDesc.name",propDesc.getName()},
                                                 {"target.class", target.getClass()},
                                                 {"value",value}
                                             } );
            throw new RuntimeException( msg );
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
               Number.class.isAssignableFrom( propertyType ) ||
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

    /**
     *
     * @param target
     * @param excludes
     * @return array of prop descriptors
     */
    public static final PropertyDescriptor[] getPropertyDescriptors( Object target, List<String> excludes)
    {
        if (excludes == null || excludes.isEmpty())
        {
            return placeSystemPropertiesOnTop( BeanUtils.getPropertyDescriptors( target.getClass() ) );
        }

        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors( target.getClass() );
        List<PropertyDescriptor> lDescriptors = Arrays.asList( descriptors );
        List<PropertyDescriptor> result = new ArrayList<PropertyDescriptor>();
        for( PropertyDescriptor d : lDescriptors )
        {
            if (excludes.contains( d.getName() ) )
            {
                continue;
            }
            result.add( d );
        }

        return placeSystemPropertiesOnTop( result.toArray( new PropertyDescriptor[0] ));
    }

    /**
     *
     * @param target
     * @return array of prop descriptors
     */
    public static final PropertyDescriptor[] getPropertyDescriptors( Object target )
    {
        return placeSystemPropertiesOnTop( BeanUtils.getPropertyDescriptors( target.getClass() ) );
    }

    /**
     *
     * @param obj
     * @param propertyName
     * @return prop descriptor
     */
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

    // HELPERS
    private static Method seeIfWeNeedToSetCustomBooleanGetter( Class hostObjectClass, PropertyDescriptor sourceFieldDesc )
    {
        if( sourceFieldDesc.getPropertyType() != Boolean.class )
        {
            return null;
        }

        String altReader = "is" + StringUtils.capitalize( sourceFieldDesc.getName() );
        try
        {
            return hostObjectClass.getDeclaredMethod( altReader );
        }
        catch( Exception e )
        {
        }

        return null;
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

}
