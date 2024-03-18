package mx.com.openpay.marvel.app;

public interface MarvelApp {

    /**
     * Método GET para realizar el consumo de los personajes de MARVEL
     * Se utiliza una llave pública y una privada para realiza el consumo
     * La URL está parametrizada desde el cliente.
     * @return la lista de personajes en formato JSON
     */
    public String getCharacters();
    
}
