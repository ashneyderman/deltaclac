package net.groovysips.jdiff;

/**
 * @author Shneyderman
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
    Object apply( Object object, Delta delta);

}
