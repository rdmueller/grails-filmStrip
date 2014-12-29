import grails.converters.JSON
import groovy.xml.MarkupBuilder

target(createFilmStrip: "Script to generate a better Test-Report for Spock-Geb Tests") {

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

    new File("./target/test-reports/geb/report.css").write(
        new File(filmStripPluginDir, 'web-app/css/report.css').text
    )

    println "FilmStrip: created at '$reportFile.path'"
}

//setDefaultTarget(createFilmStrip)
