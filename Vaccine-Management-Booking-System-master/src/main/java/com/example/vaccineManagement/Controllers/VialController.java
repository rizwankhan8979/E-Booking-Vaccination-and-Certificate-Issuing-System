package com.example.vaccineManagement.Controllers;

import com.example.vaccineManagement.Entity.Vial;
import com.example.vaccineManagement.Repository.VialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/vial")
@CrossOrigin
public class VialController {

    @Autowired
    private VialRepository vialRepository;

    // Scan / Fetch single vial details by tracking barcode
    @GetMapping("/scan/{vialNumber}")
    public ResponseEntity<?> scanVial(@PathVariable String vialNumber) {
        System.out.println("-------> SCAN VIAL API HIT FROM FRONTEND! VIAL NO: " + vialNumber + " <-------");
        Optional<Vial> vialOpt = vialRepository.findByVialNumber(vialNumber);
        if (vialOpt.isPresent()) {
            return new ResponseEntity<>(vialOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error: Vial not found with tracking number: " + vialNumber, HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    private com.example.vaccineManagement.Services.VialService vialService;

    // Manually add Vials Endpoint
    @PostMapping("/addVial/{vaccineId}/{numberOfVials}")
    public ResponseEntity<String> addManualVials(@PathVariable int vaccineId, @PathVariable int numberOfVials) {
        try {
            String result = vialService.manuallyAddVials(vaccineId, numberOfVials);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
