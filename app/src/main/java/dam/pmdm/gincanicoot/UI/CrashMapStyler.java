package dam.pmdm.gincanicoot.UI;

import static dam.pmdm.gincanicoot.utils.Constants.SIZE_MARKER;
import static dam.pmdm.gincanicoot.utils.Constants.TAG;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import android.animation.ValueAnimator;

import dam.pmdm.gincanicoot.R;

/**
 * En esta clase voy a aplicar los estilos y los efectos relacionados con el juego Crash Bandicoot
 * al mapa de Google.
 * Aspectos que modifico: Marcadores, Rutas, Animaciones y Snackbars de resultados.
 */
public class CrashMapStyler {
    private final Context context;
    private final GoogleMap googleMap;
    private MediaPlayer soundSuccess;
    private MediaPlayer soundFailure;

    /**
     * Constructor que inicializa los recursos usados en la clase
     * @param context El contexto de la actividad
     * @param googleMap El mapa de Google a personalizar
     */
    public CrashMapStyler(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        initialiseSounds();
    }

    /**
     * Inicializa los recursos de sonido. Son usados en las respuestas del dialogo de retos.
     */
    private void initialiseSounds() {
        soundSuccess = MediaPlayer.create(context, R.raw.woah);
        soundFailure = MediaPlayer.create(context, R.raw.akuaku);
    }

    /**
     * Libera recursos cuando ya no se necesitan
     */
    public void releaseResources() {
        if (soundSuccess != null) {
            soundSuccess.release();
            soundSuccess = null;
        }
        if (soundFailure != null) {
            soundFailure.release();
            soundFailure = null;
        }
    }

    /**
     * Se lleva a cabo la aplicacion de un estilo base temático al mapa
     * @return true si se aplicó correctamente
     */
    public boolean applyCrashStyleToMap() {
        try {
            boolean exit = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.crash_map_style));

