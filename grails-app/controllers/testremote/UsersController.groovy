package testremote

import grails.converters.JSON
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.validation.ConstrainedProperty

import static org.springframework.http.HttpStatus.*
import static org.springframework.http.HttpMethod.*

import grails.transaction.Transactional

@Transactional(readOnly = true)
class UsersController {


    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    static responseFormats = ['json', 'xml']  //si on veut avoir du json en priorité

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        //TODO renvoyer le toString dans le json en cas de jointure
        respond Users.list(params), model: [usersInstanceCount: Users.count()]
    }

    def getmetadata() {
        //  def usersClass = grailsApplication.getDomainClass('testremote.Users')
        def usersClass = grailsApplication.getDomainClass('testremote.Users')
        // log.info("usersClass constrainedProperties = "+usersClass.constrainedProperties)
        //  log.info("usersClass constrainedProperties = "+usersClass.constrainedProperties.class)
        //  log.info("usersClass persistentProperties = " + usersClass.persistentProperties)
        /* usersClass.persistentProperties.each {
             log.info("property = " + it)
             log.info("property name = " + it.name)
             log.info("property type = " + it.type)
             log.info("property class = " + it.type.toString().split(" ")[1])
         } */

        /* usersClass.constrainedProperties.each {
             log.info("constrainedProperties : ${it.key} = "+it.value)

         }
         new Users().constraints.each {
             log.info("constraints = "+it)
         }   */

        /*   render(contentType: "application/json") {
               users {
                   for (property in usersClass.persistentProperties) {

                       def associationType = ""
                       if (property.isOneToMany()) associationType = "one-to-many"
                       else if (property.hasOne) associationType = "has-one"
                       else if (property.manyToMany) associationType = "many-to-many"
                       else if (property.manyToOne) associationType = "many-to-one"
                       else if (property.oneToMany) associationType = "one-to-many"
                       else if (property.oneToOne) associationType = "one-to-one"

                       "${property.name}"(type: property.typePropertyName,
                               optional: property.optional,
                               associationType: associationType
                       )
                   }
               }
           }         */

        // log.info("constaints = " + constaints)
        /* constaints.each { it ->
             log.info("it key = " + it.key)
             ConstrainedProperty property = it.value
             log.info("property = " + property.properties)
             property.properties.each { prop ->
                 log.info("prop key = " + prop.key)
                 log.info("prop value = " + prop.value)

             }

             // map.put(it.key, )
             //  log.info("it adresse appliedConstraints = "+it.appliedConstraints)
         }  */



        render(contentType: "application/json") {
            domain {
                for (property in usersClass.persistentProperties) {
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
                    for (constraint in new Users().constraints) {
                        ConstrainedProperty property = constraint.value

                        def validproperty = property.properties.findAll {
                            it.value != null && it.value != "null" && it.key != "messageSource" && it.key != "appliedConstraints"
                        }
                        def nopeproperty = property.properties.findAll {
                            it.key == "appliedConstraints"
                        }
                        //  log.info("nopeproperty = " + nopeproperty)
                        "${constraint.key}"(validproperty)
                    }
                }
            }

        }

    }


    def show(Users usersInstance) {
        respond usersInstance
    }

    def create() {
        respond new Users(params)
    }

    @Transactional
    def save(Users usersInstance) {
        if (usersInstance == null) {
            notFound()
            return
        }

        if (usersInstance.hasErrors()) {
            respond usersInstance.errors, view: 'create'
            return
        }

        usersInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'users.label', default: 'Users'), usersInstance.id])
                redirect usersInstance
            }
            '*' { respond usersInstance, [status: CREATED] }
        }
    }

    def edit(Users usersInstance) {
        respond usersInstance
    }

    @Transactional
    def update(Users usersInstance) {
        if (usersInstance == null) {
            notFound()
            return
        }

        if (usersInstance.hasErrors()) {
            respond usersInstance.errors, view: 'edit'
            return
        }

        usersInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Users.label', default: 'Users'), usersInstance.id])
                redirect usersInstance
            }
            '*' { respond usersInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Users usersInstance) {

        if (usersInstance == null) {
            notFound()
            return
        }

        usersInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Users.label', default: 'Users'), usersInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'users.label', default: 'Users'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }


}
