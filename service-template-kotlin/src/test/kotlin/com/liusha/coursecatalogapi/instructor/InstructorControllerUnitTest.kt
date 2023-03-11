package com.liusha.coursecatalogapi.instructor

import com.liusha.coursecatalogapi.instructor.model.InstructorDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient

fun instructorDTO(
    id: Int?,
    name: String
) = InstructorDTO(id, name)

@WebMvcTest(InstructorController::class)
@AutoConfigureWebTestClient
class InstructorControllerUnitTest {
    @Autowired lateinit var webTestClient: WebTestClient
    @MockkBean lateinit var instructorServiceMock: InstructorService

    @Test fun createInstructor() {
        //given
        val instructor = InstructorDTO(null, "Liusha He")

        every { instructorServiceMock.createInstructor( any() ) } returns instructorDTO(1, "Liusha He")

        val savedInstructorDTO = webTestClient
            .post()
            .uri("/v1/instructors")
            .bodyValue(instructor)
            .exchange()
            .expectStatus().isCreated
            .expectBody(InstructorDTO::class.java)
            .returnResult()
            .responseBody

        //then
        Assertions.assertTrue {
            savedInstructorDTO!!.id != null
        }
    }

    @Test fun createInstructor_Validation() {
        val instructor = InstructorDTO(null, "")
        val errorMessage = "instructor name is required"
        every { instructorServiceMock.createInstructor(any()) } returns instructorDTO(1, "Liusha He")

        val response = webTestClient
            .post()
            .uri("/v1/instructors")
            .bodyValue(instructor)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(errorMessage, response)
    }
}