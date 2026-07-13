package com.dantal.clinical.web;

import com.dantal.clinical.service.ClinicalResourceService;
import com.dantal.security.ClinicalAccess;
import com.dantal.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@ClinicalAccess
public abstract class ClinicalResourceController {

    private final ClinicalResourceService<?> service;
    private final String collectionKey;
    private final String itemKey;

    protected ClinicalResourceController(ClinicalResourceService<?> service,
                                         String collectionKey,
                                         String itemKey) {
        this.service = service;
        this.collectionKey = collectionKey;
        this.itemKey = itemKey;
    }

    @GetMapping
    public Map<String, Object> list(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of(collectionKey, service.list(principal));
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String id) {
        return Map.of(itemKey, service.get(principal, id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@AuthenticationPrincipal UserPrincipal principal,
                                                      @RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(itemKey, service.create(principal, body)));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@AuthenticationPrincipal UserPrincipal principal,
                                      @PathVariable String id,
                                      @RequestBody Map<String, Object> body) {
        return Map.of(itemKey, service.update(principal, id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String id) {
        service.delete(principal, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public Map<String, Object> bulkReplace(@AuthenticationPrincipal UserPrincipal principal,
                                           @RequestBody Map<String, List<Map<String, Object>>> body) {
        List<Map<String, Object>> items = body.getOrDefault(collectionKey, List.of());
        service.replaceAll(principal, items);
        return Map.of("replaced", items.size());
    }
}
