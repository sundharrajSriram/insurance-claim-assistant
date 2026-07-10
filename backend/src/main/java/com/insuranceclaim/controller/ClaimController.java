package com.insuranceclaim.controller;

import com.insuranceclaim.model.ClaimRequest;
import com.insuranceclaim.model.ClaimState;
import com.insuranceclaim.model.ReviewRequest;
import com.insuranceclaim.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ClaimState> submit(@Valid @RequestBody ClaimRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(claimService.submit(request));
    }

    @GetMapping
    public List<ClaimState> list() {
        return claimService.findAll();
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return claimService.stats();
    }

    @GetMapping("/review/pending")
    public List<ClaimState> pendingReview() {
        return claimService.pendingReview();
    }

    @GetMapping("/{claimId}")
    public ClaimState get(@PathVariable String claimId) {
        return claimService.findById(claimId);
    }

    @PostMapping("/{claimId}/review")
    public ClaimState review(@PathVariable String claimId,
                             @Valid @RequestBody ReviewRequest request) {
        return claimService.review(claimId, request);
    }
}
