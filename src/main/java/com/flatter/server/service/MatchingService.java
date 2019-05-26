package com.flatter.server.service;

import com.flatter.server.domain.Offer;
import com.flatter.server.domain.User;
import com.flatter.server.repository.OfferRepository;
import com.flatter.server.web.feign.FeignMatchClient;
import domain.QuestionnaireableOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class MatchingService {

    private final FeignMatchClient feignMatchClient;

    private SecureRandom secureRandom;

    private final OfferRepository offerRepository;

    private final JoiningService joiningService;

    @Autowired
    public MatchingService(OfferRepository offerRepository, FeignMatchClient feignMatchClient, JoiningService joiningService) {
        this.offerRepository = offerRepository;
        this.secureRandom = new SecureRandom();
        this.feignMatchClient = feignMatchClient;
        this.joiningService = joiningService;
    }

    public List<Offer> getMockOffers(User user) {
        int limit = secureRandom.nextInt(10);
        ArrayList<Offer> offers = (ArrayList<Offer>) offerRepository.findAll();
        LinkedList<Offer> returnOffers = new LinkedList<>();

        for (int i = 0; i < limit; i++) {
            returnOffers.add(offers.get(secureRandom.nextInt(offers.size())));
        }

        return returnOffers;
    }

    public List<QuestionnaireableOffer> getOffersOfUser(User user) {
        return feignMatchClient.getMatchesOfUser(user.getLogin());
    }

    public void postJoiningRequest(String login) {
        joiningService.postRequestForClusteringData(login);
    }

}
