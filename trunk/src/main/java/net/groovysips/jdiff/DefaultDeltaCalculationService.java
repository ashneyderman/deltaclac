package net.groovysips.jdiff;

/**
 * @author Shneyderman
 */
public class DefaultDeltaCalculationService implements DeltaCalculationService
{

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
        return deltaBuilder.build( original, modified );
    }

    public Object apply( Object object, Delta delta )
    {
        return merger.merge( object, delta );
    }

}
