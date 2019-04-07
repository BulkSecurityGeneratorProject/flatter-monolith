package com.flatter.server.web.rest;

import com.flatter.server.domain.Offer;
import com.flatter.server.domain.dto.OffersDetailsDTO;
import com.flatter.server.repository.OfferRepository;
import com.flatter.server.repository.PhotoRepository;
import com.flatter.server.web.rest.errors.BadRequestAlertException;
import com.flatter.server.web.rest.util.HeaderUtil;
import com.flatter.server.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing Offer.
 */
@RestController
@RequestMapping("/api")
public class OfferResource {

    private final Logger log = LoggerFactory.getLogger(OfferResource.class);

    private static final String ENTITY_NAME = "offer";

    private final OfferRepository offerRepository;

    private final PhotoRepository photoRepository;

    public OfferResource(OfferRepository offerRepository, PhotoRepository photoRepository) {
        this.offerRepository = offerRepository;
        this.photoRepository = photoRepository;
    }

    /**
     * POST  /offers : Create a new offer.
     *
     * @param offer the offer to create
     * @return the ResponseEntity with status 201 (Created) and with body the new offer, or with status 400 (Bad Request) if the offer has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/offers")
    public ResponseEntity<Offer> createOffer(@Valid @RequestBody Offer offer) throws URISyntaxException {
        log.debug("REST request to save Offer : {}", offer);
        if (offer.getId() != null) {
            throw new BadRequestAlertException("A new offer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Offer result = offerRepository.save(offer);
        return ResponseEntity.created(new URI("/api/offers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /offers : Updates an existing offer.
     *
     * @param offer the offer to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated offer,
     * or with status 400 (Bad Request) if the offer is not valid,
     * or with status 500 (Internal Server Error) if the offer couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/offers")
    public ResponseEntity<Offer> updateOffer(@Valid @RequestBody Offer offer) throws URISyntaxException {
        log.debug("REST request to update Offer : {}", offer);
        if (offer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Offer result = offerRepository.save(offer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, offer.getId().toString()))
            .body(result);
    }

    /**
     * GET  /offers : get all the offers.
     *
     * @param pageable the pagination information
     * @param filter   the filter of the request
     * @return the ResponseEntity with status 200 (OK) and the list of offers in body
     */
    @GetMapping("/offers")
    public ResponseEntity<List<Offer>> getAllOffers(Pageable pageable, @RequestParam(required = false) String filter) {
        if ("address-is-null".equals(filter)) {
            log.debug("REST request to get all Offers where address is null");
            return new ResponseEntity<>(StreamSupport
                .stream(offerRepository.findAll().spliterator(), false)
                .filter(offer -> offer.getAddress() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        if ("album-is-null".equals(filter)) {
            log.debug("REST request to get all Offers where album is null");
            return new ResponseEntity<>(StreamSupport
                .stream(offerRepository.findAll().spliterator(), false)
                .filter(offer -> offer.getAlbum() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        if ("match-is-null".equals(filter)) {
            log.debug("REST request to get all Offers where match is null");
            return new ResponseEntity<>(StreamSupport
                .stream(offerRepository.findAll().spliterator(), false)
                .filter(offer -> offer.getMatch() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        log.debug("REST request to get a page of Offers");
        Page<Offer> page = offerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/offers");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /offers/:id : get the "id" offer.
     *
     * @param id the id of the offer to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the offer, or with status 404 (Not Found)
     */
    @GetMapping("/offers/{id}")
    public ResponseEntity<Offer> getOffer(@PathVariable Long id) {
        log.debug("REST request to get Offer : {}", id);
        Optional<Offer> offer = offerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(offer);
    }

    /**
     *  GET /offfers-with-details/:id get the "id" offer.
     * @param id  the id of the offer to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the offer and all photos, or with status 404 (Not Found)
     */
    @GetMapping("/offers-with-details/{id}")
    public ResponseEntity<OffersDetailsDTO> getOfferWithDetails(@PathVariable Long id) {
        log.debug("REST request to get Offer : {}", id);
        Optional<Offer> offer = offerRepository.findById(id);

        OffersDetailsDTO offersDetailsDTO = new OffersDetailsDTO();

        if (offer.isPresent()) {
            offersDetailsDTO.setOffer(offer.get());
            offersDetailsDTO.setPhotos(photoRepository.getAllByAlbum(offer.get().getAlbum()));
        }

        return ResponseEntity.ok(offersDetailsDTO);
    }

    /**
     * DELETE  /offers/:id : delete the "id" offer.
     *
     * @param id the id of the offer to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/offers/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        log.debug("REST request to delete Offer : {}", id);
        offerRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
