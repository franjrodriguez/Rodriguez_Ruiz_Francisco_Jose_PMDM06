package dam.pmdm.gincanicoot;

import static dam.pmdm.gincanicoot.utils.Constants.SIZE_MARKER;
import static dam.pmdm.gincanicoot.utils.Constants.TAG;
import static dam.pmdm.gincanicoot.utils.Constants.HEIGHT_ICON_GAMER;
import static dam.pmdm.gincanicoot.utils.Constants.WIDTH_ICON_GAMER;
import static dam.pmdm.gincanicoot.utils.Constants.STEPS_GAMER;
import static dam.pmdm.gincanicoot.utils.Constants.POST_DELAYED;
import static dam.pmdm.gincanicoot.utils.Constants.ZOOM_CAMERA;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import dam.pmdm.gincanicoot.UI.CrashMapStyler;
import dam.pmdm.gincanicoot.data.LocationLoader;
import dam.pmdm.gincanicoot.data.LocationPoint;

/**
 * Principal Activity del proyecto que maneja Google Maps y la interacción del usuario con los marcadores.
 * Permite visualizar puntos de interés, realizar desafíos y animar el movimiento del jugador.
 */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private CrashMapStyler mapStyler;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    public GoogleMap myMap;
    public ActionBar actionBar;
    private List<LocationPoint> locationPoints;
    private List<Marker> markers = new ArrayList<>();
    private Marker playerMarker;
    private FloatingActionButton fabTogglePlayer;
    private Polyline currentRoute;
    private boolean isPlayerMarkerInitialized = false;
    private int completedChallenges = 0;
    private int[] challengeImages = {
            R.drawable.crash_image_1, R.drawable.crash_image_2, R.drawable.crash_image_3,
            R.drawable.crash_image_4, R.drawable.crash_image_5, R.drawable.crash_image_6,
            R.drawable.crash_image_7, R.drawable.crash_image_8, R.drawable.crash_image_9,
            R.drawable.crash_image_10, R.drawable.crash_image_11, R.drawable.crash_image_12,
            R.drawable.crash_image_13, R.drawable.crash_image_14
    };

    private Handler animationHandler;
    private Runnable currentAnimation;

    /**
     * Método llamado cuando se crea la actividad.
     * Inicializa los componentes de la interfaz y carga el mapa.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.gincanicoot);
            updateActionBarCompletedChallenges();
        }

        fabTogglePlayer = findViewById(R.id.fab_toggle_player);
        fabTogglePlayer.setOnClickListener(v -> togglePlayerMarker());

        animationHandler = new Handler(Looper.getMainLooper());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.d(TAG, "Error al cargar el mapa de Google");
        }
    }

    /**
     * Actualiza el subtítulo de la ActionBar con el número de desafíos completados.
     */
    private void updateActionBarCompletedChallenges() {
        actionBar.setSubtitle(completedChallenges + getString(R.string.retos_resueltos));
    }


    /**
     * Muestra un diálogo con el desafío asociado a un punto de interés.
     *
     * @param point El punto de interés que contiene el desafío.
     */
    private void viewChallengeDialog(LocationPoint point) {
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(this, R.style.CrashDialog);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_crash_reto, null);

        ImageView imageView = dialogView.findViewById(R.id.challenge_image);
        TextView textTitle = dialogView.findViewById(R.id.title);
        TextView textChallenge = dialogView.findViewById(R.id.description);
        EditText inputCode = dialogView.findViewById(R.id.input_code);
        Button buttonSend = dialogView.findViewById(R.id.button_send);

        Random random = new Random();
        int randomIndex = random.nextInt(challengeImages.length);
        imageView.setImageResource(challengeImages[randomIndex]);

        textTitle.setText(point.getChallengeTitle());
        textChallenge.setText(point.getChallengeDescription());

        inputCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                buttonSend.setEnabled(!s.toString().trim().isEmpty());
            }
        });

        AlertDialog dialog = builderDialog.setView(dialogView).create();

        // Verifico la entrada del codigo
        buttonSend.setOnClickListener(v -> {
            String code = inputCode.getText().toString().trim();
            // Respuesta valida...
            if (code.equals(point.getChallengeAnswer())) {
                point.setCompleted(true);
                completedChallenges++;
                updateActionBarCompletedChallenges();
                updateMarkerIcon(point);
                showSnackbar(getString(R.string.completed), true);
            } else {
                // Respuesta invalida...
                showSnackbar(getString(R.string.answer_invalid), false);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Actualiza el icono de un marcador en el mapa, solo en el caso de que el desafio halla sido
     * correctamente superado.
     *
     * @param point El punto de interés asociado al marcador.
     */
    private void updateMarkerIcon(LocationPoint point) {
        Marker marker = point.getMarker();
        if (marker != null) {
            Log.d(TAG, "(updateMarkerIcon) -> Actualizando el icono para " + point.getName() + ", completado: " + point.isCompleted());
            marker.setIcon(mapStyler.resizeMarkerIcon(
                    point.isCompleted() ? R.drawable.fruta : R.drawable.box,
                    SIZE_MARKER));
        } else {
            Log.w(TAG, "(updateMarkerIcon) -> No se encontro el marcador para el punto: " + point.getName());
            for (Marker mk : markers) {
                if (mk.getTitle().equals(point.getName())) {
                    Log.d(TAG, "(updateMarkerIcon) -> Marcados encontrado en la lista para " + point.getName());
                    mk.setIcon(mapStyler.resizeMarkerIcon(
                            point.isCompleted() ? R.drawable.fruta : R.drawable.box,
                            SIZE_MARKER));
                    point.setMarker(mk);
                    break;
                }
            }
        }
    }

    /**
     * Muestra un Snackbar con un mensaje en funcion del parametro result que me indica si la respuesta
     * del jugador a sido éxito o fracaso.
     *
     * @param message El mensaje a mostrar.
     * @param result Indica si el resultado es exitoso (true) o no (false).
     */
    public void showSnackbar(String message, boolean result) {
        View rootView = findViewById(android.R.id.content);
        if (rootView == null) return;

        mapStyler.showSnackbarResult(rootView, message, result);
    }

    /**
     * Muestra un Snackbar con un mensaje.
     *
     * @param message El mensaje a mostrar.
     */
    public void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        if (rootView == null) return;

        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);

        // Vamos a tunear el snackbar
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.crash_background));
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.crash_text_secondary));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(ResourcesCompat.getFont(this, R.font.anime_ace));

        snackbar.show();
    }

    /**
     * Este método es llamado cuando el mapa está listo para ser utilizado.
     * Configura el estilo del mapa, solicita permisos y carga los puntos de interés.
     *
     * @param googleMap El objeto GoogleMap listo para usar.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady llamado");
        myMap = googleMap;
        mapStyler = new CrashMapStyler(this, myMap);
        mapStyler.applyCrashStyleToMap();
        showSnackbar(getString(R.string.googlemaps_connected));

        // Se solicitan los permisos pertinentes al usuario y se posiciona en el mapa
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return; // espera a que el usuario responda
        }

        try {
            myMap.setMyLocationEnabled(true);
            mostrarCrashJugador();
        } catch (SecurityException e) {
            Log.e(TAG, "onMapReady -> Error al habilitar la ubicacion: " + e.getMessage());
            showSnackbar(getString(R.string.error_location_user));
        }

        // Carga la lista de puntos del mapa desde archvo JSON
        locationPoints = LocationLoader.loadLocationsPointsFromJson(this);

        // Posiciona los puntos sabiendo que el mapa esta preparado
        if (locationPoints != null && !locationPoints.isEmpty()) {
            Log.d(TAG, "Puntos cargados: " + locationPoints.size());
            for (LocationPoint point : locationPoints) {
                LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                mapStyler.addThemeMarker(
                        latLng,
                        point.getName(),
                        point.isCompleted() ? R.drawable.fruta : R.drawable.box
                );
                Marker marker = mapStyler.addThemeMarker(
                        latLng,
                        point.getName(),
                        point.isCompleted() ? R.drawable.fruta : R.drawable.box
                );
                point.setMarker(marker);
                markers.add(marker);
                Log.d(TAG, "(onMapReady) -> Marcador asignado a " + point.getName() + ", marcador> " + marker);
            }

            myMap.setOnMarkerClickListener(marker -> {
                if (locationPoints != null) {
                    for (LocationPoint point : locationPoints) {
                        if (point.getName().equals(marker.getTitle())) {
                            marker.showInfoWindow();            // Mostrar el título corto (Etiqueta)

                            // Posiciono la camara sobre el objjetivo
                            LatLng targetPosition = new LatLng(point.getLatitude(), point.getLongitude());
                            mapStyler.animateCameraCrashStyle(targetPosition, marker);

                            // Reposiciono sobre el jugador para que le siga durante su movimiento
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                animatePlayerToLocation(targetPosition);
                            }, 2500); // Este valor debe coincidir con la duración de la animación de cámara

                            return true;
                        }
                    }
                }
                Log.d(TAG, "onMapReady -> Marcador presionado sin éxito: " + marker.getTitle());
                marker.showInfoWindow();
                return false;
            });

            myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoContents(@NonNull Marker marker) {
                    try {
                        View view = getLayoutInflater().inflate(R.layout.info_window, null);
                        TextView title = view.findViewById(android.R.id.text1);
                        title.setText(marker.getTitle());
                        return view;
                    } catch (Exception e) {
                        Log.e(TAG, "getInfoContents -> No se ha podido cargar info_window.xml: " + e.getMessage());
                        return null;
                    }
                }
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    return null;
                }
            });

            myMap.setOnInfoWindowClickListener(marker -> {
                if (locationPoints != null) {
                    for (LocationPoint point : locationPoints) {
                        if (point.getName().equals(marker.getTitle())) {
                            viewChallengeDialog(point);
                            return;
                        }
                    }
                }
            });
        }
        showSnackbar(getString(R.string.a_divertirse));
    }

    /**
     * En este metodo llevo a cabo la animacion del marcador del jugador hacia una ubicación objetivo.
     *
     * @param targetPos La ubicación objetivo a la que se moverá el jugador.
     */
    private void animatePlayerToLocation(LatLng targetPos) {
        if (playerMarker == null) {
            showSnackbar(getString(R.string.location_not_found));
            return;
        }

        // Detengo animaciones anteriores
        if (currentAnimation != null) {
            animationHandler.removeCallbacks(currentAnimation);
        }

        LatLng startPos = playerMarker.getPosition();
        Log.d(TAG, "(animatePlayerToLocation -> Inicio en " + startPos.latitude + ", " + startPos.longitude +
                " -> Objetivo en " + targetPos.latitude + ", " + targetPos.longitude);

        // Crear la ruta temática entre la posición inicial y destino
        createThematicRoute(startPos, targetPos);

        // Comienzo a calcular para reposicionar al jugador
        final double totalLatDiference = targetPos.latitude - startPos.latitude;
        final double totalLngDiference = targetPos.longitude - startPos.longitude;
        int steps = STEPS_GAMER;    // Se definen los pasos que debe dar. A mayor valor irá más despacio en el mapa
        final long animationDuration = POST_DELAYED;
        final int[] currentStep = {0};

        currentAnimation = new Runnable() {
            @Override
            public void run() {
                if (currentStep[0] < steps) {
                    // Interpolación
                    float progress = (float) currentStep[0] / steps;

                    // Nueva posición teniendo en cuenta la interpolación
                    double newLat = startPos.latitude + (totalLatDiference * progress);
                    double newLng = startPos.longitude + (totalLngDiference * progress);
                    LatLng newPos = new LatLng(newLat, newLng);

                    // Actualizo posición
                    playerMarker.setPosition(newPos);

                    // Actualizo la cámara por si fuera necesario
                    myMap.animateCamera(CameraUpdateFactory.newLatLng(newPos));

                    // Siguiente paso
                    currentStep[0]++;
                    animationHandler.postDelayed(this, animationDuration);
                } else {
                    playerMarker.setPosition(targetPos);
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetPos, ZOOM_CAMERA));

                    // Cuando termine la animación, hacer que rebote el marcador
                    if (mapStyler != null) {
                        mapStyler.bounceMarker(playerMarker);
                    }

                    // Opcional: Reproducir sonido de éxito al llegar
                    showRouteCompletedEffect(targetPos);
                }
            }
        };

        animationHandler.post(currentAnimation);
    }

    /**
     * Crea una ruta temática entre dos puntos en el mapa
     * @param start Punto de inicio
     * @param end Punto final
     */
    private void createThematicRoute(LatLng start, LatLng end) {
        // Si hay una ruta anterior, la eliminamos
        if (currentRoute != null) {
            currentRoute.remove();
        }

        // Crear la lista de puntos para la ruta
        List<LatLng> routePoints = new ArrayList<>();
        routePoints.add(start);

        double latStep = (end.latitude - start.latitude) / 4;
        double lngStep = (end.longitude - start.longitude) / 4;

        for (int i = 1; i < 4; i++) {
            routePoints.add(new LatLng(
                    start.latitude + (latStep * i),
                    start.longitude + (lngStep * i)
            ));
        }

        routePoints.add(end);

        // Usar el mapStyler para crear la ruta temática
        currentRoute = mapStyler.createRouteTheme(routePoints);
    }

    /**
     * Muestra efectos visuales cuando se completa la ruta
     * @param destination Indica la posición final de la tura
     */
    private void showRouteCompletedEffect(LatLng destination) {
        // Buscar el punto de interés en este destino
        for (LocationPoint point : locationPoints) {
            LatLng pointLatLng = new LatLng(point.getLatitude(), point.getLongitude());
            float[] distance = new float[1];

            // Calcular distancia entre el destino y el punto de interés
            Location.distanceBetween(
                    destination.latitude, destination.longitude,
                    pointLatLng.latitude, pointLatLng.longitude,
                    distance);

            // Si estamos cerca del punto (menos de 50 metros)
            if (distance[0] < 50) {
                // Mostrar efecto de llegada
                if (mapStyler != null) {
                    View rootView = findViewById(android.R.id.content);
                    mapStyler.showSnackbarResult(rootView,
                            "¡Has llegado a " + point.getName() + "!",
                            true);
                }
                break;
            }
        }
    }

    /**
     * Muestra el marcador del jugador en su ubicación actual.
     */
    private void mostrarCrashJugador() {
        if (isPlayerMarkerInitialized || playerMarker != null) {
            Log.d(TAG, "(mostrarCrashJugador) -> playerMarker ya existe, no se reinicia");
            return;
        }
        Log.d(TAG, "(mostrarCrashJugador) -> iniciado");
        isPlayerMarkerInitialized = true;
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "mostrarCrashJugador -> Permisos de ubicacion no otorgados");
            showSnackbar(getString(R.string.toggle_on_permission));
            isPlayerMarkerInitialized = false;
            return;
        }

        // Intento leer la ultima ubicacion conocida
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null && myMap != null) {
                Log.d(TAG, "mostrarCrashJugador -> Ubicacion obtenida");
                LatLng playerPos = new LatLng(location.getLatitude(), location.getLongitude());
                BitmapDescriptor icon = scaleBitmap(R.drawable.userlocation, WIDTH_ICON_GAMER,HEIGHT_ICON_GAMER);
                playerMarker = myMap.addMarker(new MarkerOptions()
                        .position(playerPos)
                        .title(getString(R.string.chrash_is_here))
                        .icon(icon));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(playerPos, 16f));
            } else {
                Log.w(TAG, "mostrarChrusJugador -> Ultima ubicacion NO disponible, solicitando nueva");
                requestNewLocation(fusedLocationClient);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "mostrarCrashJugador -> Error al obtener la ubicacion: " + e.getMessage());
            showSnackbar(getString(R.string.error_getting_user_position));
            isPlayerMarkerInitialized = false;
        });
    }

    /**
     * Alterna la visibilidad del marcador del jugador utilizando el boton flotante.
     */
    private void togglePlayerMarker() {
        if (playerMarker != null) {
            if (playerMarker.isVisible()) {
                playerMarker.setVisible(false);
                fabTogglePlayer.setImageResource(android.R.drawable.ic_menu_myplaces);
                showSnackbar(getString(R.string.location_gamer_off));
            } else {
                playerMarker.setVisible(true);
                fabTogglePlayer.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                showSnackbar(getString(R.string.location_gamer_on));
            }
        } else {
            showSnackbar(getString(R.string.location_gamer_not_found));
        }
    }

    /**
     * Solicita una nueva ubicación del jugador.
     *
     * @param fusedLocationClient Cliente para obtener la ubicación.
     */
    private void requestNewLocation(FusedLocationProviderClient fusedLocationClient) {
        if (isPlayerMarkerInitialized || playerMarker != null) {
            Log.d(TAG, "(requestNewLocation) -> playerMarker ya existe, no se reinicia");
            return;
        }
        Log.d(TAG, "(requestNewLocation) -> inciado");
        isPlayerMarkerInitialized = true;
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000)
                .setNumUpdates(1);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null && myMap != null) {
                    Log.d(TAG, "Nueva ubicación obtenida: " + location.getLatitude() + ", " + location.getLongitude());
                    LatLng playerPos = new LatLng(location.getLatitude(), location.getLongitude());
                    BitmapDescriptor icon = scaleBitmap(R.drawable.userlocation, WIDTH_ICON_GAMER,HEIGHT_ICON_GAMER);
                    playerMarker = myMap.addMarker(new MarkerOptions()
                            .position(playerPos)
                            .title(getString(R.string.chrash_is_here))
                            .icon(icon));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(playerPos, 16f));
                } else {
                    Log.e(TAG, getString(R.string.location_is_null));
                    showSnackbar(getString(R.string.error_location_request_activation));
                    isPlayerMarkerInitialized = false;
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(TAG, "Error al solicitar nueva ubicación: " + e.getMessage());
            showSnackbar(getString(R.string.error_location_user));
            isPlayerMarkerInitialized = false;
        }
    }

    /**
     * Despues de no lograr de otro modo que escalara adecuadamente las imagenes del juego,
     * termine por desarrollar este metodo. Escala un bitmap para usarlo como icono de marcador.
     *
     * @param resourceId ID del recurso drawable.
     * @param width Ancho deseado del icono.
     * @param height Alto deseado del icono.
     * @return BitmapDescriptor del icono escalado.
     */
    private BitmapDescriptor scaleBitmap(int resourceId, int width, int height){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap);
    }

    /**
     * Maneja la respuesta a la solicitud de permisos para acceder a Google Maps.
     *
     * @param requestCode Código de la solicitud.
     * @param permissions Permisos solicitados.
     * @param grantResults Resultados de la concesión de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, ahora puedes continuar con el acceso a la ubicación
                mostrarCrashJugador();
            } else {
                // Permiso denegado, muestra un mensaje
                showSnackbar(getString(R.string.error_location_user));
            }
        }
    }

    /**
     * Método llamado cuando la actividad es destruida.
     * Libera recursos y detiene animaciones.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Limpio recursos
        if (currentAnimation != null) {
            animationHandler.removeCallbacks(currentAnimation);
        }
        mapStyler.releaseResources();
    }
}