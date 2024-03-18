package mx.com.openpay.marvel.services;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MarvelServiceImpl implements MarvelService{

    private URI apiUrl;

    @Value("${marvel.llave.publica}")
    private String llavePublica;

    @Value("${marvel.llave.privada}")
    private String llavePrivada;

    @Autowired
    private WebClient webClient;

    @Autowired
    public MarvelServiceImpl(String url) {
        this.apiUrl = URI.create(url);
    }

    @SuppressWarnings("null")
    @Override
    public String getCharacters() {
        try {
            Long timestamp = System.currentTimeMillis();

            StringBuilder cadenaOriginal = new StringBuilder();
            cadenaOriginal.append(timestamp.toString());
            cadenaOriginal.append(llavePrivada);
            cadenaOriginal.append(llavePublica);

            StringBuilder uriBuilder = new StringBuilder();
            uriBuilder.append(apiUrl.toString());
            uriBuilder.append("?apiKey=").append(llavePublica);
            uriBuilder.append("&ts=").append(timestamp);
            uriBuilder.append("&hash=").append(generarHash(cadenaOriginal.toString()));

            String response = webClient.get()
                    .uri(uriBuilder.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return response;
        } catch (Exception e) {
            return "Ocurrió un error al realizar la petición.";
        }
    }

    /**
     * 
     * @param cadenaOriginal
     * @return
     */
    private String generarHash(String cadenaOriginal) {

        try {
            // Crear instancia de MessageDigest para MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Obtener el arreglo de bytes del texto
            byte[] bytes = cadenaOriginal.toString().getBytes();

            // Calcular el hash MD5
            byte[] hashBytes = md.digest(bytes);

            // Convertir el arreglo de bytes a formato hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return new String();
        }
    }

}
