class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "/users"(resources: "users")
        "/users/metadata"(controller: "users") {
            action = [GET: "getmetadata"]
        }
        "/car"(resources: "car")
        "/home"(resources: "home")

        "/"(view: "/index")
        "500"(view: '/error')
    }
}
