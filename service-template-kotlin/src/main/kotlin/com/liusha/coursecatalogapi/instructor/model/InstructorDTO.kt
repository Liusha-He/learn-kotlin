package com.liusha.coursecatalogapi.instructor.model

import javax.validation.constraints.NotBlank

data class InstructorDTO(
    val id: Int?,
    @get:NotBlank(message = "instructor name is required")
    var name: String
)
