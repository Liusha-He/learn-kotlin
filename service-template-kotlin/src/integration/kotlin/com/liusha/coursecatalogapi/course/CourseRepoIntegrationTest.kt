package com.liusha.coursecatalogapi.course

import com.liusha.coursecatalogapi.course.model.CourseRepo
import com.liusha.coursecatalogapi.instructor.model.InstructorRepo
import com.liusha.coursecatalogapi.utils.PostgreSQLContainerInitializer
import com.liusha.coursecatalogapi.utils.courseEntityList
import com.liusha.coursecatalogapi.utils.instructorEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseRepoIntegrationTest : PostgreSQLContainerInitializer() {
    @Autowired lateinit var courseRepo: CourseRepo
    @Autowired lateinit var instructorRepo: InstructorRepo

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
    fun findByNameContaining() {
        val courses = courseRepo.findByNameContaining("SpringBoot")
        Assertions.assertEquals(1, courses.size)
    }

    @Test
    fun findCoursesByName() {
        val courses = courseRepo.findCoursesByName("SpringBoot")
        Assertions.assertEquals(1, courses.size)
    }

    @ParameterizedTest
    @MethodSource("courseAndSize")
    fun findCourseBtName_approach2(name: String, expectedSize: Int) {
        val courses = courseRepo.findCoursesByName(name)
        Assertions.assertEquals(expectedSize, courses.size)
    }

    companion object {
        @JvmStatic
        fun courseAndSize(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments("SpringBoot", 1),
                Arguments.arguments("Android", 1)
            )
        }
    }
}
