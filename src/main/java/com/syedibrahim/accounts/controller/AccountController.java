package com.syedibrahim.accounts.controller;

import com.syedibrahim.accounts.model.Provider;
import com.syedibrahim.accounts.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * REST controller that exposes the account onboarding API endpoints.
 * All endpoints are prefixed with /api/accounts and accept/return JSON.
 * CORS is configured to allow requests from the frontend development server.
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:5173")
public class AccountController {

    /** Service layer containing all business logic for account management. */
    private final AccountService accountService;

    /**
     * Constructor injection of the AccountService.
     * Spring automatically provides the service bean at startup.
     *
     * @param accountService the service handling all account logic
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Retrieves all providers the client has added, each with their
     * current statement status (UPLOADED, MISSING, or OUTDATED).
     *
     * @return 200 OK with a collection of the client's providers
     */
    @GetMapping("/providers")
    public Collection<Provider> getProviders() {
        return accountService.getClientProviders();
    }

    /**
     * Retrieves the list of known providers that the client has not yet added.
     * Used to populate the "Add provider" search panel on the frontend.
     *
     * @return 200 OK with a list of available provider names
     */
    @GetMapping("/available")
    public List<String> getAvailable() {
        return accountService.getAvailableProviders();
    }

    /**
     * Adds a new provider to the client's account list.
     * The provider is initialised with no statement uploaded (MISSING status).
     *
     * @param body request body containing the provider "name"
     * @return 200 OK with the created Provider, or 400 Bad Request if already added
     */
    @PostMapping("/providers")
    public ResponseEntity<?> addProvider(@RequestBody Map<String, String> body) {
        try {
            Provider p = accountService.addProvider(body.get("name"));
            return ResponseEntity.ok(p);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Removes a provider from the client's account list.
     * The provider will reappear in the available providers list after removal.
     *
     * @param id the unique identifier of the provider to remove
     * @return 200 OK on success, or 400 Bad Request if the provider is not found
     */
    @DeleteMapping("/providers/{id}")
    public ResponseEntity<?> removeProvider(@PathVariable String id) {
        try {
            accountService.removeProvider(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Records a statement upload for the specified provider.
     * Sets the statement date to today, which will update the provider's
     * status from MISSING or OUTDATED to UPLOADED.
     *
     * @param id   the unique identifier of the provider
     * @param body request body containing the statement "filename"
     * @return 200 OK with the updated Provider, or 400 Bad Request if provider not found
     */
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

    /**
     * Submits the client's account onboarding once all providers have
     * a current statement. Validates server-side that no provider is
     * MISSING or OUTDATED, regardless of frontend state.
     *
     * @return 200 OK with a success message, or 400 Bad Request with details
     *         of what still needs to be resolved
     */
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