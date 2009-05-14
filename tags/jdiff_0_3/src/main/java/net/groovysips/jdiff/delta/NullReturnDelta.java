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
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.3
 */
public class NullReturnDelta implements Delta
{

    private String propertyName;
    private String writeMethodName;

    public String getPropertyName()
    {
        return propertyName;
    }

    public String getWriteMethodName()
    {
        return writeMethodName;
    }

    public NullReturnDelta()
    {
        this( null, null );
    }

    public NullReturnDelta( String propertyName, String writeMethodName )
    {
        this.propertyName = propertyName;
        this.writeMethodName = writeMethodName;
    }

    public void visit( DeltaVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override public String toString()
    {
        return "NullReturnDelta{" +
               "propertyName='" + propertyName + '\'' +
               ", writeMethodName='" + writeMethodName + '\'' +
               '}';
    }
}
