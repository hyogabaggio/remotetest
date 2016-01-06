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
        "/car/metadata"(controller: "car") {
            action = [GET: "getmetadata"]
        }
        "/home"(resources: "home")
        "/home/metadata"(controller: "home") {
            action = [GET: "getmetadata"]
        }

        "/"(view: "/index")
        "500"(view: '/error')
    }
}
