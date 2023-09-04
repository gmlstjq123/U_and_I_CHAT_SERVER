package chrome.com.chat.user.profile;

import chrome.com.chat.user.User;
import chrome.com.chat.user.profile.dto.GetS3Res;
import chrome.com.chat.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final S3Service s3Service;

    @Transactional
    public void saveProfile(GetS3Res getS3Res, User user){
        Profile profile;
        if(getS3Res.getImgUrl() != null) {
            profile = Profile.builder()
                    .profileUrl(getS3Res.getImgUrl())
                    .profileFileName(getS3Res.getFileName())
                    .user(user)
                    .build();
            profileRepository.save(profile);
        }
    }

    @Transactional
    public void deleteProfile(Profile profile) {
        s3Service.deleteFile(profile.getProfileFileName());
    }

    @Transactional
    public void deleteProfileById(Long memberId) {
        profileRepository.deleteProfileById(memberId);
    }
}
