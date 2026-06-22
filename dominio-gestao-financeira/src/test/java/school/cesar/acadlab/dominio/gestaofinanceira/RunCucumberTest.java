package school.cesar.acadlab.dominio.gestaofinanceira;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("school.cesar.acadlab.dominio.gestaofinanceira")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/cucumber.html")
public class RunCucumberTest {}
