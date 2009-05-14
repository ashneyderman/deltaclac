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

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaBuilder;
import net.groovysips.jdiff.PropertyDescriptorUtils;
import org.springframework.util.ObjectUtils;

/**
 * A delta builder that relies on reflection to do the diffing.
 *
 * @author Alex Shneyderman
 * @since 0.3
 */
public class DefaultDeltaBuilder implements DeltaBuilder
{

    // AUTOINJECTED.
    private Map<Class, List<String>> excludes = new HashMap<Class, List<String>>();

    public Map<Class, List<String>> getExcludes()
    {
        return excludes;
    }

    public void setExcludes( Map<Class, List<String>> excludes )
    {
        this.excludes = excludes;
    }

    private Map<Class, Map<String, String>> writeMethodOverrides = new HashMap<Class, Map<String, String>>();

    public Map<Class, Map<String, String>> getWriteMethodOverrides()
    {
        return writeMethodOverrides;
    }

    public void setWriteMethodOverrides( Map<Class, Map<String, String>> writeMethodOverrides )
    {
        this.writeMethodOverrides = writeMethodOverrides;
    }

    // INTERFACE.
    /**
     * Calculates delta between two java beans.
     *
     * @param original java bean
     * @param modified java bean
     * @return delta
     */
    public Delta build( Object original, Object modified )
    {
        if( original == null && modified != null )
        {
            return buildDeltaWhereOriginalIsNullAndModifiedIsNotNull( modified, null );
        }

        if( original != null && modified != null )
        {
            return buildDeltaWhereOriginalIsNotNullAndModifiedIsNotNull( original, modified, null );
        }

        if( modified == null )
        {
            return new NullReturnDelta();
        }

        return Delta.NULL;
    }

    /**
     * Calculates delta between two java beans that can trigger an assignment of the result to a property described by
     * propertyDescriptor argument.
     *
     * @param original
     * @param modified
     * @param propertyDescriptor
     * @return delta
     */
    public Delta build( Object original, Object modified, PropertyDescriptor propertyDescriptor )
    {
        Delta result = Delta.NULL;

        if( original == null && modified != null )
        {
            result = buildDeltaWhereOriginalIsNullAndModifiedIsNotNull( modified, propertyDescriptor );
        }

        if( original != null && modified != null )
        {
            result = buildDeltaWhereOriginalIsNotNullAndModifiedIsNotNull( original, modified, propertyDescriptor.getName() );
        }

        if( original != null && modified == null )
        {
            String propertyWriteMethodName = findPropertyWriteMethodOverride( original, propertyDescriptor );
            return new NullReturnDelta( propertyDescriptor.getName(), propertyWriteMethodName );
        }

        return result;
    }

    // HELPERS.
    private Delta buildDeltaWhereOriginalIsNullAndModifiedIsNotNull( Object modified, PropertyDescriptor propertyDescriptor )
    {
        PropertyDescriptor[] modifiedObjectPropertyDescs = EMPTY_PROPERTY_DESCRIPTORS_ARRAY;

        modifiedObjectPropertyDescs = PropertyDescriptorUtils.getPropertyDescriptors( modified );

        AbstractCompositeDelta result = null;

        if( propertyDescriptor == null )
        {
            result = new JavaBeanDelta( modified.getClass() );
        }
        else
        {
            result = new JavaBeanDelta( modified.getClass(), propertyDescriptor.getName() );
        }

        for( int i = 0; i < modifiedObjectPropertyDescs.length; i++ )
        {
            PropertyDescriptor propertyDesc = modifiedObjectPropertyDescs[ i ];

            if( PropertyDescriptorUtils.isSystemProperty( propertyDesc.getName() ) )
            {
                continue;
            }

            Delta delta = createDeltaForNullOriginal( modified, propertyDesc );

            if( delta != null && delta != Delta.NULL )
            {
                result.addChild( delta );
            }
        }

        return result;
    }

