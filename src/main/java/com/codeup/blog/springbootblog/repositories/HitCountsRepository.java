package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.HitCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HitCountsRepository extends CrudRepository<HitCount, Long> {

    @Query(nativeQuery = true, value="SELECT count FROM hitcounts hc WHERE hc.post_id LIKE ?1")
    int hitCountByPost(Long id);
}
