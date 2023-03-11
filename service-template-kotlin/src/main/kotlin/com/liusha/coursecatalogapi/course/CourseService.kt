package com.liusha.coursecatalogapi.course

import com.liusha.coursecatalogapi.course.model.CourseDTO
import com.liusha.coursecatalogapi.course.model.CourseEntity
import com.liusha.coursecatalogapi.course.model.CourseRepo
import com.liusha.coursecatalogapi.instructor.InstructorNotFoundException
import com.liusha.coursecatalogapi.instructor.InstructorService
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CourseService(val courseRepo: CourseRepo, val instructorService: InstructorService)
{
    companion object: KLogging()

    fun createCourse(course: CourseDTO): CourseDTO
    {
        val instructor = instructorService.findByInstructorId(course.instructorId!!)

        if (!instructor.isPresent) {
            throw InstructorNotFoundException("instructorId - ${course.instructorId} is not valid...")
        }

        val courseEntity: CourseEntity = course.let {
            CourseEntity(null, it.name, it.category, instructor.get())
        }
        courseRepo.save(courseEntity)

        logger.info("Saved course is: $courseEntity")

        return courseEntity.let {
            CourseDTO(it.id, it.name, it.category, it.instructor?.id)
        }
    }

    fun retrieveAllCourses(courseName: String? = null): List<CourseDTO>
    {
        val courses = courseName?.let {
            courseRepo.findCoursesByName(courseName)
        } ?: courseRepo.findAll()
        return courses.map {
            CourseDTO(
                it.id,
                it.name,
                it.category,
                it.instructor!!.id
            )
        }
    }

    fun updateCourse(course: CourseDTO, courseId: Int): CourseDTO
    {
        val existingCourse = courseRepo.findById(courseId)

        return if (existingCourse.isPresent) {
            existingCourse.get().let {
                it.name = course.name
                it.category = course.category
                courseRepo.save(it)
                CourseDTO(
                    id = it.id,
                    name = it.name,
                    category = it.category,
                    instructorId = it.instructor!!.id
                )
            }
        } else {
            throw CourseNotFoundException("no course found fo course id $courseId")
        }
    }

    fun deleteCourse(courseId: Int)
    {
        val existingCourse = courseRepo.findById(courseId)

        return if (existingCourse.isPresent) {
            existingCourse.get().let {
                courseRepo.deleteById(courseId)
            }
        } else {
            throw CourseNotFoundException("no course found fo course id $courseId")
        }
    }
}