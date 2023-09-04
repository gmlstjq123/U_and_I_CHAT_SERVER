package chrome.com.chat.user.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Query("select p from Profile p where p.user.id = :userId")
    Optional<Profile> findProfileById(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Profile p where p.user.id = :userId")
    void deleteProfileById(@Param("userId") Long userId);
}
