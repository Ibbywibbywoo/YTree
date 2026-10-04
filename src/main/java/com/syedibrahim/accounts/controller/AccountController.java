package com.syedibrahim.accounts.controller;

import com.syedibrahim.accounts.model.Provider;
import com.syedibrahim.accounts.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:5173")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Get client's providers
    @GetMapping("/providers")
    public Collection<Provider> getProviders() {
        return accountService.getClientProviders();
    }

    // Get available providers to add
    @GetMapping("/available")
    public List<String> getAvailable() {
        return accountService.getAvailableProviders();
    }

    // Add a provider
    @PostMapping("/providers")
    public ResponseEntity<?> addProvider(@RequestBody Map<String, String> body) {
        try {
            Provider p = accountService.addProvider(body.get("name"));
            return ResponseEntity.ok(p);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Remove a provider
    @DeleteMapping("/providers/{id}")
    public ResponseEntity<?> removeProvider(@PathVariable String id) {
        try {
            accountService.removeProvider(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Upload a statement
    @PostMapping("/providers/{id}/upload")
    public ResponseEntity<?> uploadStatement(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        try {
            Provider p = accountService.uploadStatement(id, body.get("filename"));
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Submit
    @PostMapping("/submit")
    public ResponseEntity<?> submit() {
        try {
            accountService.submit();
            return ResponseEntity.ok(Map.of("message", "Submission successful!"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}