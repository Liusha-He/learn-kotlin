package com.liusha.coursecatalogapi.course

import com.liusha.coursecatalogapi.course.model.CourseDTO
import com.liusha.coursecatalogapi.course.model.CourseEntity
import com.liusha.coursecatalogapi.course.model.CourseRepo
import com.liusha.coursecatalogapi.instructor.model.InstructorEntity
import com.liusha.coursecatalogapi.instructor.model.InstructorRepo
import com.liusha.coursecatalogapi.utils.courseEntityList
import com.liusha.coursecatalogapi.utils.instructorEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Testcontainers
class CourseControllerIntegrationTest {
    @Autowired lateinit var webTestClient: WebTestClient
    @Autowired lateinit var courseRepo: CourseRepo
    @Autowired lateinit var instructorRepo: InstructorRepo

    companion object {
        @Container
        val postgresDB = PostgreSQLContainer<Nothing>(
            DockerImageName.parse("postgres:13-alpine")
        ).apply {
            withDatabaseName("testdb")
            withUsername("postgres")
            withPassword("secret")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresDB::getJdbcUrl)
            registry.add("spring.datasource.username", postgresDB::getUsername)
            registry.add("spring.datasource.password", postgresDB::getPassword)
        }
    }

    @BeforeEach
    fun setUp() {
        courseRepo.deleteAll()
        instructorRepo.deleteAll()

        val instructor = instructorEntity()
        instructorRepo.save(instructor)

        val courses = courseEntityList(instructor)
        courseRepo.saveAll(courses)
    }

    @Test
    fun createCourse() {
        val instructor = instructorRepo.findAll().first()
        println("the instructor is ${instructor.name}")
        val course = CourseDTO(
            id = null,
            name = "Build Restful APIs Using SpringBoot and Kotlin",
            category = "developer",
            instructorId = instructor.id,
        )

        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(course)
            .exchange()
            .expectStatus().isCreated
            .expectBody(course::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue {
            savedCourseDTO!!.id != null
        }
    }

    @Test fun retrieveAllCourses() {
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

    @Test fun retreiveAllCoursesByName() {
        val uri = UriComponentsBuilder.fromUriString("/v1/courses")
            .queryParam("course_name", "SpringBoot")
            .toUriString()

        val courses = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(1, courses!!.size)
    }

    @Test fun updateCourse() {
        val instructor = instructorRepo.findAll().first()

        val course = CourseEntity(
            id = null,
            name = "Build Android App Using Python",
            category = "stranger",
            instructor = instructor,
        )
        courseRepo.save(course)

        val updatedCourseDTO = CourseDTO(
            id = null,
            name = "changed title",
            category = "liusha",
            instructorId = instructor.id
        )

        val updatedCourse = webTestClient.put()
            .uri("/v1/courses/{courseId}", course.id)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("changed title", updatedCourse!!.name)
        Assertions.assertEquals( "liusha", updatedCourse!!.category)
    }

    @Test fun deleteCourse() {
        val instructor = instructorRepo.findAll().first()
        val course = CourseEntity(
            id = null,
            name = "Build Android App Using Python",
            category = "stranger",
            instructor = instructor,
        )
        courseRepo.save(course)

        val deletedCourse = webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", course.id)
            .exchange()
            .expectStatus().isNoContent
    }
}
