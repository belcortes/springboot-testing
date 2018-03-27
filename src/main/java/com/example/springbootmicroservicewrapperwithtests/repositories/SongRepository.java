package com.example.springbootmicroservicewrapperwithtests.repositories;

import com.example.springbootmicroservicewrapperwithtests.models.Song;
import org.springframework.data.repository.CrudRepository;

public interface SongRepository extends CrudRepository<Song, Long> {

}