package com.liusha.coursecatalogapi.course.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CourseRepo: CrudRepository<CourseEntity, Int> {
    fun findByNameContaining(courseName: String): List<CourseEntity>

    @Query(value = "SELECT * FROM COURSES where name like %?1%", nativeQuery = true)
    fun findCoursesByName(courseName: String): List<CourseEntity>
}
