package net.groovysips.jdiff;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.LogManager;
import java.io.StringReader;

/**
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.5
 */
public class TestLog4jConfigurator
{

    public static String configXml_DEBUG =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "<!DOCTYPE log4j:configuration SYSTEM 'log4j.dtd'>\n" +
        "<log4j:configuration>\n" +
        "    <appender name='stdout' class='org.apache.log4j.ConsoleAppender'>\n" +
        "        <layout class='org.apache.log4j.PatternLayout'>\n" +
        "            <param name='ConversionPattern' value='%m%n'/>\n" +
        "        </layout>\n" +
        "    </appender>\n" +
        "    <root>\n" +
        "        <level value='DEBUG'/>\n" +
        "        <appender-ref ref='stdout'/>\n" +
        "    </root>\n" +
        "</log4j:configuration>";

    public static void configure( )
    {
	    configure( configXml_DEBUG );
    }

    public static void configure( String configXml )
    {
	    DOMConfigurator domConfigurator = new DOMConfigurator();
	    domConfigurator.doConfigure(new StringReader(configXml), LogManager.getLoggerRepository());
    }
}
