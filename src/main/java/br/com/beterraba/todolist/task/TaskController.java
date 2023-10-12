package br.com.beterraba.todolist.task;

import br.com.beterraba.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var userId = request.getAttribute("userId");
        return this.taskRepository.findByUserId((UUID) userId);
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        var userId = request.getAttribute("userId");
        taskModel.setUserId((UUID) userId);
        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest().body("Invalid date");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest().body("The startAt date must be before endAt");
        }

        return ResponseEntity.accepted().body(this.taskRepository.save(taskModel));
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(404).body("not found");
        }

        Utils.copyNonNullProperties(taskModel,task);

        return  ResponseEntity.ok().body("Task updated");
    }
}
