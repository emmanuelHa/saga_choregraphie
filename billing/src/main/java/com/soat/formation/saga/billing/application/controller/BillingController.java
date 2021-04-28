package com.soat.formation.saga.billing.application.controller;

// TODO useless
//@RestController
public class BillingController {

    /*@Autowired
    BillingRepository billingRepository;

    @PostMapping(value = "/billings")
    public ResponseEntity<Billing> saveBilling(@RequestBody Billing Billing) {
        Billing newBilling = billingRepository.save(Billing);
        if(newBilling == null) {
            throw new ImpossibleAddBillingException("Impossible d'ajouter cette Billing");
        }
        return new ResponseEntity<>(Billing, HttpStatus.CREATED);
    }

    @GetMapping(value = "/billings/{id}")
    public Optional<Billing> getBilling(@PathVariable int id) {

        Optional<Billing> Billing = billingRepository.findById(id);

        if(Billing.isEmpty()) throw new BillingNotFoundException("Cette Billing n'existe pas");

        return Billing;
    }*/

}
