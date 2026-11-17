package com.feedhub.feedback.presentation.controller;

import com.feedhub.common.response.ApiResponse;
import com.feedhub.common.security.SecurityUser;
import com.feedhub.feedback.application.dto.AvaliacaoRequest;
import com.feedhub.feedback.application.dto.AvaliacaoResponse;
import com.feedhub.feedback.application.usecase.CreateAvaliacaoUseCase;
import com.feedhub.feedback.application.usecase.GetAvaliacaoUseCase;
import com.feedhub.feedback.application.usecase.ListAvaliacoesUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Camada de apresentacao (REST) do feedback-service.
 */
@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final CreateAvaliacaoUseCase createAvaliacaoUseCase;
    private final ListAvaliacoesUseCase listAvaliacoesUseCase;
    private final GetAvaliacaoUseCase getAvaliacaoUseCase;

    public AvaliacaoController(CreateAvaliacaoUseCase createAvaliacaoUseCase,
                                ListAvaliacoesUseCase listAvaliacoesUseCase,
                                GetAvaliacaoUseCase getAvaliacaoUseCase) {
        this.createAvaliacaoUseCase = createAvaliacaoUseCase;
        this.listAvaliacoesUseCase = listAvaliacoesUseCase;
        this.getAvaliacaoUseCase = getAvaliacaoUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AvaliacaoResponse>> criar(@Valid @RequestBody AvaliacaoRequest request,
                                                                 @AuthenticationPrincipal SecurityUser autor) {
        var avaliacao = createAvaliacaoUseCase.execute(request, autor);
        var body = ApiResponse.of(AvaliacaoResponse.from(avaliacao), "Avaliacao registrada com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AvaliacaoResponse>>> listar(@AuthenticationPrincipal SecurityUser solicitante) {
        var avaliacoes = listAvaliacoesUseCase.execute(solicitante).stream()
                .map(AvaliacaoResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.of(avaliacoes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AvaliacaoResponse>> buscarPorId(@PathVariable String id,
                                                                       @AuthenticationPrincipal SecurityUser solicitante) {
        var avaliacao = getAvaliacaoUseCase.execute(id, solicitante);
        return ResponseEntity.ok(ApiResponse.of(AvaliacaoResponse.from(avaliacao)));
    }
}
