
target(createFilmStrip: "Script to generate a better Test-Report for Spock-Geb Tests") {
    
    //https://github.com/damage-control/report/wiki/Sample-Reports
    
    echo "brush up the  spock/geb report"
        
    def path = "./target/test-reports/"
    def testResult = new File(path+"TESTS-TestSuites.xml").getText('utf-8')
    def xml = new XmlSlurper().parseText(testResult)
    def reportNum = 1
    def specs = [:]
    xml.testsuite.testcase.each {
        def spec = it.@classname.text()
        def testName = it.@name.text()
        specs[spec] = specs[spec]?:[:]
        def reports = []
        log.debug "spec: "+spec
        new File(path+"geb/"+spec.replaceAll("[.]","/")+"/.").eachFile {
            log.debug "name: ${it.name} - test: ${testName}"
            if (it.name.contains(testName)&&it.name.endsWith('.html')) {
                def name = (it.name-"-${testName}-"-".html")
                name = name.replaceAll("^[0-9]{3}-[0-9]{3}","")
                reports << [file:it.name,'name':name]
            }
        }
        specs[spec][testName] = reports
    }
    def xhtml = new StringWriter()
    new groovy.xml.MarkupBuilder(xhtml).html {
        head {
            title('Functional Test-Report')
            link(rel:'stylesheet', type:'text/css', href:'report.css')
        }
        body {
            div(id:'menu') {
                table(class:'level1') {
                    tr {
                        specs.eachWithIndex { spec, tests, i ->
                            th {
                                a(name:'spec'+i,class:'anchor') {
                                        a(href:'#spec'+(i-1),"<") 
                                    span(" ")
                                    a(href:'#spec'+(i+1),"> ") 
                                    a(target:'content',href:"../html/${i}_${spec}.html", "${i+1}. $spec")
                                }
                            }
                        }
                        tr {
                            specs.eachWithIndex { spec, tests, i ->
                                td {
                                    table(class:'level2') {
                                        tr {
                                            tests.each { test, reports ->
                                                td {
                                                    span("$test")
                                                    table(class:'level3') {
                                                        tr {
                                                            reports.each { report ->
                                                                //                                                                if (""+report.name!="end") {
                                                                td {
                                                                    span {
                                                                        a(target:'content',href:"${spec}/"+report.file, ""+report.name)
                                                                        br()
                                                                        a(class:'img',target:'content',href:"${spec}/"+(report.file.replaceAll('html$','png'))) {
                                                                            img(src:"${spec}/"+(report.file.replaceAll('html$','png')))
                                                                        }
                                                                    }
                                                                    //                                                                    }
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
                iframe(name:'content',src:"../html/all.html") {
                    
                }
            }
        }
    }
    new File("./target/test-reports/geb/geb_report.html").write(xhtml.toString())
    new File("./target/test-reports/geb/report.css").write("""


* { border: none; padding: 0px; margin: 0px;}
a.anchor {display: block; width: 100%;}
body {
	font-family: arial, sans-serif;
	padding: 0px;
	margin: 0px;
	overflow: none;
}
html {
	overflow: none;
}
th { min-width: 100%;}
div#menu {
	position:absolute;
	bottom: 0px;
	height: 30%;
	width: 100%;
	overflow: scroll;
	border-top: 1px solid black;
	padding: 0px;
	margin: 0px;
	min-height: 155px;
	background-color: rgba(255,255,255,0.9);
}
div#content {
	width: 100%;
	padding: 0px;
	margin: 0px;
	height: 70%
}    	
iframe {
	width: 100%;
	height: 100%;
}
th { 
	text-align: left; 
	font-size: 100%;
}
td {
	font-size: 80%;
	vertical-align: top;
}
a {
	text-decoration: none;
}
table.level2 td {
	font-size: 80%;
	width: 100px;
}    	
table.level3 span {
	position: relative;
	font-size: 80%;
	height: 80px;
	width: 100px;
	display: block;
	overflow: none;
}
table.level3 span a {
	position: absolute;
	bottom: 0px;
	z-index: 10;
	padding: 3px;
	width: 100px;
	overflow: none;
	display: block;
	background: rgba(255, 255, 255, 0.8)
}
table.level3 span a.img {
	position: absolute;
	top: 0px;
	width: 100px;
	max-width:100px;	
	height: 80px;
	z-index: 1;
}
table.level3 span a img {
	width: 100px;
	max-width:100px;	
}
"""
    )
    //now fix the html snapshots/reports
    new File("./target/test-reports/geb/.").eachDir() { dir ->
        dir.eachFileMatch(~/.*[.]html/) { file ->
            file.write(
                file.text
                .replaceAll(
                            '/aquagrails/static/',
                            '../../../../web-app/')
            )
            
        }

    }
    println "Film-Strip created at './target/test-reports/geb/geb_report.html'"
}

//setDefaultTarget(createFilmStrip)
