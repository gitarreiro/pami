package mainapp.mimomusic.de.missionchuckhole.data;

        import com.google.android.gms.maps.model.LatLng;
        import com.google.maps.android.clustering.ClusterItem;

/*
 Created by Marwan on 18/06/2016.
 */
public class MyItem implements ClusterItem {

    private final LatLng mPosition;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}