package chrome.com.chat.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.id = :id")
    Optional<User> findUserById(@Param("id") Long id);

    @Query("select u from User u where u.uid = :uid")
    Optional<User> findUserByUid(@Param("uid") String uid);

    @Query("select count(u) from User u where u.email = :email")
    Integer findByEmailCount(@Param("email") String email);

    Optional<User> findByEmail(String email); // // JPA 제공 메서드

    @Modifying
    @Query("delete from User u where u.id = :userId")
    void deleteUser(@Param("userId") Long userId);
}


