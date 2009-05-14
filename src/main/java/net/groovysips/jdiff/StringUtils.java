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

import java.util.List;

/**
 * @author Alex Shneyderman
 * @since 0.3
 */
public class StringUtils
{

    public static final String buildLogableString( String msg, Object[][] ctx )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( msg );

        if( ctx == null )
        {
            return sb.toString();
        }

        for( int i = 0; i < ctx.length; i++ )
        {
            if( i == 0 )
            {
                sb.append( " - " );
            }
            if( i > 0 )
            {
                sb.append( ", " );
            }

            if( ctx[ i ][ 1 ] == null )
            {
                sb.append( "" + ctx[ i ][ 0 ] + ": null" );
            }
            else if( ctx[ i ][ 1 ] instanceof List )
            {
                sb.append( "" + ctx[ i ][ 0 ] + ": " + buildLogableStringForList( (List) ctx[ i ][ 1 ] ) );
            }
            else
            {
                sb.append( "" + ctx[ i ][ 0 ] + ": " + ctx[ i ][ 1 ] );
            }
        }

        return sb.toString();
    }

    private static String buildLogableStringForList( List list )
    {
        StringBuilder sb = new StringBuilder( "[" );

        if( list != null )
        {
            int i = 0;
            for( Object obj : list )
            {
                sb.append( i++ > 0 ? "," : "" )
                    .append( obj == null ? "null" : obj.toString() );
            }
        }

        sb.append( "]" );

        return sb.toString();
    }

}
