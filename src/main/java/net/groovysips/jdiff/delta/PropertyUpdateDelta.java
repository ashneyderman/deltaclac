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
import net.groovysips.jdiff.DeltaVisitor;

/**
 * @author Alex Shneyderman
 * @since 0.3
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
