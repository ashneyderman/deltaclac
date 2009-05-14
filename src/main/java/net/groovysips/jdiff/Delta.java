package net.groovysips.jdiff;

public interface Delta
{

    /**
     * @param visitor
     */
    void visit( DeltaVisitor visitor );

    public static final Delta NULL = new Delta()
    {
        public void visit( DeltaVisitor visitor )
        {
            visitor.visit( this );
        }

        @Override public String toString()
        {
            return "Delta.NULL";
        }
    };

}