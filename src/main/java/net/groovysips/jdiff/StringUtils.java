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
import java.util.Stack;

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
            else if(  ctx[ i ][ 1 ] instanceof Stack )
            {
               sb.append( "" + ctx[ i ][ 0 ] + ": " + buildLogableStringForStack( (Stack) ctx[ i ][ 1 ] ) );
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

    /**
     * Makes the frist letter of the string capital, leaving the rest of the string intact.
     *
     * @param s - string to capitalize
     * @return capitalized argument.
     */
    public static final String capitalize( final String s )
    {
        if(s == null || s.trim().equals( "" ))
        {
            return s;
        }

        if(s.length() > 1)
        {
            return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 );
        }
        else
        {
            return s.toUpperCase();
        }
    }

    // HELPERS
    private static String buildLogableStringForList( List list )
    {
        StringBuilder sb = new StringBuilder( "\n[" );

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

    private static String buildLogableStringForStack( Stack stack )
    {
        StringBuilder sb = new StringBuilder( "[" );

        if( stack == null )
        {
            sb.append( "null" );
        }
        else if( stack.isEmpty() )
        {
            sb.append( "empty" );
        }
        else
        {
            int i = 0;
            for( Object obj : stack )
            {
                sb.append( i++ > 0 ? "," : "" )
                    .append( obj == null ? "null" : obj.toString() );
            }
        }

        sb.append( "]" );

        return sb.toString();
    }


}
