package testremote

import org.codehaus.groovy.grails.validation.ConstrainedProperty

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CarController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    static responseFormats = ['json', 'xml']  //si on veut avoir du json en priorité

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Car.list(params), model: [carInstanceCount: Car.count()]
    }

    def getmetadata() {
        //  def usersClass = grailsApplication.getDomainClass('testremote.Users')
        def carClass = grailsApplication.getDomainClass('testremote.Car')

        render(contentType: "application/json") {
            domain {
                for (property in carClass.persistentProperties) {
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
                    for (constraint in new Car().constraints) {
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


    def show(Car carInstance) {
        respond carInstance
    }

    def create() {
        respond new Car(params)
    }

    @Transactional
    def save(Car carInstance) {
        if (carInstance == null) {
            notFound()
            return
        }

        if (carInstance.hasErrors()) {
            respond carInstance.errors, view: 'create'
            return
        }

        carInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'car.label', default: 'Car'), carInstance.id])
                redirect carInstance
            }
            '*' { respond carInstance, [status: CREATED] }
        }
    }

    def edit(Car carInstance) {
        respond carInstance
    }

    @Transactional
    def update(Car carInstance) {
        if (carInstance == null) {
            notFound()
            return
        }

        if (carInstance.hasErrors()) {
            respond carInstance.errors, view: 'edit'
            return
        }

        carInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Car.label', default: 'Car'), carInstance.id])
                redirect carInstance
            }
            '*' { respond carInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Car carInstance) {

        if (carInstance == null) {
            notFound()
            return
        }

        carInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Car.label', default: 'Car'), carInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'car.label', default: 'Car'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
