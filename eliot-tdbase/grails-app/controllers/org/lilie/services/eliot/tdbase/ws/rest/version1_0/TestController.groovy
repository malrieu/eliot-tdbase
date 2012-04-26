package org.lilie.services.eliot.tdbase.ws.rest.version1_0

import grails.converters.JSON

class TestController {

    def index() {
        render ([
                seance: [
                        titre: "ma séance",
                        dateDebut: "12/12/2004"
                ]
        ] as JSON)
    }
}
