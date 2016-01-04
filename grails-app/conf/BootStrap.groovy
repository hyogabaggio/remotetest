import testremote.Home
import testremote.Users
import testremote.Car

class BootStrap {

    def init = { servletContext ->

        def homeInstance = new Home(
                adresse: "15, almadies"
        ).save(failOnError: true)

        def hyoga = new Users(
                nom: "baggio",
                prenom: "hyoga",
                username: "hyoga",
                age: 22,
                email: "c.hyoga@gmail.com",
                home: homeInstance,
                sexe: "H",
                dateNaissance: Date.parse("yyyy-MM-dd", "1993-02-18")
        ).save(failOnError: true)

        new Users(
                nom: "house",
                prenom: "greg",
                username: "house",
                age: 45,
                email: "house@gmail.com",
                sexe: "H",
                dateNaissance: Date.parse("yyyy-MM-dd", "1974-02-18")
        ).save(failOnError: true)


        new Car(
                marque: "mini cooper",
                model: "clubman",
                couleur: "noir metallisé",
                matricule: "dk-9845-BA",
                owner: hyoga
        ).save(failOnError: true)
    }
    def destroy = {
    }
}
