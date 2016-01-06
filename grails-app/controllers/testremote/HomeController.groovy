package testremote

import org.codehaus.groovy.grails.validation.ConstrainedProperty

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class HomeController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    static responseFormats = ['json', 'xml']  //si on veut avoir du json en priorité

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        //TODO renvoyer le toString dans le json en cas de jointure
        respond Home.list(params), model: [homeInstanceCount: Home.count()]
    }

    def getmetadata() {
        //  def usersClass = grailsApplication.getDomainClass('testremote.Users')
        def homeClass = grailsApplication.getDomainClass('testremote.Home')

        render(contentType: "application/json") {
            domain {
                for (property in homeClass.persistentProperties) {
                    def associationType = ""
                    if (property.isOneToMany()) associationType = "one-to-many"
                    else if (property.hasOne) associationType = "has-one"
                    else if (property.manyToMany) associationType = "many-to-many"
                    else if (property.manyToOne) associationType = "many-to-one"
                    else if (property.oneToMany) associationType = "one-to-many"
                    else if (property.oneToOne) associationType = "one-to-one"

                    def type = property.type.toString().split(" ")[1]
                    if (type.contains('[')) type = property.typePropertyName

                    "${property.name}"(class: type, optional: property.optional,
                            associationType: associationType)
                }
                constraints {
                    for (constraint in new Home().constraints) {
                        ConstrainedProperty property = constraint.value

                        def validproperty = property.properties.findAll {
                            it.value != null && it.value != "null" && it.key != "messageSource" && it.key != "appliedConstraints"
                        }
                        /*  def nopeproperty = property.properties.findAll {
                              it.key == "appliedConstraints"
                          }   */
                        //  log.info("nopeproperty = " + nopeproperty)
                        "${constraint.key}"(validproperty)
                    }
                }
            }

        }

    }


    def show(Home homeInstance) {
        respond homeInstance
    }

    def create() {
        respond new Home(params)
    }

    @Transactional
    def save(Home homeInstance) {
        if (homeInstance == null) {
            notFound()
            return
        }

        if (homeInstance.hasErrors()) {
            respond homeInstance.errors, view: 'create'
            return
        }

        homeInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'home.label', default: 'Home'), homeInstance.id])
                redirect homeInstance
            }
            '*' { respond homeInstance, [status: CREATED] }
        }
    }

    def edit(Home homeInstance) {
        respond homeInstance
    }

    @Transactional
    def update(Home homeInstance) {
        if (homeInstance == null) {
            notFound()
            return
        }

        if (homeInstance.hasErrors()) {
            respond homeInstance.errors, view: 'edit'
            return
        }

        homeInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Home.label', default: 'Home'), homeInstance.id])
                redirect homeInstance
            }
            '*' { respond homeInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Home homeInstance) {

        if (homeInstance == null) {
            notFound()
            return
        }

        homeInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Home.label', default: 'Home'), homeInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'home.label', default: 'Home'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
