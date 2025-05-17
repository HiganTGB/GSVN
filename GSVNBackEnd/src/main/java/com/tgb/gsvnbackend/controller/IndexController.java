package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.service.index.IndexService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index")
public class IndexController {
    private final IndexService service;

    public IndexController(IndexService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_admin')")
    public void create() {
        service.createIndices();
    }
}
