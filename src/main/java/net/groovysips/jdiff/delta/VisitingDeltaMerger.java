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

import java.util.Stack;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import net.groovysips.jdiff.CompositeDelta;
import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaMerger;
import net.groovysips.jdiff.DeltaVisitor;
import net.groovysips.jdiff.PropertyDescriptorUtils;
import static net.groovysips.jdiff.StringUtils.buildLogableString;
import org.springframework.util.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * @author Alex Shneyderman
 * @since 0.3
 */
public class VisitingDeltaMerger implements DeltaVisitor, DeltaMerger
{

    private static final Log LOG = LogFactory.getLog(VisitingDeltaMerger.class);

    private ItemAppenderFactory appenderFactory;

    public ItemAppenderFactory getAppenderFactory()
    {
        return appenderFactory;
    }

    private Stack resultStack = new Stack();

    public VisitingDeltaMerger ()
    {
        appenderFactory = new ItemAppenderFactory() {
            public ItemAppender create( Object item )
            {
                return new ItemAppender() {
                    public void append( Stack resultStack, Collection collection, Object item )
                    {
                        collection.add( item );
                    }
                };
            }
        };
    }

    public VisitingDeltaMerger (ItemAppenderFactory appenderFactory)
    {
        this.appenderFactory = appenderFactory;
    }
    public void visit( Delta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.visit", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        if( delta instanceof JavaBeanDelta )
        {
            handleStart( (JavaBeanDelta) delta );
        }
        else if( delta instanceof SimpleContainerDelta )
        {
            handleStart( (SimpleContainerDelta) delta );
        }
        else if( delta instanceof CollectionDelta )
        {
            handleStart( (CollectionDelta) delta );
        }
        else if( delta instanceof NewItemDelta )
        {
            handleStart( (NewItemDelta) delta );
        }
        else if( delta instanceof UpdateItemDelta )
        {
            handleStart( (UpdateItemDelta) delta );
        }
        else
        {
            String msg = buildLogableString( "We do not know yet how to deal with this delta in 'visit( Delta delta )' method.",
                                             new Object[][]{
                                                 { "delta", delta }
                                             } );
            throw new RuntimeException( msg );
        }
    }

