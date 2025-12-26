package com.example.appointment.services;


import com.example.appointment.services.dto.ServiceCreateRequest;
import com.example.appointment.services.dto.ServiceResponse;
import com.example.appointment.services.dto.ServiceUpdateRequest;
import com.example.appointment.services.repo.ServiceRepositoryJdbc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepositoryJdbc repo;

    public ServiceService(ServiceRepositoryJdbc repo){
        this.repo=repo;
    }

    public List<ServiceResponse> listActive(){
        return repo.listActive();
    }

    @Transactional
    public long create(ServiceCreateRequest req){
        String name=req.getName().trim();
        return repo.insert(name,req.getDurationMinutes(),req.getPrice());
    }

    @Transactional
    public void update(long id, ServiceUpdateRequest req){
        if (!repo.existsById(id)){
            throw new IllegalArgumentException("Service bulunamadı.");
        }
        int updated = repo.updatePartial(id,
                req.getName() == null ? null :req.getName().trim(),
                req.getDurationMinutes(),
                req.getPrice(),
                req.getActive());

        if (updated == 0){
            throw new IllegalStateException("Service güncellenemedi. ");
        }
    }
}
