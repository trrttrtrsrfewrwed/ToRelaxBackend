package backend.repository;

import backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    @Query("select u from User u where u.email = :email")
    User getByEmail(@Param("email") String email);

    @Query("select count(u) from User u where u.email = :email")
    int countByEmail(@Param("email") String email);
}
