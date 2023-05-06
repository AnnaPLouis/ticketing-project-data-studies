package com.cydeo.repository;

import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

      Task getTaskById(Long id);

      @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectCode = ?1 AND t.taskStatus <> 'COMPLETE'")
      Integer totalNonCompletedTasks(String projectCode);


      @Query(value = "SELECT COUNT(*) FROM tasks t JOIN projects p on t.project_id=p.id where p.project_code = ?1 AND t.task_status = 'COMPLETE'", nativeQuery = true)
      Integer totalCompletedTasks(String projectCode);

      List<Task> findAllByProject(Project project);

      List<Task> findAllByTaskStatusIsNotAndAssignedEmployee(Status status, User assignedEmployee);

      List <Task> findAllByTaskStatusAndAssignedEmployee(Status status, User assignedEmployee);
}
