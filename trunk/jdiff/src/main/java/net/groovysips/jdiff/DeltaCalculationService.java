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

/**
 * @author Alex Shneyderman
 * @since 0.3
 */
public interface DeltaCalculationService
{

    /**
     * <p>
     * Calculate graph diff which if applied to the original will produce an instance that
     * will be "equal" to the modified instance.
     * </p>
     *
     * @param original - original instance
     * @param modified - modified/target instance
     * @return - delta
     */
    Delta diff( Object original, Object modified );

    /**
     * Applies delta to the object in the argument.
     *
     * @param delta to apply
     * @return object
     */
    Object apply( Object object, Delta delta );

}
