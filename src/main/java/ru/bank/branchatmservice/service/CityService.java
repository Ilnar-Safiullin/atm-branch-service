package ru.bank.branchatmservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bank.branchatmservice.exception.CityNotFoundException;
import ru.bank.branchatmservice.model.City;
import ru.bank.branchatmservice.repository.CityRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public City getCityByName(String nameCity){
        return cityRepository.findByName(nameCity)
                .orElseThrow(() -> new CityNotFoundException(
                        String.format("Город с наименованием %s не найден", nameCity)
                )
        );
    }

    public City getCityById(UUID cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new CityNotFoundException(
                        String.format("Город с id %s не найден!", cityId))
                );
    }
}
