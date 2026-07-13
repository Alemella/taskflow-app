package taskflow_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskflow_backend.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserEmail(String email);

    Optional<Task> findByIdAndUserEmail(Long id, String email);
}