package com.dust.sensor;

import model.DustReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DustRepository extends MongoRepository<DustReport,String> {

    List<DustReport> findByDens(double dens);
    List<DustReport> findByDate(LocalDateTime from, LocalDateTime to);

}
