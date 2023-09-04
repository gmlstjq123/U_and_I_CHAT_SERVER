package chrome.com.chat.user.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetS3Res {
    private String imgUrl;
    private String fileName;
}
