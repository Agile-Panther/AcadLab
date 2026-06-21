package school.cesar.acadlab.dominio.permanenciaacademica;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("school.cesar.acadlab.dominio.permanenciaacademica")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "school.cesar.acadlab.dominio.permanenciaacademica")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunPermanenciaCucumberTest {
}
