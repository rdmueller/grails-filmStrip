/*
 * Copyright 2014, 2015 the original author or authors.
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


import groovy.json.JsonOutput
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import geb.report.ReporterSupport
import grails.converters.JSON
import groovy.xml.MarkupBuilder

/**
 *
 * @author Ralf D. Müller
 */
def pluginDir = GrailsPluginUtils.pluginInfos.find { it.name == "film-strip" }?.pluginDir.file.canonicalPath
if (!pluginDir) {
    pluginDir = filmStripPluginDir
}

//includeTargets << new File(pluginDir, "scripts/CreateFilmStrip.groovy")
//includeTargets << new File(pluginDir, "scripts/_convertJsonFilmStripReport.groovy")

//Script to convert ouput from geb.ReportingListener into something structured
def convertJsonReport = {
    def reportsDir = "target/test-reports/geb/"
    def thisPath = new File('.').canonicalPath.replaceAll('\\\\','/')
    def gebReports = new File(reportsDir, "gebReportInfo.json").text.replaceAll('\\\\','/')
    def allReports = [specs:[]]

    gebReports.eachLine { json ->
        def reportLine = JSON.parse(json)
        if (!(reportLine.spec.label in allReports.specs.label)) {
            allReports.specs << [label:reportLine.spec.label,tests:[]]
        }
        def spec = allReports.specs.find { spec -> spec.label==reportLine.spec.label }
        if (!(reportLine.spec.test.num in spec.tests.num)) {
            spec.tests << [
                num:reportLine.spec.test.num,
                label: reportLine.spec.test.label,
                reports: []
            ]
        }
        def test = spec.tests.find { test -> test.num==reportLine.spec.test.num }
        if (!(reportLine.spec.test.report.num in test.reports.num)) {
            test.reports << [
                num:reportLine.spec.test.report.num,
                label:reportLine.spec.test.report.label,
                url:reportLine.spec.test.report.url,
                files:[]
            ]
        }
        def report = test.reports.find { report -> report.num==reportLine.spec.test.report.num }
        if (!(reportLine.spec.test.report.files in report.files)) {
            report.files += reportLine.spec.test.report.files.collect{"."+it.replaceAll('//','/')-thisPath-reportsDir}
        }
    }

    def newJson = JsonOutput.toJson(allReports)
    new File(reportsDir, "gebReportInfo2.json").write(JsonOutput.prettyPrint(newJson))
}
//Script to generate a better Test-Report for Spock-Geb Tests
def createFilmStrip= {

    //https://github.com/damage-control/report/wiki/Sample-Reports

    println "FilmStrip: create Film-Strip"

    def reportsDir = "./target/test-reports/geb"

    def gebReports = new File(reportsDir, "gebReportInfo2.json").text.replaceAll('\\\\','/')
    gebReports = JSON.parse(gebReports)

    def xhtml = new StringWriter()
    new MarkupBuilder(xhtml).html {
        head {
            title('Functional Test-Report')
            link(rel:'stylesheet', type:'text/css', href:'report.css')
            meta('http-equiv':'Content-Type', content:'text/html; charset=utf-8')
        }
        body {
            div(id:'menu') {
                table(class:'level1') {
                    tr {
                        gebReports.specs.eachWithIndex { spec, i ->
                            th {
                                a(name:'spec'+i,class:'anchor') {
                                    a(href:'#spec'+(i-1),"<")
                                    span(" ")
                                    a(href:'#spec'+(i+1),"> ")
                                    a(target:'content',href:"../html/${i}_${spec.label}.html", "${i+1}. ${spec.label}")
                                }
                            }
                        }
                        tr {
                            gebReports.specs.eachWithIndex { spec, i ->
                                td {
                                    table(class:'level2') {
                                        tr {
                                            spec.tests.each { test ->
                                                td {
                                                    span("${test.label}")
                                                    table(class:'level3') {
                                                        tr {
                                                            test.reports.each { report ->
                                                                if (report.label!="end") {
                                                                    td {
                                                                        span {
                                                                            a(class:'report',target:'content',href:report.files.find{it.endsWith('html')}, ""+report.label)
                                                                            a(class:'url',target:'content',href:report.url, "*")
                                                                            br()
                                                                            a(class:'img',target:'content',href:(report.files.find{it.endsWith('png')})) {
                                                                                img(src:(report.files.find{it.endsWith('.png')}))
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            div(id:'content') {
                iframe(name:'content',src:"../html/all.html")
            }
        }
    }

    File reportFile = new File(reportsDir, "geb_report.html")
    reportFile.write(xhtml.toString())

    new File("$reportsDir/report.css").write(
        new File(pluginDir, '/web-app/css/report.css').text
    )

    println "FilmStrip: created at '$reportFile.path'"
}

eventTestProduceReports = {
    println "eventTestProduceReports "
    convertJsonReport()
    createFilmStrip()
}

eventTestPhasesStart = { name ->
    println "eventTestPhasesStart "+name
    println "monkey patch for geb.report.Reporter"
    ReporterSupport.metaClass.static.toTestReportLabel={
                int testCounter,
                int reportCounter,
                String methodName,
                String label ->
        //escape dashes...
        "${testCounter}-${reportCounter}-${methodName.replaceAll('-','--')}-${label.replaceAll('-','--')}"
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
