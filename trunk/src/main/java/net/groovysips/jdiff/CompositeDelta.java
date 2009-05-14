package net.groovysips.jdiff;

import java.util.List;

/**
 * Delta that may contain child deltas.
 *
 * @author Shneyderman
 */
public interface CompositeDelta extends Delta
{

    List<Delta> children();

}
