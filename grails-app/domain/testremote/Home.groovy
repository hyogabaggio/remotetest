package testremote

class Home {

    Long id
    Long version

    String adresse
    String rue
    String etages="O"
    String bp

    static constraints = {
        adresse(blank: false)
        rue(nullable: true)
        etages(inList: ["O", "N"])
        bp(nullable: true, validator: { value, homeInstance, errors ->
            if (!value && !homeInstance.adresse) {
                errors.rejectValue("bp", "home.bp.not.nullable", "Renseignez la boite postale")
                return false
            }
            return true
        })
    }

    String toString() {
        return "${adresse}"
    }
}
