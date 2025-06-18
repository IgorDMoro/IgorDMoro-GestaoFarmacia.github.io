package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Transportadora;
import com.igordmoro.farmacia.GestaoFarmacia.repository.TransportadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportadoraService {

    private final TransportadoraRepository transportadoraRepository;

    @Autowired
    public TransportadoraService(TransportadoraRepository transportadoraRepository) {
        this.transportadoraRepository = transportadoraRepository;
    }

    public Transportadora salvarTransportadora(Transportadora transportadora) {
        // Validações de negócio para Transportadora
        if (transportadora.getNome() == null || transportadora.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da transportadora não pode ser vazio.");
        }
        if (transportadora.getCnpj() == null || String.valueOf(transportadora.getCnpj()).length() != 14) {
            // Validação básica de CNPJ como 14 dígitos (apenas exemplo)
            throw new IllegalArgumentException("CNPJ inválido. Deve ter 14 dígitos.");
        }
        if (transportadora.getLocalAtendimento() == null || transportadora.getLocalAtendimento().trim().isEmpty()) {
            throw new IllegalArgumentException("Local de atendimento não pode ser vazio.");
        }

        return transportadoraRepository.save(transportadora);
    }

    public List<Transportadora> listarTodasTransportadoras() {
        return transportadoraRepository.findAll();
    }

    public Optional<Transportadora> buscarTransportadoraPorId(Long id) {
        return transportadoraRepository.findById(id);
    }

    public Optional<Transportadora> buscarTransportadoraPorCnpj(Long cnpj) {
        return transportadoraRepository.findByCnpj(cnpj);
    }

    public void deletarTransportadora(Long id) {
        if (!transportadoraRepository.existsById(id)) {
            throw new IllegalArgumentException("Transportadora não encontrada com ID: " + id);
        }
        transportadoraRepository.deleteById(id);
    }
}