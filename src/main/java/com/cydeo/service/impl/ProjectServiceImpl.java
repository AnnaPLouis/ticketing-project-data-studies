package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, UserService userService, UserMapper userMapper, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.taskService = taskService;
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> projectList = projectRepository.findAll();

        return projectList.stream().map(projectMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.getByProjectCode(code);

        return projectMapper.convertToDTO(project);

    }

    @Override
    public void save(ProjectDTO project) {

        project.setProjectStatus(Status.OPEN);
        projectRepository.save(projectMapper.convertToEntity(project));

    }

    @Override
    public void update(ProjectDTO project) {

        //Find current project
        Project project1 = projectRepository.getByProjectCode(project.getProjectCode());
        //map u[date project dto to entity
        Project convertedProject = projectMapper.convertToEntity(project);
        //set id to the converted object
        convertedProject.setId(project1.getId());
        //set project status
        convertedProject.setProjectStatus(project1.getProjectStatus());
        //save the updated project in db
        projectRepository.save(convertedProject);


    }

    @Override
    public void delete(String code) {

        Project project = projectRepository.getByProjectCode(code);
        project.setIsDeleted(true);

        project.setProjectCode(project.getProjectCode() + "-" + project.getId());
        projectRepository.save(project);

        taskService.deleteByProject(projectMapper.convertToDTO(project));

    }

    @Override
    public void complete(String code) {

        Project project = projectRepository.getByProjectCode(code);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);

        taskService.completeByProject(projectMapper.convertToDTO(project));

    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
        UserDTO currentUserDTO = userService.findByUserName("harold@manager.com");
        User user = userMapper.convertToEntity(currentUserDTO);

        List<Project> projectList = projectRepository.findByAssignedManager(user);

        return projectList.stream().map(project -> {
                    ProjectDTO obj = projectMapper.convertToDTO(project);

                    obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
                    obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));

                    return obj;
                }

        ).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedByAssignedManager(UserDTO assignedManager) {
        List<Project> projects = projectRepository
                .findAllByProjectStatusIsNotAndAssignedManager(Status.COMPLETE, userMapper.convertToEntity(assignedManager));

        return projects.stream().map(projectMapper::convertToDTO).collect(Collectors.toList());
    }

}
