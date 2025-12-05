package br.ce.wcaquino.taskbackend.repo;

import br.ce.wcaquino.taskbackend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import br.ce.wcaquino.taskbackend.model.Task;
import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Status status);

    boolean existsByStatus(Status status);
}
