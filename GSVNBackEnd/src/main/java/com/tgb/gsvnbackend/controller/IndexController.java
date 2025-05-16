package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.service.index.IndexService;
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
    public void create() {
        service.createIndices();
    }
}
