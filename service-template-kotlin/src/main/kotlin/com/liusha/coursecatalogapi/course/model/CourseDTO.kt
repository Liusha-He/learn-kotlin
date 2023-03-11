package com.liusha.coursecatalogapi.course.model

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CourseDTO(
    val id: Int?,
    @get:NotBlank(message = "courseDTO.name is required") val name: String,
    @get:NotBlank(message = "courseDTO.category is required") val category: String,
    @get:NotNull(message = "courseDTO.instructorId is required")
    val instructorId: Int? = null,
)
