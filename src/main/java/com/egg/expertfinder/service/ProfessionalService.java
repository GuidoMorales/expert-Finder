package com.egg.expertfinder.service;

import com.egg.expertfinder.entity.CustomUser;
import com.egg.expertfinder.entity.Image;
import com.egg.expertfinder.entity.Job;
import com.egg.expertfinder.entity.Location;
import com.egg.expertfinder.entity.Professional;
import com.egg.expertfinder.exception.EntityNotFoundException;
import com.egg.expertfinder.repository.ProfessionalRepository;
import com.egg.expertfinder.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfessionalService {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private JobService jobService;

    //Creación de un profesional.
    @Transactional
    public void createProfessional(String name, String lastName, String email, String password,
            String password2, String address, MultipartFile file, Long idJob,
            String description, String license, String phone) throws IllegalArgumentException {

        validate(name, lastName, email, password, password2, file, idJob, description, license, phone, address);

        Professional professional = new Professional(name, lastName, email,
                description, license, phone);

        //Seteo de contraseña encriptada.
        professional.setPassword(new BCryptPasswordEncoder().encode(password));

        Location location = locationService.createLocation("Chacras de Coria", address);

        professional.setLocation(location);

        Job job = jobService.getJobById(idJob);

        professional.setJob(job);

        Image image = imageService.createImage(file);

        professional.setImage(image);

        professionalRepository.save(professional);
    }

    @Transactional
    public void updateProfessional(Long id, String name, String lastName, String email,
            MultipartFile file, String description, String phone) throws IllegalArgumentException {
        Optional<Professional> response = professionalRepository.findById(id);
        if (response.isPresent()) {
            Professional professional = response.get();

            professional.updateProfessional(name, lastName, description, phone);
            //Comprobamos que si llega un email para actualizar no exista en la DB
            if (email != null) {
                Professional proEmail = professionalRepository.findProfessionalByEmail(email);
                CustomUser userEmail = userRepository.findCustomUserByEmail(email);
                if (proEmail != null || userEmail != null) {
                    throw new IllegalArgumentException("Ya existe un profesional registrado con ese email.");
                } else {
                    professional.setEmail(email);
                }
            }

            //Si llega un MultipartFile lo actualizamos
            if (file != null) {
                Long idImage = professional.getImage().getId();
                Image image = imageService.updateImage(idImage, file);
                professional.setImage(image);
            }

            professionalRepository.save(professional);
        }
    }

    @Transactional
    public void deactivateProfessional(Long id) throws EntityNotFoundException {
        Optional<Professional> response = professionalRepository.findById(id);
        if (response.isPresent()) {
            Professional professional = response.get();
            professional.deactivateProfessional();
            professionalRepository.save(professional);
        } else {
            throw new EntityNotFoundException(Professional.class, id);
        }
    }

    @Transactional
    public void activateProfessional(Long id) throws EntityNotFoundException {
        Optional<Professional> response = professionalRepository.findById(id);
        if (response.isPresent()) {
            Professional professional = response.get();
            professional.activateProfessional();
            professionalRepository.save(professional);
        } else {
            throw new EntityNotFoundException(Professional.class, id);
        }
    }

    @Transactional
    public void deleteProfessional(Long id) throws EntityNotFoundException {
        Optional<Professional> response = professionalRepository.findById(id);
        if (response.isPresent()) {
            professionalRepository.delete(response.get());
        } else {
            throw new EntityNotFoundException(Professional.class, id);
        }
    }

    //Validación de datos del profesional.
    public void validate(String name, String lastName, String email, String password,
            String password2, MultipartFile file, Long idJob, String description,
            String license, String phone,String address) throws IllegalArgumentException {
        
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o estar vacío.");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo o estar vacío.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o estar vacía.");
        }
        if (password.length() <= 5) {
            throw new IllegalArgumentException("La contraseña no puede contener 5 caracteres o menos.");
        }
        if (!password2.equals(password)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar una imagen de perfil.");
        }
        if (idJob == null) {
            throw new IllegalArgumentException("Debe ingresar un servicio a ofrecer.");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar una descripción.");
        }
        if (license == null || license.isEmpty()) {
            throw new IllegalArgumentException("Debe presentar su matrícula.");
        }
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar su número de teléfono.");
               }else {
                       for (int j = 0; j <= phone.length()-1 ; j++) {
                           try {
                               Integer.parseInt( phone.substring(j,j+1));
                           } catch (Exception e) {
                               throw new IllegalArgumentException("El numero de telefono debe ser solo numeros");
                           }
                       }
        }
        if (address==null || address.isEmpty()){
            throw new IllegalArgumentException("Debe ingresar una direccion");
        }
        if (email==null || email.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un correo");
        }else if(!email.contains("@")){
            throw new IllegalArgumentException("El correo debe poseer '@'");
        }else if(email.substring(email.length()-1).equals("@")){
            throw new IllegalArgumentException("El correo debe poseer caracteres luego de la '@'");
        }
    }
       public void validateAll(String name, String lastName, String email, String password,
            String password2, MultipartFile file, Long idJob, String description,
            String license, String phone,String address,int num) throws IllegalArgumentException {
        
           switch (num) {
               case 1:
                   if (name == null || name.isEmpty()) {
                       throw new IllegalArgumentException("El nombre no puede ser nulo o estar vacío.");
                   }
                   break;
               case 2:
                   if (lastName == null || lastName.isEmpty()) {
                       throw new IllegalArgumentException("El apellido no puede ser nulo o estar vacío.");
                   }
                   break;
               case 3:
                   if (password == null || password.isEmpty()) {
                      throw new IllegalArgumentException("La contraseña no puede ser nula o estar vacía.");
                   }else if(password.length() <= 5){
                       throw new IllegalArgumentException("La contraseña no puede contener 5 caracteres o menos.");
                   }
                   break;
               case 4:
                   if (!password2.equals(password)) {
                       throw new IllegalArgumentException("Las contraseñas no coinciden.");
                   }
                   break;
               case 5:
                   if (address == null || address.isEmpty()) {
                       throw new IllegalArgumentException("Debe ingresar una direccion");
                   }
                   break;
               case 6:
                   if (file == null || file.isEmpty()) {
                       throw new IllegalArgumentException("Debe ingresar una imagen de perfil.");
                   }
                   break;
               case 7:
                   if (idJob == null) {
                       throw new IllegalArgumentException("Debe ingresar un servicio a ofrecer.");
                   }
                   break;
               case 8:
                   if (description == null || description.isEmpty()) {
                       throw new IllegalArgumentException("Debe ingresar una descripción.");
                   }
                   break;
               case 9:
                   if (license == null || license.isEmpty()) {
                       throw new IllegalArgumentException("Debe presentar su matrícula.");
                   }
                   break;
               case 10:
                   if (phone == null || phone.isEmpty()) {
                       throw new IllegalArgumentException("Debe ingresar su número de teléfono.");
                    }else {            
                       for (int j = 0; j <= phone.length()-1 ; j++) {
                           try {
                               Integer.parseInt( phone.substring(j,j+1));
                           } catch (Exception e) {
                               throw new IllegalArgumentException("El numero de telefono debe ser solo numeros");
                           }
                       }
                   }
                       break;
                case 11:
                    if (email == null || email.isEmpty()) {
                        throw new IllegalArgumentException("Debe ingresar un correo");
                    } else if (!email.contains("@")) {
                        throw new IllegalArgumentException("El correo debe poseer '@'");
                    } else if (email.substring(email.length() - 1).equals("@")) {
                        throw new IllegalArgumentException("El correo debe poseer caracteres luego de la '@'");
                    }
                       break;
           }
    }

    //Obtener un profesional de la base de datos usando su ID.
    public Professional getProfessionalById(Long id) throws EntityNotFoundException {
        Optional<Professional> response = professionalRepository.findById(id);
        if (response.isPresent()) {
            return response.get();
        } else {
            throw new EntityNotFoundException(Professional.class, id);
        }
    }

    //Listar todos los profesionales existentes.
    public List<Professional> getAllProfessionals() {
        return professionalRepository.findAll();
    }

    //Listar todos los profesionales que están activos.
    public List<Professional> getProfessionalsActivate() {
        
        return professionalRepository.findByActiveTrue();        
    }

    //Listar todos los profesionales que están inactivos.
    public List<Professional> getProfessionalsDeactivate() {
        return professionalRepository.findByActiveFalse();
    }

    //Listar todos los profesionales según el service que ofrece
    public List<Professional> getProfessionalsByJobId(Long idJob) {
        return professionalRepository.findByJobId(idJob);
    }

}
