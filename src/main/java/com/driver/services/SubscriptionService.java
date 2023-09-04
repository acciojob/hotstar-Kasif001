package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int total_Screen = subscriptionEntryDto.getNoOfScreensRequired();

        LocalDate currentDate = LocalDate.now();
        Date date = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Subscription subscription = new Subscription();
        int totalAmmount = 0;
        if(subscriptionType.equals(SubscriptionType.BASIC)){
             totalAmmount = 500 + total_Screen*200;
            subscription = new Subscription(subscriptionType,total_Screen,date,totalAmmount);
        }else if(subscriptionType.equals(SubscriptionType.PRO)){
             totalAmmount = 800 + total_Screen*250;
             subscription = new Subscription(subscriptionType,total_Screen,date,totalAmmount);
        }else if (subscriptionType.equals(SubscriptionType.ELITE)){
             totalAmmount = 1000 + total_Screen*350;
             subscription = new Subscription(subscriptionType,total_Screen,date,totalAmmount);
        }

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        user.setSubscription(subscription);
        subscription.setUser(subscription.getUser());

        subscriptionRepository.save(subscription);
        userRepository.save(user);
        return totalAmmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        User user = userRepository.findById(userId).get();
        if(user.getSubscription().equals(SubscriptionType.ELITE)){
            throw new Exception ("Already the best Subscription");
        }
        Subscription subscription = new Subscription();
        int newAmmount = 0;
        int prevAmmount = 0;

        if(user.getSubscription().equals(SubscriptionType.BASIC)){
            prevAmmount = user.getSubscription().getTotalAmountPaid();
            newAmmount = 800 + user.getSubscription().getNoOfScreensSubscribed()*250;
            subscription = new Subscription(SubscriptionType.PRO,user.getSubscription().getNoOfScreensSubscribed(),user.getSubscription().getStartSubscriptionDate(),newAmmount);

        }else if(user.getSubscription().equals(SubscriptionType.PRO)){
            prevAmmount = user.getSubscription().getTotalAmountPaid();
            newAmmount = 1000 + user.getSubscription().getNoOfScreensSubscribed()*350;
            subscription = new Subscription(SubscriptionType.PRO,user.getSubscription().getNoOfScreensSubscribed(),user.getSubscription().getStartSubscriptionDate(),newAmmount);

        }
        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        return newAmmount-prevAmmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int revenue = 0;
        for(Subscription subscription: subscriptionList){
            revenue+=subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
