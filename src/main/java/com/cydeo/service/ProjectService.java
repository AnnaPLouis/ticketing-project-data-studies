package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;

import java.util.List;

public interface ProjectService {

    List<ProjectDTO> listAllProjects();
    ProjectDTO getByProjectCode(String code);
    void save(ProjectDTO project);
    void update(ProjectDTO project);
    void  delete(String code);
    void complete(String code);

    List<ProjectDTO> listAllProjectDetails();

    List<ProjectDTO> listAllNonCompletedByAssignedManager(UserDTO assignedManager);
}
