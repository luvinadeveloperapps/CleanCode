package todolist.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import todolist.dto.TaskDto;
import todolist.entity.Task;
import todolist.repository.TaskRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<TaskDto> getTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks
                .stream()
                .map(task -> modelMapper.map(task, TaskDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskDto getTask(Integer taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("Task ID not found: %d", taskId)));
        return modelMapper.map(task, TaskDto.class);
    }

    @Transactional
    public TaskDto saveTask(TaskDto taskDto) {
        Task task;
        if (Objects.isNull(taskDto.getId())) {
            task = new Task();
        } else {
            task = taskRepository.findById(taskDto.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, String.format("Task ID not found: %d", taskDto.getId())));
        }
        modelMapper.map(taskDto, task);
        Task savedTask = taskRepository.save(task);
        return modelMapper.map(savedTask, TaskDto.class);
    }

    @Transactional
    public void deleteTask(Integer taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("Task ID not found: %d", taskId)));
        taskRepository.delete(task);
    }

}
