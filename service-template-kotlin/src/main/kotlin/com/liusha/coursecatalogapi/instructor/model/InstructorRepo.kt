package com.liusha.coursecatalogapi.instructor.model

import org.springframework.data.repository.CrudRepository

interface InstructorRepo: CrudRepository<InstructorEntity, Int> {
    fun findInstructorByName(name : String) : InstructorEntity
}
