package com.egg.expertfinder.service;

import com.egg.expertfinder.entity.Image;
import com.egg.expertfinder.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @InjectMocks
    private JobService jobService;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ImageService imageService;

    @Test
    public void testCreateJobWithNullName() {
        MultipartFile file = mock(MultipartFile.class);
        // Prueba que se lance una excepción IllegalArgumentException cuando el nombre es nulo
        assertThrows(IllegalArgumentException.class, () -> {
            jobService.createJob(null, file);
        });
    }

    @Test
    public void testCreateJobWithNullFile() {
        // Prueba que se lance una excepción IllegalArgumentException cuando el archivo es nulo
        assertThrows(IllegalArgumentException.class, () -> {
            jobService.createJob("Nombre", null);

        });
    }

    @Test
    public void testCreateJobWithValidParameters() {
        MultipartFile file = mock(MultipartFile.class);

        // Simula el comportamiento de los métodos de repositorio e imagen
        when(jobRepository.findJobByName(anyString())).thenReturn(null);
        when(imageService.createImage(any(MultipartFile.class))).thenReturn(new Image());

        // Prueba que no se lance una excepción cuando se proporcionan parámetros válidos
        assertDoesNotThrow(() -> {
            jobService.createJob("Nombre", file);
        });

        // También puedes verificar otros comportamientos esperados aquí, como llamadas a métodos mock.
    }

    @Test
    public void testCreateJWithNullName() {
        MultipartFile file = mock(MultipartFile.class);
        try {
            jobService.createJob(null, file);
            fail("Exception was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("El nombre del servicio no puede estar vacio.", e.getMessage());
        }
    }

    @Test
    public void testCreateJWithNullFile() {
        try {
            jobService.createJob("Nombre", null);
            fail("Exception was not thrown");
        } catch (IllegalArgumentException e) {
            // La excepción IllegalArgumentException se lanzará y se capturará aquí
            assertEquals("Debe ingresar una imagen para identificar al Servicio.", e.getMessage());
        }
    }

}

