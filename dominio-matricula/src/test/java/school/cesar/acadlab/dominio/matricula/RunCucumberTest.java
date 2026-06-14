package school.cesar.acadlab.dominio.matricula;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("school.cesar.acadlab.dominio.matricula")
public class RunCucumberTest {
}
