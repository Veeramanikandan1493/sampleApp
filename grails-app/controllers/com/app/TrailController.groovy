package com.app

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class TrailController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Trail.list(params), model:[trailCount: Trail.count()]
    }

    def show(Trail trail) {
        respond trail
    }

    def create() {
        respond new Trail(params)
    }

    @Transactional
    def save(Trail trail) {
        if (trail == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (trail.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond trail.errors, view:'create'
            return
        }

        trail.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'trail.label', default: 'Trail'), trail.id])
                redirect trail
            }
            '*' { respond trail, [status: CREATED] }
        }
    }

    def edit(Trail trail) {
        respond trail
    }

    @Transactional
    def update(Trail trail) {
        if (trail == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (trail.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond trail.errors, view:'edit'
            return
        }

        trail.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'trail.label', default: 'Trail'), trail.id])
                redirect trail
            }
            '*'{ respond trail, [status: OK] }
        }
    }

    @Transactional
    def delete(Trail trail) {

        if (trail == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        trail.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'trail.label', default: 'Trail'), trail.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'trail.label', default: 'Trail'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
