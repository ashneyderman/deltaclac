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
import net.groovysips.jdiff.CompositeDelta;
import net.groovysips.jdiff.Delta;
import net.groovysips.jdiff.DeltaMerger;
import net.groovysips.jdiff.DeltaVisitor;
import net.groovysips.jdiff.PropertyDescriptorUtils;
import static net.groovysips.jdiff.StringUtils.buildLogableString;
import org.springframework.util.StringUtils;

/**
 * @author Alex Shneyderman
 * @since 0.3
 */
public class VisitingDeltaMerger implements DeltaVisitor, DeltaMerger
{

    private Stack resultStack = new Stack();

    public void visit( Delta delta )
    {
        if( delta instanceof JavaBeanDelta )
        {
            handleStart( (JavaBeanDelta) delta );
        }
        else if( delta instanceof SimpleContainerDelta )
        {
            handleStart( (SimpleContainerDelta) delta );
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
        if( composite == null )
        {
            return;
        }

        Object popped = resultStack.pop();

        if( resultStack.peek() instanceof ResultHolder )
        {
            ( (ResultHolder) resultStack.peek() ).result = popped;
        }
    }

    public Object merge( Object object, Delta delta )
    {
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

    // HELPERS.
    private void handleStart( JavaBeanDelta delta )
    {
        Object instance = delta.createInstance();

        if( StringUtils.hasText( delta.getPropertyName() ) )
        {
            PropertyDescriptorUtils.write( resultStack.peek(), instance, delta.getPropertyName() );
        }

        resultStack.push( instance );
    }

    private void handleStart( SimpleContainerDelta delta )
    {
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

    private void handlePropertyUpdate( PropertyUpdateDelta delta )
    {
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

    private void handleNullReturn( NullReturnDelta delta )
    {
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

    private static class ResultHolder
    {
        public Object result;
    }

}
