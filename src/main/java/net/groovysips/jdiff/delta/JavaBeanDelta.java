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

import net.groovysips.jdiff.Delta;

/**
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.3
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
            throw new RuntimeException( "Unable to instantiate class " + rootObjectClass, e );
        }
    }

    @Override public String toString()
    {
        return "BeanCreate{" +
               "assignedToProperty='" + propertyName + "'" +
               ", beanClass='" + rootObjectClass + "'" +
               "}";
    }
}
