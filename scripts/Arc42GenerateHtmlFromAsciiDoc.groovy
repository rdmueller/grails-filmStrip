import grails.converters.JSON
import org.asciidoctor.Asciidoctor

target(githubIssues2AsciiDoc: "Fetch GitHub issues and render them as AsciiDoc") {

    def issues = JSON.parse(new URL('https://api.github.com/repos/rdmueller/grails-filmStrip/issues?state=open').text)

    def generated = new File('src/doc/generated')
    generated.mkdirs()

    def out = new File(generated, 'issues.adoc')

    out.withWriter { writer ->

        writer.writeLine('''
[options="header",cols="1,1,10"]
|====
|ID    |State  | Description''')

        issues.each { writer.writeLine << "|${it.html_url}[FS-${it.number}] | ${it.state} | ${it.title}" }

        writer.writeLine('|====')
    }
}

target(generateHtml: "compile AsciiDoc to HTML") {
    githubIssues2AsciiDoc()

    def asciidoctor = Asciidoctor.Factory.create()

    def output = asciidoctor.renderFile(new File('src/doc/arc42-template.adoc'), [
        in_place:false,
        backend:'html5',
        header_footer:true,
        safe:'SERVER',
        attributes:[
            'toc-position':'left',
            toc2:true,
            numbered:true,
            linkcss:false,
        ]
    ])

    def targetDocs = new File('target/doc')
    targetDocs.mkdirs()

    new File(targetDocs, 'architecture.html').write(output,'utf-8')
    new File('../grails-filmStrip.io/index.html').write(output,'utf-8')

    println "FilmStrip: finished generating docu at target/doc/architecture.html"
}

setDefaultTarget(generateHtml)
