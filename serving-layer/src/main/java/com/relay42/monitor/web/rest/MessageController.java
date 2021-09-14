package com.relay42.monitor.web.rest;

import com.relay42.monitor.dto.MessageSearchDTO;
import com.relay42.monitor.model.Message;
import com.relay42.monitor.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device")

public class MessageController {

    @Autowired
    MessageService messageService;

    @PostMapping("/search")
    public ResponseEntity<List<Message>> search(@RequestBody MessageSearchDTO searchDTO) {
            List<Message> list = messageService.search(searchDTO.getDevices(), searchDTO.getFrom(), searchDTO.getTo());
        return new ResponseEntity<List<Message>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/min")
    public ResponseEntity<Long> minimum(@RequestBody MessageSearchDTO searchDTO) {
        Long min = messageService.minimum(searchDTO.getDevices(), searchDTO.getFrom(), searchDTO.getTo());
        return new ResponseEntity<Long>(min, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/max")
    public ResponseEntity<Long> maximum(@RequestBody MessageSearchDTO searchDTO) {
        Long max = messageService.maximum(searchDTO.getDevices(), searchDTO.getFrom(), searchDTO.getTo());
        return new ResponseEntity<Long>(max, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/avg")
    public ResponseEntity<Long> average(@RequestBody MessageSearchDTO searchDTO) {
        Long avg = messageService.average(searchDTO.getDevices(), searchDTO.getFrom(), searchDTO.getTo());
        return new ResponseEntity<Long>(avg, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/med")
    public ResponseEntity<Double> median(@RequestBody MessageSearchDTO searchDTO) {
        Double med = messageService.median(searchDTO.getDevices(), searchDTO.getFrom(), searchDTO.getTo());
        return new ResponseEntity<Double>(med, new HttpHeaders(), HttpStatus.OK);
    }

}
