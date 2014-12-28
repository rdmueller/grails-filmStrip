@Grapes([
    @Grab('org.asciidoctor:asciidoctor-java-integration:0.1.4'),
])
import org.asciidoctor.*
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import grails.converters.*

def pluginDir = GrailsPluginUtils.pluginInfos.find { it.name == "film-strip" }.pluginDir

target(githubIssues2AsciiDoc: "fetch github issues and render them as asciidoc") {
    
    def issues = JSON.parse(new URL('https://api.github.com/repos/rdmueller/grails-filmStrip/issues?state=open').text)
    new File('src/doc/generated').mkdirs()
    def out = new File('src/doc/generated/issues.adoc')
    out.write('''
[options="header",cols="1,1,10"]
|====
|ID    |State  | Description
''')
    issues.each { issue ->
        out.append("|${issue.url}[FS-${issue.number}] | ${issue.state} | ${issue.title}\n")
    }
    out.append('''|====
''')
}

target(generateHtml: "compile AsciiDoc to HTML") {
    githubIssues2AsciiDoc()
    
    def asciidoctor = Asciidoctor.Factory.create()
                      
    def output = asciidoctor.renderFile(
                                new File('src/doc/arc42-template.adoc'),
                                [
                                    'in_place':false,
                                    'backend':'html5',
                                    'header_footer':true,
                                    'safe':'SERVER',
                                    'attributes':[
                                        'toc-position':'left',
                                        'toc2':true,
                                        'numbered':true,
                                        'linkcss':false,
                                    ]
                                ]
                            )
    new File('target/doc/').mkdirs()
    new File('target/doc/architecture.html').write(output,'utf-8')									
    new File('../grails-filmStrip.io/index.html').write(output,'utf-8')									
    println "FilmStrip: finished generating docu at target/doc/architecture.html"									
}

setDefaultTarget(generateHtml)