package net.groovysips.jdiff;

import java.beans.PropertyDescriptor;

/**
 * // TODO (Shneyderman - May 11, 2009) : needs better description.
 * Delta builder that buildes delta by calculating the diffs of the values of the java bean's properties.
 *
 * @author Shneyderman
 */
public interface DeltaBuilder
{

    public static final PropertyDescriptor[] EMPTY_PROPERTY_DESCRIPTORS_ARRAY = new PropertyDescriptor[0];

    /**
     * Calculates delta between two java beans.
     *
     * @param original java bean
     * @param modified java bean
     * @return delta
     */
    Delta build( Object original, Object modified );

    /**
     * Calculates delta between two java beans that can trigger an assignment of the result to a property described by
     * propertyDescriptor argument.
     *
     * @param original
     * @param modified
     * @param propertyDescriptor
     * @return delta
     */
    Delta build( Object original, Object modified, PropertyDescriptor propertyDescriptor);

}