    private Delta buildDeltaWhereOriginalIsNotNullAndModifiedIsNotNull( Object original, Object modified, String propertyeName )
    {
        PropertyDescriptor[] modifiedObjectPropertyDescs = EMPTY_PROPERTY_DESCRIPTORS_ARRAY;

        PropertyDescriptor[] originalObjectPropertyDescs = EMPTY_PROPERTY_DESCRIPTORS_ARRAY;

        modifiedObjectPropertyDescs = PropertyDescriptorUtils.getPropertyDescriptors( modified );

        originalObjectPropertyDescs = PropertyDescriptorUtils.getPropertyDescriptors( original );

        AbstractCompositeDelta result = new SimpleContainerDelta( propertyeName );

        for( int i = 0; i < modifiedObjectPropertyDescs.length; i++ )
        {
            PropertyDescriptor modifiedObjectPropertyDesc = modifiedObjectPropertyDescs[ i ];

            PropertyDescriptor originalObjectPropertyDesc = originalObjectPropertyDescs[ i ];

            if( PropertyDescriptorUtils.isSystemProperty( modifiedObjectPropertyDesc.getName() ) )
            {
                continue;
            }

            Delta delta = createDelta( original, modified, originalObjectPropertyDesc, modifiedObjectPropertyDesc );

            if( delta != null && delta != Delta.NULL )
            {
                result.addChild( delta );
            }
        }

        if( result.children() != null && !result.children().isEmpty() )
        {
            return result;
        }

        return Delta.NULL;
    }

    private Delta createDeltaForNullOriginal( Object modified, PropertyDescriptor propertyDesc )
    {
        Object propNewValue = PropertyDescriptorUtils.read( modified, propertyDesc );

        if( propNewValue == null )
        {
            return Delta.NULL;
        }

        Class propertyType = propertyDesc.getPropertyType();
        if( PropertyDescriptorUtils.isPrimitive( propertyType ) )
        {
            String propertyWriteMethodName = findPropertyWriteMethodOverride( modified, propertyDesc );
            return new PropertyUpdateDelta( propertyDesc.getName(), propNewValue, null, propertyWriteMethodName );
        }

        if( propertyType.isArray() )
        {
            return Delta.NULL;
        }

        if( propertyType.isAssignableFrom( Collection.class ) )
        {
            return Delta.NULL;
        }

        // composite
        Object newCompositeVal = PropertyDescriptorUtils.read( modified, propertyDesc );

        return build( null, newCompositeVal, propertyDesc );
    }

    private Delta createDelta( Object original, Object modified, PropertyDescriptor originalPD, PropertyDescriptor modifiedPD )
    {
        Object originalPropVal = PropertyDescriptorUtils.read( original, originalPD );

        Object modifiedPropVal = PropertyDescriptorUtils.read( modified, modifiedPD );

        if( ObjectUtils.nullSafeEquals( originalPropVal, modifiedPropVal ) )
        {
            return Delta.NULL;
        }

        Class propertyType = modifiedPD.getPropertyType();
        if( PropertyDescriptorUtils.isPrimitive( propertyType ) )
        {
            String propertyWriteMethodName = findPropertyWriteMethodOverride( original != null ? original : modified,
                                                                              originalPD != null ? originalPD : modifiedPD );
            String propertyName = originalPD != null ? originalPD.getName() : modifiedPD.getName();
            return new PropertyUpdateDelta( propertyName, modifiedPropVal, originalPropVal, propertyWriteMethodName );
        }

        if( propertyType.isArray() )
        {
            return Delta.NULL;
        }

        if( propertyType.isAssignableFrom( Collection.class ) )
        {
            return Delta.NULL;
        }

        // it must be a composite.
        return build( originalPropVal, modifiedPropVal, originalPD );
    }

    private String findPropertyWriteMethodOverride( Object object, PropertyDescriptor propertyDescriptor )
    {
        if( object == null || propertyDescriptor == null )
        {
            throw new IllegalArgumentException( "None of the argumetns can be null." );
        }

        Class objClass = object.getClass();

        String propName = propertyDescriptor.getName();

        Map<String, String> classOverrides = writeMethodOverrides.get( objClass );

        if( classOverrides != null )
        {
            return classOverrides.get( propName );
        }

        return null;
    }

}