            if (!exit) {
                Log.e(TAG, "(applyCrashStyleToMap) -> No ha funcionado aiiiii");
            }
            return exit;
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Igual encuentro mi cabeza porque el archivo de estilo.... pues va a ser que no!", e);
            return false;
        }
    }

    /**
     * Crea un mark temático en el mapa
     * @param position Coordenadas del mark
     * @param title Título del mark
     * @param idResourceIcon ID del recurso drawable
     * @return El mark creado
     */
    public Marker addThemeMarker(LatLng position, String title,
                                 @DrawableRes int idResourceIcon) {
        BitmapDescriptor icono = resizeMarkerIcon(idResourceIcon, SIZE_MARKER);

        MarkerOptions options = new MarkerOptions()
                .position(position)
                .title(title)
                .icon(icono);

        return googleMap.addMarker(options);
    }

    /**
     * Redimensiona un icono para usar como mark
     * @param idResource ID del recurso drawable
     * @param sizeDp Tamaño deseado en DP
     * @return BitmapDescriptor redimensionado
     */
    public BitmapDescriptor resizeMarkerIcon(@DrawableRes int idResource, int sizeDp) {
        Bitmap OriginalImage = BitmapFactory.decodeResource(context.getResources(), idResource);

        // Convertir DP a píxeles
        float density = context.getResources().getDisplayMetrics().density;
        int sizePx = (int) (sizeDp * density);

        Bitmap resizedImage = Bitmap.createScaledBitmap(
                OriginalImage, sizePx, sizePx, false);

        return BitmapDescriptorFactory.fromBitmap(resizedImage);
    }

    /**
     * Muestra un Snackbar personalizado con éxito
     * @param view La view raíz donde mostrar el Snackbar
     * @param message El message a mostrar
     */
    public void showSnackbarResult(View view, String message, boolean result) {
        if (soundSuccess != null && soundFailure != null) {
            (result ? soundSuccess : soundFailure).start();
        }

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        // Usar el API moderno para personalizar
        snackbar.setBackgroundTint(context.getResources().getColor(R.color.crash_primary));
        snackbar.setTextColor(context.getResources().getColor(R.color.crash_text_primary));

        try {
            int layoutID = result ? R.layout.snackbar_crash_success : R.layout.snackbar_crash_failure;
            customiseSnackbarWithView(snackbar, layoutID, message);
        } catch (Exception e) {
            Log.e(TAG, "Error al personalizar Snackbar: " + e.getMessage());
            // La personalización básica ya está hecha con los métodos anteriores
        }
        snackbar.show();
    }

    /**
     * Método auxiliar para personalizar un Snackbar con una view personalizada
     * @param snackbar El Snackbar a personalizar
     * @param layoutId El ID del layout personalizado
     * @param message El message a mostrar
     */
    private void customiseSnackbarWithView(Snackbar snackbar, int layoutId, String message) {
        //  view personalizada
        View customView = LayoutInflater.from(context).inflate(layoutId, null);
        TextView txtmessage = customView.findViewById(R.id.txt_mensaje_snackbar);
        txtmessage.setText(message);

        // Obtener la view del Snackbar
        View snackbarView = snackbar.getView();

        // Enfoque 2: Encontrar el ViewGroup dentro del Snackbar
        ViewGroup viewGroup = (ViewGroup) snackbarView;
        viewGroup.removeAllViews(); // Eliminar las views existentes
        viewGroup.addView(customView); // Añadir nuestra view personalizada
    }


    /**
     * Crea una ruta temática entre dos puntos. Le da un aspecto semejante al de los videojuegos.
     * @param points Lista de coordenadas para la ruta
     * @return La polilínea creada
     */
    public Polyline createRouteTheme(List<LatLng> points) {
        // Definir patrón de línea (estilo cajas de Crash)
        List<PatternItem> patron = Arrays.asList(
                new com.google.android.gms.maps.model.Dash(30),  // Línea
                new com.google.android.gms.maps.model.Gap(20),   // Espacio
                new com.google.android.gms.maps.model.Dot(),            // Puntos
                new com.google.android.gms.maps.model.Gap(20)    // Espacio
        );

        // Crear la polilínea
        PolylineOptions optionsRoute = new PolylineOptions()
                .addAll(points)
                .width(12f)
                .color(context.getResources().getColor(R.color.crash_primary))
                .pattern(patron)
                .geodesic(true);

        Polyline route = googleMap.addPolyline(optionsRoute);

        // Animar la route
        animateRoute(route, points);

        return route;
    }

    /**
     * Anima la aparición progresiva de una route
     * @param polyline La polilínea a animar
     * @param points Los pointss completos de la route
     */
    private void animateRoute(final Polyline polyline, final List<LatLng> points) {
        ValueAnimator animator = ValueAnimator.ofInt(0, points.size());
        animator.setDuration(2000); // 2 segundos para la animación completa
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                List<LatLng> currentPoints = points.subList(0, Math.max(1, progress));
                polyline.setPoints(currentPoints);
            }
        });

        animator.start();
    }

    /**
     * Anima la cámara con estilo temático de Crash
     * @param destination Coordenadas del destination
     * @param destinationMarker mark opcional que rebotará al finalizar
     */
    public void animateCameraCrashStyle(LatLng destination, final Marker destinationMarker) {
        // Crear posición de cámara con efecto "Crash"
        CameraPosition positionCamera = new CameraPosition.Builder()
                .target(destination)
                .zoom(17f)          // Nivel de zoom
                .bearing(45f)       // Rotación
                .tilt(60f)          // Inclinación
                .build();

        // Animar cámara con efecto de rebote
        googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(positionCamera),
                2500,  // 2.5 segundos
                new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        // Reproducir sonido al terminar
                        MediaPlayer soundWoah = MediaPlayer.create(
                                context, R.raw.woah);
                        soundWoah.start();
                        soundWoah.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });

                        // Opcional: hacer rebotar el mark en esa posición
                        if (destinationMarker != null) {
                            bounceMarker(destinationMarker);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // Nada que hacer.... toi'aburriooooooo
                    }
                }
        );
    }

    /**
     * Hace que un mark rebote con animación
     * @param mark El mark a animar
     */
    public void bounceMarker(final Marker mark) {
        final Handler handler = new Handler();
        final long initTime = SystemClock.uptimeMillis();
        final long duration = 1500; // 1.5 segundos

        final BounceInterpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsedTime = SystemClock.uptimeMillis() - initTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsedTime / duration), 0);

                // Ajustar el points de anclaje para simular rebote
                mark.setAnchor(0.5f, 1.0f + 1.5f * t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16); // Aprox. 60fps
                }
            }
        });
    }
}
