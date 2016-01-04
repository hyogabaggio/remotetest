package testremote

import grails.rest.Resource

//@Resource(uri='/users', formats=['json', 'xml'])
class Users {

    static hasMany = [cars: Car]

    Long id
    Long version

    Home home

    String nom
    String prenom
    String username
    String email
    String sexe
    Integer age
    Boolean actif = false
    Short sequence = 0

    byte[] photo

    Date dateCreated = new Date()
    Date dateNaissance


    static constraints = {
        age(blank: false, nullable: false, range: 1..65)
        username(blank: false, unique: true, size: 4..30)
        home(nullable: true)
        photo(nullable: true)
        email(blank: false, email: true)
        sexe(inList: ["H", "F"])
        sequence(nullable: true) // !!!!!!!!!! note: des erreurs de validation lorsque le type est Short pour les contraintes telles que range, min, max. Les arguments de ces contraintes sont evaluées en Integer, ce qui plante l'evaluation. Donc si obligatoire d'appliquer ces contraintes, mettre le type Integer
        dateNaissance(blank: false, validator: { value, usersInstance, errors ->
            if (!value || value > new Date()) {
                errors.rejectValue("dateNaissance", "agent.dateNaissance.before.today", "Veuillez verifier la date de naissance saisie")
                return false
            }
            return true
        })
    }
}
