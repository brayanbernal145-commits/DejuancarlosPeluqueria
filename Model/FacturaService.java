package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model.Repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository FacturaRepository;

    public List<Factura> getAllFacturas(){
        return FacturaRepository.findAll();
    }

}
