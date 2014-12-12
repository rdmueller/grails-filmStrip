/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 * @author Ralf D. Müller
 */
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils

def pluginDir = GrailsPluginUtils.pluginInfos.find { it.name == "film-strip" }.pluginDir

includeTargets << new File("${pluginDir}/scripts/CreateFilmStrip.groovy")
includeTargets << new File("${pluginDir}/scripts/_convertJsonFilmStripReport.groovy")

eventTestProduceReports = { 
    println "eventTestProduceReports "
    convertJsonReport()
    createFilmStrip()
}
eventTestPhasesStart = { name ->
    println "eventTestPhasesStart "+name
    println "monkey patch for geb.report.Reporter"
    geb.report.ReporterSupport
        .metaClass.'static'.toTestReportLabel={
                int testCounter, 
                int reportCounter, 
                String methodName, 
                String label ->
        //escape dashes...
        return "${testCounter}-${reportCounter}-${methodName.replaceAll('-','--')}-${label.replaceAll('-','--')}"
    }
}
eventTestPhaseEnd = { name ->
    println "eventTestPhaseEnd "+name
}
eventTestPhasesEnd = { 
    println "eventTestPhasesEnd "
}
eventTestSuiteStart = { name ->
    println "testSuiteStart: ${name}"
    
}
eventTestSuiteEnd = { name ->
    println "eventTestSuiteEnd "+name
}