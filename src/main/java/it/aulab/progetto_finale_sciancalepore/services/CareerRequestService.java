package it.aulab.progetto_finale_sciancalepore.services;

import it.aulab.progetto_finale_sciancalepore.models.CareerRequest;
import it.aulab.progetto_finale_sciancalepore.models.User;

public interface CareerRequestService {
    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);
    void save(CareerRequest careerRequest, User user);
    void careerAccept(Long requestId);
    void careerReject(Long requestId);
    CareerRequest find(Long id);
}
