package com.egg.expertfinder.service;

import com.egg.expertfinder.entity.Image;
import com.egg.expertfinder.entity.Job;
import com.egg.expertfinder.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.egg.expertfinder.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class JobService {


    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ImageService imageService;

    @Transactional
    public void createJob(String name, MultipartFile file) throws IllegalArgumentException {
        validate(name, file);

        if (jobRepository.findJobByName(name) != null) {
            throw new IllegalArgumentException("Ya existe este servicio");
        }

        Job job = new Job(name);

        Image image = imageService.createImage(file);

        job.setImage(image);

        jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long id) throws EntityNotFoundException {
        Optional<Job> response = jobRepository.findById(id);
        if (response.isPresent()) {
            jobRepository.delete(response.get());
        } else {
            throw new EntityNotFoundException(Job.class, id);
        }
    }

    @Transactional
    public void updateJob(Long id, String name, MultipartFile file) throws EntityNotFoundException {
        Optional<Job> response = jobRepository.findById(id);
        if (response.isPresent()) {
            Job job = response.get();
            if (name != null) {
                job.setName(name);
            }
            if (file != null) {
                Image image = job.getImage();
                image = imageService.updateImage(image.getId(), file);
                job.setImage(image);
            }
            jobRepository.save(job);
        } else {
            throw new EntityNotFoundException(Job.class, id);
        }
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) throws EntityNotFoundException {
        Optional<Job> response = jobRepository.findById(id);
        if (response.isPresent()) {
            return response.get();
        } else {
            throw new EntityNotFoundException(Job.class, id);
        }
    }

    private void validate(String name, MultipartFile file) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio no puede estar vacio.");
        }
        if (file == null) {
            throw new IllegalArgumentException("Debe ingresar una imagen para identificar al Servicio.");
        }
    }
}
