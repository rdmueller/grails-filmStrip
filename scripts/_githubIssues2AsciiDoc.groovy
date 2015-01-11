import grails.converters.*

target(githubIssues2AsciiDoc: "fetch github issues and render them as asciidoc") {

    def issues = JSON.parse(new URL('https://api.github.com/repos/rdmueller/grails-filmStrip/issues?state=open').text)
    def out = new File('target/doc/issues.adoc')
    out.write('''
[options="header",cols="1,1,10"]
|====
''')
    issues.each { issue ->
        out.append("|${issue.url}[FS-${issue.number}] | ${issue.state} | ${issue.title}\n")
    }
    out.write('''|====
''')
}
setDefaultTarget(githubIssues2AsciiDoc)