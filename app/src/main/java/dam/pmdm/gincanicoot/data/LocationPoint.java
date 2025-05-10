package dam.pmdm.gincanicoot.data;

import com.google.android.gms.maps.model.Marker;

/**
 * Clase que representa un punto de interés en el mapa con su ubicación y desafío asociado.
 * Contiene información sobre la posición geográfica, el estado de completado y el marcador asociado.
 */
public class LocationPoint {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String challengeTitle;
    private String challengeDescription;
    private String challengeAnswer;
    private boolean isCompleted;
    private Marker marker;


    /**
     * Constructor para crear un nuevo punto de interés.
     * Ademas de los parametros, hay un atributo mas:
     *      isCompleted que permite mientras la app esta en memoria saber si el reto de una localizacion
     *      ha sido superado. Esto permite controlar que si el jugador quiere volver a resolver un reto
     *      ya resuelto, no haga nada.
     *
     * @param id Identificador único del punto.
     * @param name Titulo general de la localizacion.
     * @param latitude Latitud geográfica.
     * @param longitude Longitud geográfica.
     * @param challengeTitle Título del desafío asociado.
     * @param challengeDescription Descripción del desafío.
     * @param challengeAnswer Respuesta correcta del desafío.
     */
    public LocationPoint(int id, String name, double latitude, double longitude, String challengeTitle, String challengeDescription, String challengeAnswer) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.challengeTitle = challengeTitle;
        this.challengeDescription = challengeDescription;
        this.challengeAnswer = challengeAnswer;
        this.isCompleted = false;
    }

    /**
     * Indica si el desafío asociado a este punto ha sido completado.
     *
     * @return true si el desafío está completado, false en caso contrario.
     */
    public boolean isCompleted() {
        return isCompleted;
    }


    /**
     * Establece el estado de completado del desafío.
     *
     * @param completed true para marcar como completado, false para marcar como no completado.
     */
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * Obtiene el ID del punto.
     *
     * @return El identificador único del punto.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del punto.
     *
     * @param id El nuevo identificador único.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del punto.
     *
     * @return El nombre del punto.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del punto.
     *
     * @param name El nuevo nombre del punto.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la latitud geográfica del punto.
     *
     * @return La latitud en grados decimales.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Establece la latitud geográfica del punto.
     *
     * @param latitude La nueva latitud en grados decimales.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Obtiene la longitud geográfica del punto.
     *
     * @return La longitud en grados decimales.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Establece la longitud geográfica del punto.
     *
     * @param longitude La nueva longitud en grados decimales.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Obtiene el título del desafío asociado.
     *
     * @return El título del desafío.
     */
    public String getChallengeTitle() {
        return challengeTitle;
    }

    /**
     * Establece el título del desafío asociado.
     *
     * @param challengeTitle El nuevo título del desafío.
     */
    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }


    /**
     * Obtiene la descripción del desafío.
     *
     * @return La descripción del desafío.
     */
    public String getChallengeDescription() {
        return challengeDescription;
    }


    /**
     * Establece la descripción del desafío.
     *
     * @param challengeDescription La nueva descripción del desafío.
     */
    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
    }

    /**
     * Obtiene la respuesta correcta del desafío.
     *
     * @return La respuesta correcta.
     */
    public String getChallengeAnswer() {
        return challengeAnswer;
    }

    /**
     * Establece la respuesta correcta del desafío.
     *
     * @param challengeAnswer La nueva respuesta correcta.
     */
    public void setChallengeAnswer(String challengeAnswer) {
        this.challengeAnswer = challengeAnswer;
    }

    /**
     * Obtiene el marcador asociado a este punto en el mapa.
     *
     * @return El objeto Marker asociado, o null si no está asignado.
     */
    public Marker getMarker() {
        return marker;
    }

    /**
     * Establece el marcador asociado a este punto en el mapa.
     *
     * @param marker El nuevo objeto Marker a asociar.
     */
    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
