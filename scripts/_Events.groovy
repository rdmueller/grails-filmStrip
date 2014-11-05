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
//includeTargets << new File('./scripts/CreateFilmStrip.groovy')
//includeTargets << new File("${arc42PluginDir}/scripts/_Arc42GenerateHtmlFromAsciiDoc.groovy")
eventTestProduceReports = { name ->
    System.out.println ">"*80
    System.out.println "produceReports "+name
    System.out.println ">"*80
}
eventTestPhaseEnd = { name ->
    System.out.println ">"*80
    System.out.println "phaseEnd "+name
    System.out.println ">"*80
}
eventTestPhasesEnd = { name ->
    System.out.println ">"*80
    System.out.println "phasesEnd "+name
    System.out.println ">"*80
}
eventTestSuiteEnd = { name ->
    System.out.println ">"*80
    System.out.println "! ${filmstripPluginDir}"
    System.out.println "! ${grails-filmStripPluginDir}"
    if (name.toLowerCase()=='spock') {
    System.out.println ">"*80
    System.out.println name
    System.out.println ">"*80
    createFilmStrip()
    }
}