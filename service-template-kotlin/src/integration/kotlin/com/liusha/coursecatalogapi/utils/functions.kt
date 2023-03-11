package com.liusha.coursecatalogapi.utils

import com.liusha.coursecatalogapi.course.model.CourseEntity
import com.liusha.coursecatalogapi.instructor.model.InstructorEntity

fun courseEntityList(instructor: InstructorEntity? = null) = listOf(
    CourseEntity(
        id = null,
        name = "Build Restful APIs Using SpringBoot and Kotlin",
        category = "developer",
        instructor = instructor,
    ),
    CourseEntity(
        id = null,
        name =  "Build Restful APIs Using FastAPI and Python",
        category = "Liusha",
        instructor = instructor,
    ),
    CourseEntity(
        id = null,
        name = "Android Development for Beginners",
        category = "Degere",
        instructor = instructor,
    ),
)

fun instructorEntity(name: String = "Liusha") =
    InstructorEntity(id = null, name = name)