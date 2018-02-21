package com.saeniel.citypicker;

import java.io.Serializable;

/**
 * Created by Saeniel on 21.02.2018.
 */

public class City implements Serializable {

    String name, description, area, population, image;

    public City() {
    }

    public City(String name, String description, String area, String population, String image) {
        this.name = name;
        this.description = description;
        this.area = area;
        this.population = population;
        this.image = image;
    }
}
