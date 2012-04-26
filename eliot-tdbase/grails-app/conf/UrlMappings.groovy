class UrlMappings {

  static mappings = {
      "/ws/rest/1.0/$controller/$action?/$id?" {
          constraints {
              // apply constraints here
          }
      }

    "/$controller/$action?/$id?" {
      constraints {
        // apply constraints here
      }
    }



    "/"(controller: "accueil")


    "500"(view: '/error')
  }
}
