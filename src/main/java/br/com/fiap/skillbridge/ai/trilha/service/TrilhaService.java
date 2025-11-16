package br.com.fiap.skillbridge.ai.trilha.service;

import br.com.fiap.skillbridge.ai.shared.exception.NotFoundException;
import br.com.fiap.skillbridge.ai.trilha.dto.*;
import br.com.fiap.skillbridge.ai.trilha.model.Trilha;
import br.com.fiap.skillbridge.ai.trilha.repository.TrilhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TrilhaService {
    private final TrilhaRepository repo;
    public TrilhaService(TrilhaRepository repo){ this.repo = repo; }

    @Transactional
    public TrilhaResponse create(TrilhaRequest r){
        var t = Trilha.builder()
                .titulo(r.titulo())
                .descricao(r.descricao())
                .ativa(r.ativa() == null || r.ativa())
                .build();
        return map(repo.save(t));
    }

    @Transactional(readOnly = true)
    public List<TrilhaResponse> list(){ return repo.findAll().stream().map(this::map).toList(); }

    @Transactional(readOnly = true)
    public TrilhaResponse get(Long id){
        var t = repo.findById(id).orElseThrow(() -> new NotFoundException("Trilha não encontrada."));
        return map(t);
    }

    @Transactional
    public TrilhaResponse update(Long id, TrilhaUpdateRequest r){
        var t = repo.findById(id).orElseThrow(() -> new NotFoundException("Trilha não encontrada."));
        t.setTitulo(r.titulo());
        t.setDescricao(r.descricao());
        if (r.ativa()!=null) t.setAtiva(r.ativa());
        return map(repo.save(t));
    }

    @Transactional
    public void delete(Long id){
        if (!repo.existsById(id)) throw new NotFoundException("Trilha não encontrada.");
        repo.deleteById(id);
    }

    private TrilhaResponse map(Trilha t){ return new TrilhaResponse(t.getId(), t.getTitulo(), t.getDescricao(), t.isAtiva()); }
}
