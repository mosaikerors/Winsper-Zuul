package com.mosaiker.winserzuul.repository;

import com.mosaiker.winserzuul.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserByUId(Long uId);

}
