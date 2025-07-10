package it.aulab.progetto_finale_sciancalepore.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.aulab.progetto_finale_sciancalepore.models.CareerRequest;
import it.aulab.progetto_finale_sciancalepore.models.Role;
import it.aulab.progetto_finale_sciancalepore.models.User;
import it.aulab.progetto_finale_sciancalepore.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_sciancalepore.repositories.RoleRepository;
import it.aulab.progetto_finale_sciancalepore.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class CareerRequestServiceImpl implements CareerRequestService{

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();

        if(allUserIds.contains(user.getId())){
            return true;
        }

        List<Long> requests = careerRequestRepository.findByUserId(user.getId());

        return requests.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    @Override
    public void save(CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        emailService.sendSimpleEmail("adminChronicle@admin.it", "Richiesta per ruolo " + careerRequest.getRole().getName(), "C'è una nuova richiesta di collaborazione da parte di " + user.getUsername());
    }

    @Override
    public void careerAccept(Long requestId) {
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        User user = request.getUser();
        Role role = request.getRole();

        List<Role> rolesUser = user.getRoles();
        if (role.getName().equals("ROLE_USER")) {
            rolesUser.clear();
            Role newRole = roleRepository.findByName(role.getName());
            rolesUser.add(newRole);
        } else {
            rolesUser.removeIf(roleUser -> roleUser.getName().equals("ROLE_USER"));
            Role newRole = roleRepository.findByName(role.getName());
            rolesUser.add(newRole);
        }

        user.setRoles(rolesUser);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleEmail(user.getEmail(), "Ruolo abilitato", "Ciao, la tua richiesta di collaborazione è stata accettata dalla nosta amministrazione");
    }

    @Override
    public void careerReject(Long requestId) {
        CareerRequest request = careerRequestRepository.findById(requestId).get();
        User user = request.getUser();
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleEmail(user.getEmail(), "Ruolo non abilitato", "Ciao, la tua richiesta di collaborazione è stata rifiutata dalla nosta amministrazione, puoi rieffettuare la richiesta in più tardi");
    }

    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    }
    
}
