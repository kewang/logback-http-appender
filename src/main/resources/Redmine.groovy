@Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.7.1")
import groovyx.net.http.RESTClient

import static groovyx.net.http.ContentType.JSON

def redmine = new RESTClient('http://example.com')

def resp = redmine.post(
        path: "/issues.json",
        headers: [
                "X-Redmine-API-Key": ""
        ],
        body: [
                issue: [
                        project_id : -1,
                        subject    : "HELLO",
                        description: "WORLD"
                ]
        ],
        requestContentType: JSON
)

println resp