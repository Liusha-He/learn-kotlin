package com.liusha.coursecatalogapi.course

import com.liusha.coursecatalogapi.course.model.CourseDTO
import com.liusha.coursecatalogapi.course.model.CourseEntity
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

fun courseDTO(
    id: Int? = null,
    name: String = "Build RestFul APis using Spring Boot and Kotlin",
    category: String = "Dilip Sundarraj",
    instructorId: Int? = 1
) = CourseDTO(
    id,
    name,
    category,
    instructorId
)

@WebMvcTest(CourseController::class)
@AutoConfigureWebTestClient
class CourseControllerUnitTest {
    @Autowired lateinit var webTestClient: WebTestClient
    @MockkBean lateinit var courseServiceMock: CourseService

    @Test
    fun createCourse() {
        val course = courseDTO()
        every { courseServiceMock.createCourse(any()) } returns courseDTO(id = 1)

        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(course)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue {
            savedCourseDTO!!.id != null
        }
    }

    @Test
    fun createCourse_validation() {
        val course = CourseDTO(id = null, name = "", category = "", instructorId = 1)
        every { courseServiceMock.createCourse(any()) } returns courseDTO(id = 1)

        val response = webTestClient
            .post()
            .uri("/v1/courses")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(course)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("courseDTO.category is required,courseDTO.name is required",
            response
        )
    }

    @Test
    fun createCourse_runtime_exception() {
        val course = CourseDTO(id = null, name = "test", category = "test", 1)
        val errorMessage = "unexpected error message"
        every { courseServiceMock.createCourse(any()) } throws RuntimeException(errorMessage)

        val response = webTestClient
            .post()
            .uri("/v1/courses")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(course)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(errorMessage, response)
    }

    @Test
    fun retrieveCourse() {
        every {
            courseServiceMock.retrieveAllCourses()
        }.returnsMany(
                listOf(
                    CourseDTO(1, "test 1", "dev 1"),
                    CourseDTO(2, "test 2", "dev 2"),
                    CourseDTO(3, "test 3", "dev 3")
                )
        )

        val courses = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(3, courses!!.size)
    }

    @Test
    fun updateCourse() {
        val course = CourseEntity(id = null, name = "test course 1", category = "test category")
        every {
            courseServiceMock.updateCourse(any(), any())
        } returns CourseDTO(id = 100, name = "updated course name", category = "test category")

        val updateCourseDTO = CourseDTO(id = null, name = "updated course name", category = "test category", 1)

        val updatedCourse = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", 100)
            .bodyValue(updateCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("updated course name", updatedCourse!!.name)
    }

    @Test
    fun deleteCourse() {
        every {
            courseServiceMock.deleteCourse(any())
        } just runs

        val deletedCourse = webTestClient.delete()
            .uri("/v1/courses/{courseId}", 100)
            .exchange()
            .expectStatus().isNoContent
    }
}
