package mx.com.openpay.marvel.app;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MarvelAppImpl implements MarvelApp{

    private String llavePublica;

    private String llavePrivada;

    private WebClient webClient;
    
    private URI apiUrl;

    private Logger log = LogManager.getLogger(this.getClass());
    
    public MarvelAppImpl(String url, String llavePublica, String llavePrivada) {
        this.apiUrl = URI.create(url);
        this.llavePrivada = llavePrivada;
        this.llavePublica = llavePublica;
        this.webClient = WebClient.builder().build();
    }

    @SuppressWarnings("null")
    @Override
    public String getCharacters() {
        try {
            Long timestamp = System.currentTimeMillis();

            StringBuilder cadenaOriginal = new StringBuilder(timestamp.toString());
            cadenaOriginal.append(llavePrivada);
            cadenaOriginal.append(llavePublica);

            StringBuilder uriBuilder = new StringBuilder();
            uriBuilder.append(apiUrl.toString());
            uriBuilder.append("?apikey=").append(llavePublica);
            uriBuilder.append("&ts=").append(timestamp.toString());
            uriBuilder.append("&hash=").append(generarHash(cadenaOriginal.toString()));

            String response = webClient.get()
                    .uri(uriBuilder.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return "Ocurrió un error al realizar la petición.";
        }
    }

    /**
     * 
     * @param cadenaOriginal
     * @return
     */
    private String generarHash(String cadenaOriginal) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        
            messageDigest.update(cadenaOriginal.getBytes(StandardCharsets.UTF_8));
            byte[] hash = messageDigest.digest();
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(),e);
            return "";
        }
    }

}
