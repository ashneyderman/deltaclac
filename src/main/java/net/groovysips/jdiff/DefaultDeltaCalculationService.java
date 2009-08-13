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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.StringWriter;
import net.groovysips.jdiff.delta.DeltaPrinter;

/**
 * @author Alex Shneyderman
 * @since 0.3
 */
public class DefaultDeltaCalculationService implements DeltaCalculationService
{

    private static final Log LOG = LogFactory.getLog(DefaultDeltaCalculationService.class);

    private DeltaBuilder deltaBuilder;

    public DeltaBuilder getDeltaBuilder()
    {
        return deltaBuilder;
    }

    public void setDeltaBuilder( DeltaBuilder deltaBuilder )
    {
        this.deltaBuilder = deltaBuilder;
    }

    private DeltaMerger merger;

    public DeltaMerger getMerger()
    {
        return merger;
    }

    public void setMerger( DeltaMerger merger )
    {
        this.merger = merger;
    }

    public DefaultDeltaCalculationService()
    {
    }

    public DefaultDeltaCalculationService( DeltaBuilder deltaBuilder )
    {
        setDeltaBuilder( deltaBuilder );
    }

    public DefaultDeltaCalculationService( DeltaBuilder deltaBuilder, DeltaMerger merger )
    {
        setDeltaBuilder( deltaBuilder );
        setMerger( merger );
    }

    public Delta diff( Object original, Object modified )
    {
        Delta result = deltaBuilder.build( original, modified );

        if (LOG.isDebugEnabled())
        {
            StringWriter w = new StringWriter( );
            result.visit( new DeltaPrinter( w ) );
            LOG.debug( "Diff result - \n" + w.getBuffer().toString() );
        }
        
        return result;
    }

    public Object apply( Object object, Delta delta )
    {
        return merger.merge( object, delta );
    }

}