    public void visitChild( Delta child )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.visitChild", new Object [][] { { "child",child }, {"stack",resultStack} } ));
        }

        if( child instanceof CompositeDelta )
        {
            child.visit( this );
        }
        else if( child instanceof NullReturnDelta )
        {
            handleNullReturn( (NullReturnDelta) child );
        }
        else if( child instanceof PropertyUpdateDelta )
        {
            handlePropertyUpdate( (PropertyUpdateDelta) child );
        }
        else if( child instanceof RemoveItemDelta )
        {
            handleRemoveItem( (RemoveItemDelta) child );
        }
        else if( child instanceof PrimitiveValueDelta )
        {
            handlePrimitiveValue( (PrimitiveValueDelta) child );
        }
        else if( child instanceof ClearAllDelta )
        {
            handleClearAll( (ClearAllDelta) child );
        }
        else if( child == Delta.NULL )
        {
            ;
        }
        else
        {
            String msg = buildLogableString( "We do not know yet how to deal with this child delta in 'visit( CompositeDelta parent, Delta child )' method.",
                                             new Object[][]{
                                                 { "child", child }
                                             } );
            throw new RuntimeException( msg );
        }
    }

    public void endVisit( CompositeDelta composite )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.endVisit", new Object [][] { { "composite",composite }, {"stack",resultStack} } ));
        }

        if( composite == null )
        {
            return;
        }

        if (composite instanceof NewItemDelta)
        {
            handleEnd((NewItemDelta) composite);
            return;
        }
        else if (composite instanceof UpdateItemDelta )
        {
            return; // we do not need to pop anything from the stack / it was done already.
        }

        Object popped = resultStack.pop();

        if( resultStack.peek() instanceof ResultHolder )
        {
            ( (ResultHolder) resultStack.peek() ).result = popped;
        }
    }

    public Object merge( Object object, Delta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.merge", new Object [][] { { "object",object }, {"delta",delta}, {"stack",resultStack} } ));
        }

        resultStack.push( new ResultHolder() );

        if( expectsInitialObjectOnResultStack( delta ) )
        {
            resultStack.push( object );
        }

        if( delta instanceof CompositeDelta )
        {
            delta.visit( this );      // this spawns the whole visitor machinery
        }
        else
        {
            this.visitChild( delta ); // this is a simple cheat
        }

        return ( (ResultHolder) resultStack.pop() ).result;
    }

    // HELPERS.
    private void handleStart( JavaBeanDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleStart - JavaBeanDelta", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        Object instance = delta.createInstance();

        if( StringUtils.hasText( delta.getPropertyName() ) )
        {
            PropertyDescriptorUtils.write( resultStack.peek(), instance, delta.getPropertyName() );
        }

        resultStack.push( instance );
    }

    private void handleStart( SimpleContainerDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleStart - SimpleContainerDelta", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        if( delta == null )
        {
            return;
        }

        if( StringUtils.hasText( delta.getPropertyName() ) )
        {
            Object bean = PropertyDescriptorUtils.read( resultStack.peek(), delta.getPropertyName() );

            resultStack.push( bean );
        }
    }

    private void handleStart( CollectionDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleStart - CollectionDelta", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        if( delta == null )
        {
            return;
        }

        Collection collection = null;

        if( StringUtils.hasText( delta.getPropertyName() ) )
        {
            Object tos = null;
            if( resultStack.peek() instanceof ResultHolder )
            {
                tos = ((ResultHolder) resultStack.peek()).result;
            }
            else
            {
                tos = resultStack.peek();
            }

            collection = (Collection) PropertyDescriptorUtils.read( tos, delta.getPropertyName() );
        }

        if( collection == null )
        {
            try
            {
                collection = (Collection) ( delta.getCollectionClass() == null ? new ArrayList() : delta.getCollectionClass().newInstance() );
            }
            catch( Exception e )
            {
                throw new RuntimeException( "Collection class can be instantiated '" + delta.getCollectionClass() + "'" );
            }

            if( StringUtils.hasText( delta.getPropertyName() ) )
            {
                PropertyDescriptorUtils.write( resultStack.peek(), collection, delta.getPropertyName() );
            }
        }

        if( collection instanceof Collection )
        {
            resultStack.push( collection );
        }
        else
        {
            throw new RuntimeException( "CollectionDelta can only be applied to an instance of java.util.Collection" );
        }
    }

    private void handleStart( NewItemDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleStart - NewItemDelta", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        resultStack.push( new ResultHolder() );
    }

    private void handleStart( UpdateItemDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleStart - UpdateItemDelta", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        FinderCriteria crit = delta.getFinderCriteria();

        Object item = crit.find( (Collection) resultStack.peek() );

        resultStack.push( item );
    }

    private void handleEnd( NewItemDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleEnd - NewItemDelta", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        ResultHolder rHolder = (ResultHolder) resultStack.pop();

        ItemAppender appender = appenderFactory.create(rHolder.result);

        appender.append( resultStack, (Collection) resultStack.peek(), rHolder.result );
    }

    private void handlePropertyUpdate( PropertyUpdateDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handlePropertyUpdate - PropertyUpdateDelta", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        if( delta == null )
        {
            return;
        }

        String writeMethodName = delta.getWriteMethodName();

        String propertyName = delta.getPropertyName();

        Object val = delta.getNewValue();

        Object obj = resultStack.peek();

        if( StringUtils.hasText( writeMethodName ) )
        {
            PropertyDescriptorUtils.writeWith( obj, val, writeMethodName );
        }
        else
        {
            PropertyDescriptorUtils.write( obj, val, propertyName );
        }
    }

    private void handleClearAll( ClearAllDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleClearAll - ClearAllDelta", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        if( delta == null )
        {
            return;
        }

        if (resultStack.peek() instanceof Collection)
        {
            ((Collection) resultStack.peek()).clear();
        }
        else
        {
            throw new RuntimeException( "ClearAllDelta can only be applied to the stack that has collection on the top." );
        }
    }

    private void handleNullReturn( NullReturnDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleNullReturn", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        if( delta.getPropertyName() == null )
        {
            if( resultStack.peek() instanceof ResultHolder )
            {
                ( (ResultHolder) resultStack.peek() ).result = null;
                return;
            }

            throw new RuntimeException( "NullReturnDelta with no target property name is not supported on non-empty result stack." );
        }

        if( StringUtils.hasText( delta.getWriteMethodName() ) )
        {
            PropertyDescriptorUtils.writeWith( resultStack.peek(), null, delta.getWriteMethodName() );
        }
        else
        {
            PropertyDescriptorUtils.write( resultStack.peek(), null, delta.getPropertyName() );
        }
    }

    private void handleRemoveItem( RemoveItemDelta delta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handleRemoveItem", new Object [][] { { "delta",delta }, {"stack",resultStack} } ));
        }

        FinderCriteria crit = delta.getFinderCriteria();

        Collection collection = (Collection) resultStack.peek();

        Object item = crit.find( collection );

        Iterator iter = collection.iterator();

        while (iter.hasNext())
        {
            if (iter.next() == item)
            {
                iter.remove();
                
                break;
            }
        }
    }

    private void handlePrimitiveValue( PrimitiveValueDelta primitiveValueDelta )
    {
        if( LOG.isDebugEnabled() )
        {
            LOG.debug( buildLogableString( "VDM.handlePrimitiveValue", new Object [][] { { "primitiveValueDelta",primitiveValueDelta }, {"stack",resultStack} } ));
        }

        ResultHolder rHolder = (ResultHolder) resultStack.peek();

        rHolder.result = primitiveValueDelta.getValue();
    }

    private boolean expectsInitialObjectOnResultStack( Delta delta )
    {
        if( delta instanceof SimpleContainerDelta )
        {
            if( !StringUtils.hasText( ( (SimpleContainerDelta) delta ).getPropertyName() ) )
            {
                return true;
            }
        }

        return false;
    }
    private static class ResultHolder
    {
        public Object result;

        @Override public String toString()
        {
            return "RH{"+ result +"}";
        }
    }

}
