package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        if(!webSeriesList.isEmpty()){
            for(WebSeries webSeries: webSeriesList){
                if(webSeries.getSeriesName().equals(webSeriesEntryDto.getSeriesName())){
                    throw new Exception("Series is already present");
                }
            }
        }


        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(),webSeriesEntryDto.getAgeLimit(),webSeriesEntryDto.getRating(),webSeriesEntryDto.getSubscriptionType());
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        List<WebSeries> newWebList = productionHouse.getWebSeriesList();
        newWebList.add(webSeries);

        int noOfWebSeries = productionHouse.getWebSeriesList().size();
        double rating = productionHouse.getRatings();

        double newRating = ((noOfWebSeries*rating)+webSeries.getRating())/(noOfWebSeries+1);

        webSeries.setProductionHouse(productionHouse);
        productionHouse.setWebSeriesList(newWebList);
        productionHouse.setRatings(newRating);


        productionHouseRepository.save(productionHouse);
        webSeriesRepository.save(webSeries);

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        return 123;
    }

}
