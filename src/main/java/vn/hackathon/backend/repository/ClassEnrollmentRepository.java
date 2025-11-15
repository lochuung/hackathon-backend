package vn.hackathon.backend.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hackathon.backend.entity.ClassEnrollment;

public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, UUID> {}
