package med.voll.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import med.voll.api.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final String ISSUER = "API Voll.med";

    @Value("${api.security.token.secret}")
    private String secret;
    public String gerarToken(Usuario usuario){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getLogin())
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(dataExpiracao())
                    .sign(algorithm);

        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            throw  new RuntimeException("Error ao gerar token jwt", exception);
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String tokenJWT){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    // specify an specific claim validations
                    .withIssuer(ISSUER)
                    // reusable verifier instance
                    .build()
                    .verify(tokenJWT)
                    .getSubject();

        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            throw  new RuntimeException("Token JWT inválido ou expirado: " + tokenJWT);
        }
    }
}
