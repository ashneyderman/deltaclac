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
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
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

    private Map<Class, List<String>> refilledCollection = new HashMap<Class, List<String>>();

    public Map<Class, List<String>> getRefilledCollection()
    {
        return refilledCollection;
    }

    public void setRefilledCollection( Map<Class, List<String>> refilledCollection )
    {
        this.refilledCollection = refilledCollection;
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

    private FinderCriteriaFactory finderCriteriaFactory;

    public FinderCriteriaFactory getFinderCriteriaFactory()
    {
        return finderCriteriaFactory;
    }

    public void setFinderCriteriaFactory( FinderCriteriaFactory finderCriteriaFactory )
    {
        this.finderCriteriaFactory = finderCriteriaFactory;
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

        List<String> excludedProps = findExcludedProperties(modified.getClass());

        modifiedObjectPropertyDescs = PropertyDescriptorUtils.getPropertyDescriptors( modified, excludedProps );

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

        List<String> excludedProps = findExcludedProperties(modified.getClass());

        modifiedObjectPropertyDescs = PropertyDescriptorUtils.getPropertyDescriptors( modified, excludedProps );

        originalObjectPropertyDescs = PropertyDescriptorUtils.getPropertyDescriptors( original, excludedProps );

        AbstractCompositeDelta result = new SimpleContainerDelta(propertyeName);

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

        if( propertyType.isArray() )  // TODO (Shneyderman - Jun 13, 2009) : we do not do arrays yet.
        {
            return Delta.NULL;
        }

        if( Collection.class.isAssignableFrom( propertyType ) )
        {
            CollectionDelta collDelta = createNewCollectionDelta( (Collection) propNewValue, propertyDesc.getName(), propertyType );
            return (collDelta == null || collDelta.children() == null || collDelta.children().isEmpty()) ? Delta.NULL : collDelta;
        }

        // composite
        Object newCompositeVal = PropertyDescriptorUtils.read( modified, propertyDesc );

        return build( null, newCompositeVal, propertyDesc );
    }

    private Delta createDelta( Object original, Object modified, PropertyDescriptor originalPD, PropertyDescriptor modifiedPD )
    {
        Object originalPropVal = PropertyDescriptorUtils.read( original, originalPD );

        Object modifiedPropVal = PropertyDescriptorUtils.read( modified, modifiedPD );

        Class propertyType = modifiedPD.getPropertyType();
        if( Collection.class.isAssignableFrom( propertyType ) )
        {
            if (modifiedPropVal == null && originalPropVal == null)
            {
                return Delta.NULL;
            }

            if ((modifiedPropVal != null && ((Collection) modifiedPropVal).isEmpty()) &&
                (originalPropVal != null && ((Collection) originalPropVal).isEmpty()))
            {
                return Delta.NULL;
            }

            CollectionDelta collDelta = null;
            if (originalPropVal == null)
            {
                collDelta = createNewCollectionDelta( (Collection) modifiedPropVal, modifiedPD.getName(), modifiedPD.getPropertyType() );
            }
            else if ( isRefilledCollection( original.getClass(), modifiedPD.getName() ) )
            {
                collDelta = createRefillCollectionDelta( (Collection) modifiedPropVal, modifiedPD.getName(), modifiedPD.getPropertyType() );
            }
            else
            {
                collDelta = createOverlayCollectionDelta( (Collection) originalPropVal, (Collection) modifiedPropVal, modifiedPD.getName(), originalPD.getPropertyType() );
            }

            return (collDelta == null || collDelta.children() == null || collDelta.children().isEmpty()) ? Delta.NULL : collDelta;
        }

        if( ObjectUtils.nullSafeEquals( originalPropVal, modifiedPropVal ) )
        {
            return Delta.NULL;
        }

        if( PropertyDescriptorUtils.isPrimitive( propertyType ) ||
            modifiedPropVal == null)
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

        // it must be a composite.
        return build( originalPropVal, modifiedPropVal, originalPD );
    }

    private boolean isRefilledCollection( Class clazz, String name )
    {
        if (clazz == null || name == null)
        {
            return false;
        }

        List<String> pNames = findRefilledCollectionProperties( clazz );

        if (pNames == null)
        {
            return false;
        }

        return pNames.contains( name );
    }

    private CollectionDelta createRefillCollectionDelta(Collection modCollection, String propertyName, Class collectionClazz)
    {
        Class collectionType = determineCollectionType(collectionClazz);
        CollectionDelta result = new CollectionDelta(propertyName, collectionType);
        result.addChild( new ClearAllDelta() );
        if (modCollection == null)
        {
            return result;
        }

        for( Object modObj : modCollection )
        {
            Delta dToAdd = null;

            if ( modObj != null && PropertyDescriptorUtils.isPrimitive( modObj.getClass() ))
            {
                dToAdd = new NewItemDelta( new PrimitiveValueDelta( modObj ) );
            }
            else
            {
                Delta itemDelegateDelta = build( null, modObj );
                if (Delta.NULL != itemDelegateDelta)
                {
                    dToAdd = new NewItemDelta( (JavaBeanDelta) itemDelegateDelta ) ;
                }
            }

            if (dToAdd != null)
            {
                result.addChild( dToAdd );
            }
        }

        return result;
    }

    private CollectionDelta createNewCollectionDelta(Collection modCollection, String propertyName, Class collectionClazz)
    {
        Class collectionType = determineCollectionType(collectionClazz);
        CollectionDelta result = new CollectionDelta(propertyName, collectionType );

        for( Object modObj : modCollection )
        {
            if ( modObj != null && PropertyDescriptorUtils.isPrimitive( modObj.getClass() ))
            {
                result.addChild( new NewItemDelta( new PrimitiveValueDelta( modObj ) ) );
            }
            else
            {
                Delta itemDelegateDelta = build( null, modObj );
                if (Delta.NULL != itemDelegateDelta)
                {
                    result.addChild( new NewItemDelta( (JavaBeanDelta) itemDelegateDelta ) );
                }
            }
        }

        return result;
    }

    private Class determineCollectionType( Class collectionClazz )
    {
        if (Set.class.isAssignableFrom( collectionClazz ))
        {
            return HashSet.class;
        }
        else if (List.class.isAssignableFrom( collectionClazz ))
        {
            return ArrayList.class;
        }
        else if ( Map.class.isAssignableFrom( collectionClazz ))
        {
            return HashMap.class;
        }

        return ArrayList.class;
    }

    private CollectionDelta createOverlayCollectionDelta(Collection origCollection, Collection modCollection, String propertyName, Class collectionClazz)
    {
        Class collectionType = determineCollectionType(collectionClazz);
        CollectionDelta result = new CollectionDelta(propertyName, collectionType );

        if(modCollection == null)
        {
            return result;
        }

        for( Object modObj : modCollection )
        {
            FinderCriteria crit = finderCriteriaFactory.create( modObj );
            Object origObj = crit.find( origCollection );
            if (origObj == null)
            {
                if ( modObj != null && PropertyDescriptorUtils.isPrimitive( modObj.getClass() ))
                {
                    result.addChild( new NewItemDelta( new PrimitiveValueDelta( modObj ) ) );
                }
                else
                {
                    Delta itemDelegateDelta = build( null, modObj );
                    if (Delta.NULL != itemDelegateDelta)
                    {
                        result.addChild( new NewItemDelta( (JavaBeanDelta) itemDelegateDelta ) );
                    }
                }
            }
            else
            {
                Delta delta = build( origObj, modObj );
                if (Delta.NULL != delta)
                {
                    result.addChild( new UpdateItemDelta( (SimpleContainerDelta) delta, crit ) );
                }
            }
        }

        // remove original items that are not in the modified collection.
        // TODO (Shneyderman - Jun 30, 2009) : we do not use this feature as all of our collections are refill or add only
        //                                     (items for example). So, we need some sane way of dealing with this.
        //        for( Object origObj : origCollection )
        //        {
        //            FinderCriteria crit = finderCriteriaFactory.create( origObj );
        //            Object modObj = crit.find( modCollection );
        //            if (modObj == null)
        //            {
        //                result.addChild( new RemoveItemDelta( crit ) );
        //            }
        //        }

        return result;
    }
    private String findPropertyWriteMethodOverride( Object object, PropertyDescriptor propertyDescriptor )
    {
        if( object == null || propertyDescriptor == null )
        {
            return null;
        }

        Class objClass = object.getClass();

        String propName = propertyDescriptor.getName();

        Map<String, String> classOverrides = writeMethodOverrides.get( objClass );

        if( classOverrides != null && classOverrides.get( propName ) != null )
        {
            return classOverrides.get( propName );
        }

        // let's see if there is any superclass in that map.
        Set<Class> keys = writeMethodOverrides.keySet();

        for( Class keyClass : keys )
        {
            if( keyClass == objClass )
            {
                continue;
            }

            if( keyClass.isAssignableFrom( objClass ) )
            {
                Map<String, String> superClass = writeMethodOverrides.get( keyClass );

                if (superClass != null && superClass.get( propName ) != null)
                {
                    return superClass.get( propName );
                }
            }
        }

        return null;
    }

    private List<String> findExcludedProperties( Class clazz )
    {
        if (excludes == null || clazz == null)
        {
            return null;
        }

        Set<String> result = new HashSet<String>();

        List<String> directClass = excludes.get( clazz );

        if( directClass != null )
        {
            result.addAll(directClass);
        }

        // let's see if there is any superclass in that map.
        Set<Class> keys = excludes.keySet();

        for( Class keyClass : keys )
        {
            if( keyClass.isAssignableFrom( clazz ) ) //&& keyClass != clazz )
            {
                List<String> superClass = excludes.get( keyClass );

                if (superClass != null)
                {
                    result.addAll( superClass );
                }
            }
        }

        if (result.isEmpty())
        {
            return null;
        }

        return new ArrayList<String>( result );
    }

    private List<String> findRefilledCollectionProperties( Class clazz )
    {
        if (refilledCollection == null || clazz == null)
        {
            return null;
        }

        Set<String> result = new HashSet<String>();

        List<String> directClass = refilledCollection.get( clazz );

        if( directClass != null )
        {
            result.addAll(directClass);
        }

        // let's see if there is any superclass in that map.
        Set<Class> keys = refilledCollection.keySet();

        for( Class keyClass : keys )
        {
            if( keyClass.isAssignableFrom( clazz ) ) //&& keyClass != clazz )
            {
                List<String> superClass = refilledCollection.get( keyClass );

                if (superClass != null)
                {
                    result.addAll( superClass );
                }
            }
        }

        if (result.isEmpty())
        {
            return null;
        }

        return new ArrayList<String>( result );
    }
}
