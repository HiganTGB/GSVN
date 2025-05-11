package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.SPUDTO;
import com.tgb.gsvnbackend.service.SPUService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spus")
public class SPUController {

    private final SPUService spuService;

    @Autowired
    public SPUController(SPUService spuService) {
        this.spuService = spuService;
    }


    @PostMapping
    public ResponseEntity<SPUDTO> createSPU(@RequestBody @Valid SPUDTO spuDTO) {
        SPUDTO createdSPU = spuService.create(spuDTO);
        return new ResponseEntity<>(createdSPU, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SPUDTO> updateSPU(@PathVariable int id, @RequestBody @Valid SPUDTO spuDTO) {
        SPUDTO updatedSPU = spuService.update(id, spuDTO);
        return new ResponseEntity<>(updatedSPU, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSPU(@PathVariable int id) {
        spuService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/exists")
    public Boolean check (@PathVariable int id) {
        return spuService.exists(id);
    }

    @GetMapping("/{id}/domain")
    public SPUDomain readDomain (@PathVariable int id) {
        return spuService.getDomain(id);
    }
}
