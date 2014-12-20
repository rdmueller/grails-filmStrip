import grails.converters.*

target(convertJsonReport: "Script to convert ouput from geb.ReportingListener into something structured") {
    def reportsDir = "target/test-reports/geb/"
    def thisPath = new File('.').canonicalPath.replaceAll('\\\\','/')
    def gebReports = new File(reportsDir+"gebReportInfo.json").text.replaceAll('\\\\','/')
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
    def newJson = allReports as JSON
    newJson.prettyPrint = true
    new File(reportsDir+"gebReportInfo2.json").write(newJson.toString())

}