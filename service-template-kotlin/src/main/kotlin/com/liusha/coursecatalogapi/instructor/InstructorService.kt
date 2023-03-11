package com.liusha.coursecatalogapi.instructor

import com.liusha.coursecatalogapi.instructor.model.InstructorDTO
import com.liusha.coursecatalogapi.instructor.model.InstructorEntity
import com.liusha.coursecatalogapi.instructor.model.InstructorRepo
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class InstructorService(val instructorRepo: InstructorRepo)
{
    companion object: KLogging()

    fun createInstructor(instructorDTO: InstructorDTO): InstructorDTO
    {
        val instructorEntity = instructorDTO.let {
            InstructorEntity(it.id, it.name)
        }
        instructorRepo.save(instructorEntity)

        logger.info("Saved instructor: $instructorEntity")

        return instructorEntity.let {
            InstructorDTO(it.id, it.name)
        }
    }

    fun findByInstructorId(instructorId: Int): Optional<InstructorEntity> =
        instructorRepo.findById(instructorId)
}