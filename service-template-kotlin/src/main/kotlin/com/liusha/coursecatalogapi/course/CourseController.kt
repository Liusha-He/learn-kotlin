package com.liusha.coursecatalogapi.course

import com.liusha.coursecatalogapi.course.model.CourseDTO
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/courses")
@Validated
class CourseController(val courseService: CourseService)
{
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCourse(@RequestBody @Valid course: CourseDTO): CourseDTO =
        courseService.createCourse(course)

    @GetMapping
    fun retrieveAllCourses(@RequestParam("course_name", required = false) courseName: String?): List<CourseDTO> =
        courseService.retrieveAllCourses(courseName)

    @PutMapping("/{course_id}")
    fun updateCourse(@RequestBody @Valid course: CourseDTO, @PathVariable("course_id") courseId: Int): CourseDTO =
        courseService.updateCourse(course, courseId)

    @DeleteMapping("/{course_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourse(@PathVariable("course_id") courseId: Int) =
        courseService.deleteCourse(courseId)
}
