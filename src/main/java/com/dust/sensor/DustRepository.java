package com.dust.sensor;

import model.DustReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DustRepository extends MongoRepository<DustReport,String> {

    List<DustReport> findAllByDateBetween(LocalDateTime from, LocalDateTime to);

    DustReport findTopByOrderByDateDesc();

}
