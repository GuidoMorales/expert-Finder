package com.egg.expertfinder.service;

import com.egg.expertfinder.entity.Comment;
import com.egg.expertfinder.entity.Professional;
import com.egg.expertfinder.entity.Task;
import com.egg.expertfinder.exception.EntityNotFoundException;
import com.egg.expertfinder.repository.CommentRepository;
import com.egg.expertfinder.repository.ProfessionalRepository;
import com.egg.expertfinder.repository.TaskRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProfessionalService professionalService;
    
    @Autowired
    private ProfessionalRepository professionalRepository;

    @Transactional
    public void createComment(Long idTask, Long idUser, Long idProfessional, String content, Double score) throws IllegalArgumentException {

        validate(idTask, idUser, idProfessional, content, score);

        Task task = taskRepository.getReferenceById(idTask);

        if (task != null) {

            if (task.getComment() == null) {

                Comment comment = new Comment(content, score);

                comment.setUser(userService.getUserById(idUser));
                comment.setProfessional(professionalService.getProfessionalById(idProfessional));

                task.setComment(commentRepository.save(comment));
                taskRepository.save(task);
                Professional professional = professionalService.getProfessionalById(idProfessional);
                professional.getComments().add(comment);
                professionalRepository.save(professional);
            } else {
                throw new IllegalArgumentException("Ya existe un comentario en esta tarea.");
            }

        } else {
            throw new IllegalArgumentException("No existe una tarea con ese Id.");
        }
    }

    @Transactional
    public void reportComment(Long idComment) throws EntityNotFoundException {
        Optional<Comment> response = commentRepository.findById(idComment);
        if (response.isPresent()) {
            Comment comment = response.get();

            comment.updateReports();
            commentRepository.save(comment);

        } else {
            throw new EntityNotFoundException(Comment.class, idComment);
        }
    }

    @Transactional
    public void updateComment(Long idTask, Long idUser, String content) throws IllegalArgumentException {
        Optional<Task> response = taskRepository.findById(idTask);
        if (response.isPresent()) {
            Comment comment = response.get().getComment();
            if (comment != null && comment.getUser().getId() == idUser) {
                comment.setContent(content);
                commentRepository.save(comment);
            } else {
                throw new IllegalArgumentException("No existe un comentario con ese id o el usuario no corresponde.");
            }
        } else {
            throw new IllegalArgumentException("No existe una tarea con ese Id.");
        }
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(Long id) throws EntityNotFoundException {
        Optional<Comment> response = commentRepository.findById(id);
        if (response.isPresent()) {
            return response.get();
        } else {
            throw new EntityNotFoundException(Comment.class, id);
        }
    }

    public List<Comment> getCommentsWithReports() {
        return commentRepository.findCommentsWithReportsGreaterThanZero();
    }

    @Transactional
    public void deactivateCommentById(Long id) throws EntityNotFoundException {
        Optional<Comment> response = commentRepository.findById(id);
        if (response.isPresent()) {
            Comment comment = response.get();
            comment.deactivateComment();
            commentRepository.save(comment);
        } else {
            throw new EntityNotFoundException(Comment.class, id);
        }
    }
    
    public List<Comment> getCommentsByProfessionalId(Long id) {
        return commentRepository.findCommentsByProfessionalId(id);
    }

    private void validate(Long idTask, Long idUser, Long idProfessional, String content, Double score) throws IllegalArgumentException {
        if (idTask == null) {
            throw new IllegalArgumentException("No se ingresó el Id de la tarea.");
        }
        if (idUser == null) {
            throw new IllegalArgumentException("No se ingresó el Id del Usuario.");
        }
        if (idProfessional == null) {
            throw new IllegalArgumentException("No se ingresó el Id del Professional.");
        }
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("El contenido del comentario no puede estar vacio.");
        }
        if (score == null) {
            throw new IllegalArgumentException("Debe ingresar una valoración por el trabajo.");
        }
    }
}
