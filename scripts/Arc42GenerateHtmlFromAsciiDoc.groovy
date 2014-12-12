@Grapes([
    @Grab('org.asciidoctor:asciidoctor-java-integration:0.1.4'),
])
import org.asciidoctor.*

target(generateHtml: "compile AsciiDoc to HTML") {

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
    println "finished generating docu at target/doc/architecture.html"									
}

setDefaultTarget(generateHtml)