package chrome.com.chat.jwt;

import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponseStatus;
import chrome.com.chat.user.User;
import chrome.com.chat.user.dto.PostReissueReq;
import chrome.com.chat.utils.Secret;
import chrome.com.chat.utils.UtilService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtService {
    private Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(Secret.JWT_SECRET_KEY));
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;
    private final UtilService utilService;

    public String getJwt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("Authorization");
    }

    /**
     * JWT에서 userId 추출
     */
    public Long getUserIdx() throws BaseException {
        // 1. JWT 추출
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(BaseResponseStatus.EMPTY_JWT);
        }
        if (checkBlackToken(accessToken)) {
            throw new BaseException(BaseResponseStatus.LOG_OUT_USER);
        }
        try {
            // 2. JWT parsing
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
            // 3. userId 추출
            Long userId = claims.getBody().get("userId", Long.class);
            User user = utilService.findByUserIdWithValidation(userId);

            return userId;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT);
        } catch (Exception ignored) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT);
        }
    }

    /**
     * 로그아웃 전용 userId 추출 메서드
     */
    // 로그아웃을 시도할 때는 accsee token과 refresh 토큰이 만료되었어도
    // 형식만 유효하다면 토큰 재발급 없이 로그아웃 할 수 있어야 함.
    public String getLogoutUserIdx() throws BaseException {

        // 1. JWT 추출
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(BaseResponseStatus.EMPTY_JWT);
        }
        if (checkBlackToken(accessToken)) {
            throw new BaseException(BaseResponseStatus.LOG_OUT_USER);
        }
        try {
            // 2. JWT parsing
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
            claims.getBody().get("userId", Long.class);

            // access token이 만료되지 않은 경우
            Long expiration = jwtProvider.getExpiration(accessToken);
            // access token의 만료 시간을 TTL로 하여 Redis Cache에 저장
            redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
            return "로그아웃 되었습니다.";
        } catch (ExpiredJwtException e) {
            // access token이 만료된 경우
            return "로그아웃 되었습니다.";
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT);
        } catch (Exception ignored) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT);
        }
    }

    /**
     * 토큰의 만료 여부를 판별
     */
    public Boolean checkExpiration() throws BaseException {
        // 1. JWT 추출
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(BaseResponseStatus.EMPTY_JWT);
        }
        if (checkBlackToken(accessToken)) {
            throw new BaseException(BaseResponseStatus.LOG_OUT_USER);
        }
        try {
            // 2. JWT parsing
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
            // 3. userId 추출
            return false; // 유효
        } catch (ExpiredJwtException e) {
            // access token 만료
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT);
        } catch (Exception ignored) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT);
        }
    }

    /**
     * 액세스 토큰 재발급
     */
    public String refreshAccessToken(PostReissueReq postReissueReq) throws BaseException {
        try {
            String refreshToken = postReissueReq.getRefreshToken();
            String uid = postReissueReq.getUid();
            // 리프레시 토큰이 만료 등의 이유로 유효하지 않은 경우
            if (!jwtProvider.validateToken(refreshToken)) {
                throw new BaseException(BaseResponseStatus.INVALID_JWT);
            }
            else { // 리프레시 토큰이 유효한 경우
                User user = utilService.findByUserUidWithValidation(uid);
                Long userId = user.getId();
                String refreshedAccessToken = jwtProvider.createToken(userId);
                // 액세스 토큰 재발급에 성공한 경우
                if (refreshedAccessToken != null) {
                    return refreshedAccessToken;
                }
                throw new BaseException(BaseResponseStatus.FAILED_TO_REFRESH);
            }
        } catch (BaseException exception) {
            throw new BaseException(exception.getStatus());
        }
    }

    /**
     * Redis 블랙 리스트 등록 여부 확인
     */
    private boolean checkBlackToken(String accessToken) {
        // Redis에 있는 엑세스 토큰인 경우 로그아웃 처리된 엑세스 토큰이다.
        Object redisToken = redisTemplate.opsForValue().get(accessToken);
        if (redisToken != null) { // Redis에 저장된 토큰이면 블랙토큰
            return true;
        }
        return false;
    }
}
