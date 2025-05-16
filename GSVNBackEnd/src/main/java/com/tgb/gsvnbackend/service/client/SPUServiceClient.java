package com.tgb.gsvnbackend.service.client;

import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.service.SPUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Component
public class SPUServiceClient {
    private final SPUService spuService;
    @Autowired
    public SPUServiceClient(SPUService spuService) {
        this.spuService = spuService;
    }
    public Boolean check(int id)
    {
        return spuService.exists(id);
    }

    public SPUDomain readDomain (int id) {

        return spuService.getDomain(id);
    }
    public void syncAttribute(int id, Map<String,Object> attributes) // should add into MQ
    {
        spuService.updateSyncAttributes(id,attributes);
    }

}
/*
    @FeignClient(value = "SPU-SERVICE", url = "http://localhost:8080")
    public interface SPUServiceClient {
        @GetMapping("/{id}/exists")
        public Boolean check (@PathVariable int id);
         @GetMapping("/{id}/domain")
        public SPUDomain readDomain (@PathVariable int id)
    }
*/
