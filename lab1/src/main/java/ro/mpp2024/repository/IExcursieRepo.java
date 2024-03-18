package ro.mpp2024.repository;

import ro.mpp2024.domain.Excursie;

import java.time.LocalTime;

public interface IExcursieRepo extends IRepository<Integer, Excursie>{
    Iterable<Excursie> findBeetwenHours(LocalTime ora1, LocalTime ora2);
}
