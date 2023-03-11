package com.liusha.coursecatalogapi.instructor.model

import com.liusha.coursecatalogapi.course.model.CourseEntity
import javax.persistence.*

@Entity
@Table(name = "instructors")
data class InstructorEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?,
    var name: String,
    @OneToMany(
        mappedBy = "instructor",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var courses: List<CourseEntity> = mutableListOf()
)
