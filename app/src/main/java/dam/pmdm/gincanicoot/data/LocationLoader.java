package dam.pmdm.gincanicoot.data;

import static dam.pmdm.gincanicoot.utils.Constants.FILE_JSON;
import static dam.pmdm.gincanicoot.utils.Constants.TAG;
import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Esta clase permite cargar puntos de interés desde un archivo JSON. El archivo JSON ha sido
 * generado haciendo uso de ChatGPT.
 * Proporciona métodos para parsear el JSON y convertirlo en objetos LocationPoint, que seran cargados
 * en memoria en una Lista.
 */
public class LocationLoader {
    /**
     * Carga una lista de puntos de interés desde un archivo JSON en los assets.
     *
     * @param context El contexto de la aplicación para acceder a los assets.
     * @return Lista de LocationPoint cargados, o null si ocurre un error.
     */
    public static List<LocationPoint> loadLocationsPointsFromJson(Context context) {
        try {
            InputStreamReader is = new InputStreamReader(context.getAssets().open(FILE_JSON));
            BufferedReader reader = new BufferedReader(is);
            Gson gson = new Gson();
            StringBuilder jsonString = new StringBuilder();
            String line;

            // Cargo JSON a memoria
            while((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();

            // Convierto JSON a lista
            Type locationListType = new TypeToken<List<LocationPoint>>(){}.getType();

            return gson.fromJson(jsonString.toString(), locationListType);

        } catch (Exception ex) {
            Log.e(TAG, "Error cargando las localizaciones:" + ex);
            return null;
        }
    }
}
